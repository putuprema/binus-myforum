package xyz.purema.binusmyforum.domain.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

object ActivityUtils {
    /**
     * Go to other activity
     *
     * @param ctx          Currrent activity
     * @param destActivity Destination activity
     */
    fun goToActivity(ctx: Context, destActivity: Class<out AppCompatActivity?>?) {
        val intent = Intent(ctx, destActivity)
        ctx.startActivity(intent)
    }

    /**
     * Set custom background for status bar
     */
    fun setStatusBarBackground(activity: Activity, resourceId: Int) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
        activity.window.setBackgroundDrawable(activity.resources.getDrawable(resourceId))
    }

    fun displayProgressBar(mProgressBar: ProgressBar, displayed: Boolean) {
        mProgressBar.visibility = if (displayed) View.VISIBLE else View.GONE
    }
}