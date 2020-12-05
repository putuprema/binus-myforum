package xyz.purema.binusmyforum.ui.dialog

import android.app.Activity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.purema.binusmyforum.R

class LoadingDialog(
    activity: Activity,
    text: String = "Loading..."
) {
    private var alertDialog: AlertDialog
    private var loadingTextView: TextView

    var text: String = text
        set(value) {
            loadingTextView.text = value
            field = value
        }

    init {
        val loadingDlgView = activity.layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingTextView = loadingDlgView.findViewById(R.id.loading_text)
        loadingTextView.text = text

        alertDialog = MaterialAlertDialogBuilder(activity, R.style.ThemeOverlay_App_AlertDialog)
            .setCancelable(false)
            .setView(loadingDlgView)
            .create()
    }

    fun show() {
        alertDialog.show()
    }

    fun dismiss() {
        alertDialog.dismiss()
    }
}