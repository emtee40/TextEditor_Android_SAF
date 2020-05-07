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

            when {
                !datasource.hasActiveFile() -> {
                    view.setTitle("untitled")
                    view.setContent("")
                }
            }

            view.loaderVisible(false)
        }
    }

    fun new(contentSize: Int) {
        datasource.getLastSaveSize{ saveSize, error ->
            when {
                error != null -> {
                    view.showError(error)
                }
                else -> {
                    if (saveSize == contentSize) {
                        setupNew()
                    } else {
                        view.confirmNew()
                    }
                }
            }
        }
    }

    fun newOverride() = setupNew()

    private fun setupNew(){
        datasource.clear()
        view.setTitle("Untitled")
        view.setContent("")
        view.setMarkdownButtonVisisbility(false)
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
        when {
            datasource.hasActiveFile() -> {
                //existing file
                datasource.saveCurrent(content, {error ->
                    view.showError(error)
                }){
                    view.showMessage("$filename saved")
                }
            }
            else -> {
                //new file
                view.createNewFile()
            }
        }
    }

    fun fileCreated(uri: Uri, flags: Int, content: String) {
        datasource.setCurrent(uri, flags, content){ success: Boolean, message: String?, filename: String? ->
            when {
                success -> {
                    view.setTitle(filename)
                    view.showMessage("$filename created")
                }
                else -> {
                    view.showError(message)
                }
            }
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