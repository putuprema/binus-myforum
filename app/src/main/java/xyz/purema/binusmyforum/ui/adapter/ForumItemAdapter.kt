package xyz.purema.binusmyforum.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.domain.model.forum.GslcForum
import xyz.purema.binusmyforum.domain.utils.DateUtils
import xyz.purema.binusmyforum.ui.contract.ForumReplyActivityContract
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ForumItemAdapter(
    private val ctx: AppCompatActivity,
    private val sharedPrefs: SharedPrefs
) : RecyclerView.Adapter<ForumItemAdapter.ViewHolder>() {
    private val gslcForumThreads: MutableList<GslcForum> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var courseNameView: TextView = itemView.findViewById(R.id.course_name)
        var classNameView: TextView = itemView.findViewById(R.id.class_name)
        var forumSubjectView: TextView = itemView.findViewById(R.id.forum_subject)
        var forumDeadlineView: TextView = itemView.findViewById(R.id.forum_deadline)
        var forumRepliedIndicatorView: LinearLayout =
            itemView.findViewById(R.id.forum_replied_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(ctx).inflate(R.layout.forum_item, parent, false)
        return ViewHolder(v)
    }

    @ExperimentalCoroutinesApi
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forum = gslcForumThreads[position]

        holder.courseNameView.text = forum.course
        holder.classNameView.text = "${forum.classCode} - ${forum.classType}"
        holder.forumSubjectView.text =
            forum.forumThread?.subject ?: ctx.getString(R.string.no_forum)
        holder.forumDeadlineView.text = ctx.getString(
            R.string.forum_deadline_text,
            DateUtils.formatDate(forum.dueDate),
            ChronoUnit.DAYS.between(LocalDate.now(), forum.dueDate).toString()
        )

        if (forum.forumThread?.replyMessage == null) {
            holder.forumRepliedIndicatorView.visibility = View.GONE
        } else {
            holder.forumRepliedIndicatorView.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            val gslc = gslcForumThreads[position]

            if (gslc.forumThread == null) {
                Toast.makeText(ctx, ctx.getString(R.string.no_forum_toast), Toast.LENGTH_SHORT)
                    .show()
            } else {
                forumReplyActivity.launch(gslc.forumThread)
            }
        }
    }

    override fun getItemCount(): Int = gslcForumThreads.size

    fun updateData(newData: List<GslcForum>) {
        gslcForumThreads.clear()
        gslcForumThreads.addAll(newData)
        notifyDataSetChanged()
    }

    @ExperimentalCoroutinesApi
    private val forumReplyActivity =
        ctx.registerForActivityResult(ForumReplyActivityContract()) { shouldPromptAppReview ->
            if (shouldPromptAppReview) {
                val reviewManager = ReviewManagerFactory.create(ctx)

                // request in-app review flow
                reviewManager.requestReviewFlow().addOnCompleteListener {
                    if (it.isSuccessful) {
                        // launch the in-app review flow
                        val reviewInfo = it.result
                        reviewManager.launchReviewFlow(ctx, reviewInfo).addOnCompleteListener {
                            sharedPrefs.lastReviewPopup = LocalDate.now()
                        }
                    }
                }
            }
        }
}