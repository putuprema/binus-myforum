package xyz.purema.binusmyforum.domain.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

object TextUtils {
    fun renderHtml(htmlString: String): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT)
        }
        return Html.fromHtml(htmlString)
    }
}