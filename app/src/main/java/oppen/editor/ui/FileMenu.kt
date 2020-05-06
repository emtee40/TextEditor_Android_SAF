package oppen.editor.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.PopupMenu
import delay
import oppen.editor.R

class FileMenu(private val activity: Activity, anchor: View, onNew: () -> Unit, onSave: () -> Unit) {

    private val fileMenu = PopupMenu(anchor.context, anchor)

    init {
        fileMenu.inflate(R.menu.file_menu)
        fileMenu.setOnMenuItemClickListener { menuItem ->
            activity.delay(250) {
                when (menuItem.itemId) {
                    R.id.menu_action_new -> onNew.invoke()
                    R.id.menu_action_open -> openFile(activity)
                    R.id.menu_action_save -> onSave.invoke()
                }
            }
            Thread.sleep(200)//Give the ripple effect a chance to complete
            return@setOnMenuItemClickListener true
        }
    }

    fun show() = fileMenu.show()

    companion object{
        const val REQUEST_ID_OPEN = 102

        fun openFile(activity: Activity){
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "*/*"
            activity.startActivityForResult(intent, REQUEST_ID_OPEN)
        }
    }
}