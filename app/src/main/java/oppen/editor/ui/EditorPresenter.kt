package oppen.editor.ui

import android.net.Uri
import oppen.editor.io.FileDatasource

class EditorPresenter(
    private val view: EditorView,
    private val datasource: FileDatasource) {

    fun initialise(){
        view.loaderVisible(true)
        datasource.openLastFile { filename, content, error ->
            view.setTitle(filename)
            view.setContent(content)
            view.showError(error)

            checkMarkdown(filename)

            if(!datasource.hasActiveFile()){
                view.setTitle("untitled")
                view.setContent("")
            }

            view.loaderVisible(false)
        }
    }

    fun new() {

    }

    fun open(uri: Uri, flags: Int) {
        view.loaderVisible(true)
        datasource.openFile(uri, flags){ filename: String?, content: String?, error: String? ->
            view.setTitle(filename)
            view.setContent(content)
            view.showError(error)
            view.loaderVisible(false)
            checkMarkdown(filename)

        }
    }

    fun save(filename: String, content: String) {
        if(datasource.hasActiveFile()){
            //existing file
            datasource.saveCurrent(content, {error ->
                view.showError(error)
            }){
                view.showMessage("$filename saved")
            }
        }else{
            //new file
        }
    }

    private fun checkMarkdown(filename: String?){
        if(filename == null) return

        when {
            filename.toLowerCase().endsWith(".md") -> {
                view.setMarkdownButtonVisisbility(true)
            }
            else -> {
                view.setMarkdownButtonVisisbility(false)
            }
        }
    }
}