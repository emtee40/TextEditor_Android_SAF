package oppen.editor.ui.edit_text.syntax_highlighter

import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import java.util.regex.Pattern

class MarkdownHighlighter: Highlighter {

    companion object {
        const val DEFAULT = 0
        const val INNER = 1
    }

    private val red = Color.parseColor("#d55f51")
    private val vividBlue = Color.parseColor("#4065c1")
    private val mutedBlue = Color.parseColor("#3983b7")
    private val brown = Color.parseColor("#916922")
    private val mauve = Color.parseColor("#98369f")
    private val lightGray = Color.parseColor("#fefefe")
    private val codeGreen = Color.parseColor("#659e59")

    private var start = 0
    private var end = 0

    private val headerScheme = Highlighter.Scheme(Pattern.compile("#+\\s.*\\n"), red, null, Typeface.BOLD, DEFAULT)
    private val inlineCodeScheme = Highlighter.Scheme(Pattern.compile("`.*`"), codeGreen, lightGray, null, DEFAULT)
    private val blockCodeScheme = Highlighter.Scheme(Pattern.compile("(```[a-z]*\\n[\\s\\S]*?\\n```)"), codeGreen, lightGray, null, DEFAULT)
    private val linkTitleScheme = Highlighter.Scheme(Pattern.compile("\\[(.*?)\\]"), vividBlue, null, null, INNER)
    private val linkTargetScheme = Highlighter.Scheme(Pattern.compile("\\((.*?)\\)"), mutedBlue, null, null, INNER)
    private val italicScheme = Highlighter.Scheme(Pattern.compile("_(.*?)_"), mauve, null, Typeface.ITALIC, DEFAULT)
    private val boldScheme = Highlighter.Scheme(Pattern.compile("\\*\\*.*\\*\\*"), brown, null, Typeface.BOLD, DEFAULT)
    private val listScheme = Highlighter.Scheme(Pattern.compile("[\\*]"), red, null, null, DEFAULT)

    private val schemes = mutableListOf<Highlighter.Scheme>()

    init {
        schemes.add(listScheme)
        schemes.add(inlineCodeScheme)
        schemes.add(blockCodeScheme)
        schemes.add(headerScheme)
        schemes.add(linkTitleScheme)
        schemes.add(linkTargetScheme)
        schemes.add(italicScheme)
        schemes.add(boldScheme)
    }

    override fun setup(editable: Editable?) {
        //
    }

    override fun process(editable: Editable?) {
        if(editable == null) return

        //Remove previous highlighting spans
        val foregroundColorSpans = editable.getSpans(0, editable.toString().length, ForegroundColorSpan::class.java)
        for(span in foregroundColorSpans){
            editable.removeSpan(span)
        }

        val backgroundColorSpans = editable.getSpans(0, editable.toString().length, BackgroundColorSpan::class.java)
        for(span in backgroundColorSpans){
            editable.removeSpan(span)
        }

        val styleSpans = editable.getSpans(0, editable.toString().length, StyleSpan::class.java)
        for(span in styleSpans){
            editable.removeSpan(span)
        }

        for(scheme in schemes){
            val matcher = scheme.pattern.matcher(editable)
            while (matcher.find()) {
                when (INNER) {
                    scheme.highlightType -> {
                        start = matcher.start() + 1
                        end = matcher.end() - 1
                    }
                    else -> {
                        start = matcher.start()
                        end = matcher.end()
                    }
                }

                if(scheme.foregroundColorSpan != null) editable.setSpan(ForegroundColorSpan(scheme.foregroundColorSpan), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if(scheme.backgroundColorSpan != null) editable.setSpan(BackgroundColorSpan(scheme.backgroundColorSpan), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if(scheme.textStyleSpan != null) editable.setSpan(StyleSpan(scheme.textStyleSpan), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}