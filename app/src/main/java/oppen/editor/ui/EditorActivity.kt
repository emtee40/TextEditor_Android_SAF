package oppen.editor.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_editor.*
import oppen.editor.R
import oppen.editor.io.FileDatasource
import oppen.editor.ui.about.AboutBottomsheet
import oppen.editor.ui.markdown.MarkdownBottomsheet
import setVisible

class EditorActivity : AppCompatActivity(), EditorView {

    private lateinit var presenter: EditorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        setupKeyboardVisibilityListener()

        setSupportActionBar(bottom_app_bar)

        bottom_app_bar.setNavigationOnClickListener {
            FileMenu(this, hidden_file_menu_anchor,{
                presenter.new(edit_textt.text?.length ?: 1)
            }){
                presenter.save(document_title.text.toString(), edit_textt.text.toString())
            }.show()
        }

        markdown_preview_button.setOnClickListener {
            MarkdownBottomsheet(edit_textt.text.toString()).show(supportFragmentManager, "markdown_dialog")
        }

        presenter = EditorPresenter(this, FileDatasource(this, contentResolver)).apply {
            initialise()
        }
    }

    override fun setTitle(title: String?) = runOnUiThread {
        document_title.text = title ?: "No Filename"
    }

    override fun setContent(content: String?) = runOnUiThread {
        edit_textt.setText(content ?: "")
    }

    override fun setMarkdownButtonVisisbility(visible: Boolean) = runOnUiThread {
        markdown_preview_button.setVisible(visible)
    }

    override fun showError(error: String?) = runOnUiThread {
        if (error != null) Snackbar.make(root, error, Snackbar.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String?) = runOnUiThread {
        if (message != null) Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun loaderVisible(visible: Boolean) = runOnUiThread {
        progress.setVisible(visible)
    }

    override fun confirmNew() = runOnUiThread {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Unsaved Changes")
            .setMessage("You have unsaved changes")
            .setNegativeButton("Cancel"){ _, _ -> }
            .setPositiveButton("Discard"){ _, _ ->
                presenter.newOverride()
            }.show()
    }

    /**
     * todo - find safer way of detecting keyboard state
     * This is a nasty hack to change view states when keyboard is shown/hidden
     */
    private fun setupKeyboardVisibilityListener(){
        root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            root.getWindowVisibleDisplayFrame(r)
            val heightDiff = root.rootView.height - (r.bottom - r.top)
            when {
                heightDiff > root.rootView.height * 0.35 -> bottom_app_bar.setVisible(false)
                else -> bottom_app_bar.setVisible(true)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            FileMenu.REQUEST_ID_OPEN -> {
                data?.data?.also { uri ->
                    presenter.open(uri, data.flags)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_action_about -> showAbout()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    private fun showAbout(){
        AboutBottomsheet().show(supportFragmentManager, "about_dialog")
    }
}