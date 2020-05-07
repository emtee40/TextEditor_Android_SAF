package oppen.editor.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.about_dialog.view.*
import oppen.editor.R


class AboutBottomsheet: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.about_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.markwon_apache_license_button.setOnClickListener {
            openBrowser("https://www.apache.org/licenses/LICENSE-2.0")
        }

        view.gnu_license_button.setOnClickListener {
            openBrowser("https://www.gnu.org/licenses/gpl-3.0.html")
        }
    }

    private fun openBrowser(url: String){
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }
}