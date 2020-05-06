package oppen.editor.ui.edit_text

import android.text.Editable
import android.text.TextWatcher

class EditWatcher(val onEdit: (e: Editable?) -> Unit): TextWatcher {
    override fun afterTextChanged(editable: Editable?) = onEdit(editable)
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}