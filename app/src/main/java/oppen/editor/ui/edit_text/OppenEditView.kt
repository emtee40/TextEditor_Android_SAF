package oppen.editor.ui.edit_text

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import oppen.editor.ui.edit_text.syntax_highlighter.Highlighter

class OppenEditView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatEditText(context, attrs) {

    private var highlighter: Highlighter? = null
    private var lineNumbering: LineNumbering? = null

    init {
        lineNumbering = LineNumbering(this)
        addTextChangedListener(EditWatcher { editable ->
            highlighter?.process(editable)
        })
    }

    override fun onDraw(canvas: Canvas?) {
        lineNumbering?.drawLineNumbers(canvas)
        super.onDraw(canvas)
    }

    fun setHighlighter(highlighter: Highlighter?) {
        this.highlighter = highlighter
        highlighter?.setup(text)
        highlighter?.process(text)
    }

    fun toggleLineNumbers() {
        lineNumbering?.toggle()
    }
}