package oppen.editor.ui.markdown

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import kotlinx.android.synthetic.main.markdown_dialog.view.*
import oppen.editor.R


class MarkdownBottomsheet(private val markdown: String): BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.markdown_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                view.markdown_textview.setTextColor(Color.BLACK)
            }
            Configuration.UI_MODE_NIGHT_YES,Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                view.markdown_textview.setTextColor(Color.WHITE)
            }
            else -> {
                view.markdown_textview.setTextColor(Color.WHITE)
            }
        }

        Markwon.builder(view.context).build().apply {
            setMarkdown(view.markdown_textview, markdown)
        }
    }

    override fun onStart() {
        super.onStart()

        val view = dialog?.findViewById<View>(R.id.design_bottom_sheet)
        view?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
    }
}