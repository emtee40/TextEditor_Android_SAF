package oppen.editor.ui.edit_text

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Selection
import android.widget.EditText
import asColor
import dpToPx
import oppen.editor.R

class LineNumbering(private val view: EditText) {

    private var marginSize = 0
    private var lineNumberYFudge = 0
    private var caretLineOffset = 0
    private val lineBoundsRect = Rect()
    private val paint = Paint()
    private var lineNumberPaddingDrawable: Drawable? = null

    private var active = true

    init {
        lineNumberYFudge = 4.dpToPx()

        paint.color = "#FF16161D".asColor()

        paint.textSize = view.textSize
        paint.textAlign = Paint.Align.RIGHT

        marginSize = paint.measureText("123").toInt()

        lineNumberPaddingDrawable = view.context.getDrawable(R.drawable.editor_line_number_padding)
        val bounds = lineNumberPaddingDrawable?.bounds
        bounds?.left = 0
        bounds?.right = bounds!!.left + marginSize + 8.dpToPx()
        lineNumberPaddingDrawable?.bounds = bounds
        view.setCompoundDrawables(lineNumberPaddingDrawable, null, null, null)
    }

    fun turnOn(){
        view.setCompoundDrawables(lineNumberPaddingDrawable, null, null, null)
        active = true
    }

    fun turnOff(){
        view.setCompoundDrawables(null, null, null, null)
        active = false
    }

    fun toggle() = when {
        active -> {
            turnOff()
        }
        else -> {
            turnOn()
        }
    }

    fun drawLineNumbers(canvas: Canvas?){
        if(!active) return

        var lineNumber = 1
        caretLineOffset = 0
        for (i in 0 until view.lineCount) {

            view.getLineBounds(i, lineBoundsRect)

            val caretLine = getCurrentCursorLine()
            val caretLineWithOffset = caretLine + caretLineOffset

            when (lineNumber) {
                caretLineWithOffset -> paint.color = Color.GRAY
                else -> paint.color = Color.LTGRAY
            }

            val num = (lineNumber).toString()

            if (i == 0) {
                canvas?.drawText(num, marginSize.toFloat(), lineBoundsRect.bottom.toFloat() - lineNumberYFudge, paint)
                lineNumber++
            } else if (view.text[view.layout.getLineStart(i) - 1] == '\n') {
                canvas?.drawText(num, marginSize.toFloat(), lineBoundsRect.bottom.toFloat() - lineNumberYFudge, paint)
                if(view.lineHeight == lineBoundsRect.height()) lineNumber++
            }else{
                caretLineOffset--
                paint.color = Color.LTGRAY
                canvas?.drawText("•", marginSize.toFloat(), lineBoundsRect.bottom.toFloat() - lineNumberYFudge, paint)
            }
        }
    }

    private fun getCurrentCursorLine(): Int {
        if(view.layout == null) return -1

        val selectionStart = Selection.getSelectionStart(view.text)
        return when {
            selectionStart != -1 -> view.layout.getLineForOffset(selectionStart) + 1
            else -> 1
        }
    }
}