package oppen.editor.io

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import okio.BufferedSource
import okio.buffer
import okio.source
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

    fun openFile(uri: Uri?, onFileReady: (filename: String?, content: String?, error: String?) -> Unit) {
        openFile(uri, null, onFileReady)
    }

    fun openFile(uri: Uri?, flags: Int?, onFileReady: (filename: String?, content: String?, error: String?) -> Unit) {

        if(uri == null){
            onFileReady(null, null, "Could not retrieve Uri")
            return
        }

        if(flags != null) {
            //Persist permission of this app to access the file:
            //https://developer.android.com/guide/topics/providers/document-provider.html#permissions
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
}