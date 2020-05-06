package oppen.editor.ui.edit_text.syntax_highlighter

import android.text.Editable
import java.util.regex.Pattern

interface Highlighter {

    fun setup(editable: Editable?)

    fun process(editable: Editable?)

    data class Scheme(
        val pattern: Pattern,
        val foregroundColorSpan: Int?,
        val backgroundColorSpan: Int?,
        val textStyleSpan: Int?,
        val highlightType: Int
    )

    companion object {
        fun findHighlighter(filename: String): Highlighter? {

            /**
             *
             * If a directory already contains foo.md and the user tries to create foo.md Android's SAF will instead create
             * 'foo.md (1)', and not 'foo (1).md' which complicates things a little (well, it's insane, thanks Google).
             *
             */
            return when {
                filename.endsWith(".md") -> MarkdownHighlighter()
                filename.endsWith(")") && filename.contains(".md") -> MarkdownHighlighter()
                else -> null
            }
        }
    }
}

