package oppen.editor.ui

interface EditorView {
    fun setTitle(title: String?)
    fun setContent(content: String?)
    fun setMarkdownButtonVisisbility(visible: Boolean)
    fun showError(error: String?)
    fun loaderVisible(visible: Boolean)
}