package oppen.editor.io

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Editable
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

const val PREFS_ACTIVE_DOCUMENT_URI = "oppen.editor.io.FileDatasource.PREFS_ACTIVE_DOCUMENT_URI"

class FileDatasource(
    context: Context,
    private val contentResolver: ContentResolver) {

    private var activeUri: Uri? = null

    private val prefs: SharedPreferences = context.getSharedPreferences("oppen_editor_prefs", Context.MODE_PRIVATE)

    fun openLastFile(onFileReady: (filename: String?, content: String?, error: String?) -> Unit){
        if(prefs.contains(PREFS_ACTIVE_DOCUMENT_URI)){
            activeUri = Uri.parse(prefs.getString(PREFS_ACTIVE_DOCUMENT_URI, ""))
            openFile(activeUri, onFileReady)
        }else{
            onFileReady(null, null, null)
        }
    }

    fun hasActiveFile(): Boolean = activeUri != null

    private fun openFile(uri: Uri?, onFileReady: (filename: String?, content: String?, error: String?) -> Unit) = openFile(uri, null, onFileReady)

    fun openFile(uri: Uri?, flags: Int?, onFileReady: (filename: String?, content: String?, error: String?) -> Unit) {

        if(uri == null){
            onFileReady(null, null, "Could not retrieve Uri")
            return
        }

        activeUri = uri

        if(flags != null) {
            //Persist permission of this app to access the file: https://developer.android.com/guide/topics/providers/document-provider.html#permissions
            val takeFlags: Int = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(uri, takeFlags)
        }

        val cursor: Cursor?
        try {
            cursor = contentResolver.query(uri, null, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                thread {
                    val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val source: BufferedSource? = contentResolver.openInputStream(uri)?.source()?.buffer()
                    val result = source?.readUtf8() ?: "Could not open file stream"
                    source?.close()
                    cursor.close()

                    prefs.edit().putString(PREFS_ACTIVE_DOCUMENT_URI, uri.toString()).apply()
                    onFileReady(displayName, result, null)
                }
            }
        }catch (exception: SecurityException){
            onFileReady(null, null, exception.toString())
        }
    }

    fun getLastSaveSize(onFileSize: (filesize: Int, error: String?) -> Unit){
        if(activeUri == null){
            onFileSize(-1, "No active Uri")
            return
        }
        val cursor: Cursor?
        try {
            cursor = contentResolver.query(activeUri!!, null, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                thread {
                    val fileBytes = cursor.getInt(cursor.getColumnIndex(OpenableColumns.SIZE))
                    cursor.close()
                    onFileSize(fileBytes, null)
                }
            }
        }catch (exception: SecurityException){
            onFileSize(-1, exception.toString())
        }
    }

    fun setCurrent(uri: Uri, flags: Int?, content: String, onNewSaved: (success: Boolean, message: String?, filename: String?) -> Unit) {
        activeUri = uri

        if(flags != null) {
            //Persist permission of this app to access the file: https://developer.android.com/guide/topics/providers/document-provider.html#permissions
            val takeFlags: Int = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(uri, takeFlags)
        }

        val cursor: Cursor?
        try {
            cursor = contentResolver.query(uri, null, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                thread {
                    val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor.close()
                    prefs.edit().putString(PREFS_ACTIVE_DOCUMENT_URI, uri.toString()).apply()

                    saveCurrent(content, {error ->
                        onNewSaved(false, error, null)
                    }){
                        onNewSaved(true, null, displayName)
                    }
                }
            }
        }catch (exception: SecurityException){
            onNewSaved(false, exception.toString(), null)
        }
    }

    fun saveCurrent(content: String, onError: (error: String) -> Unit, onSaved: () -> Unit) {
        if(activeUri == null) {
            onError("No url - need to generate new file?")
            return
        }
        try {
            contentResolver.openFileDescriptor(activeUri!!, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
                onSaved.invoke()
            }
        }catch(fnfe: FileNotFoundException){
            onError(fnfe.toString())
        }catch(ioe: IOException){
            onError(ioe.toString())
        }
    }

    fun clear(){
        prefs.edit().clear().apply()
        activeUri = null
    }


}