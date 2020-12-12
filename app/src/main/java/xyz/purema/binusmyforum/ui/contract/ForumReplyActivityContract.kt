package xyz.purema.binusmyforum.ui.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import xyz.purema.binusmyforum.ui.activity.ForumReplyActivity

@ExperimentalCoroutinesApi
class ForumReplyActivityContract : ActivityResultContract<ForumThread, Boolean>() {
    override fun createIntent(context: Context, input: ForumThread): Intent {
        return Intent(context, ForumReplyActivity::class.java).apply {
            putExtra("forum_thread", input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return intent?.getBooleanExtra("should_prompt_review", false) ?: false
    }
}