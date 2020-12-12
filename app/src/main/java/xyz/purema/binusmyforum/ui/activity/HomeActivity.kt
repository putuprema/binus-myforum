package xyz.purema.binusmyforum.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.WorkInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.domain.DataState
import xyz.purema.binusmyforum.domain.model.student.Student
import xyz.purema.binusmyforum.domain.utils.ActivityUtils
import xyz.purema.binusmyforum.ui.adapter.ForumItemAdapter
import xyz.purema.binusmyforum.ui.viewmodel.HomeViewEvent
import xyz.purema.binusmyforum.ui.viewmodel.HomeViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var mForumItemAdapter: ForumItemAdapter
    private lateinit var mForumRecyclerView: RecyclerView
    private lateinit var mHeaderMessage: TextView
    private lateinit var mSwipeRefreshContainer: SwipeRefreshLayout
    private lateinit var mLogoutButton: Button
    private lateinit var mLogoutDialog: AlertDialog
    private lateinit var mRefreshForumDialog: AlertDialog

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private var forumCacheUpdating: Boolean = false

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setStatusBarBackground(this, R.drawable.gradient_bg)
        setContentView(R.layout.activity_home)

        mHeaderMessage = findViewById(R.id.header_message)

        val student = intent.getParcelableExtra<Student>("student")
        if (student != null) {
            findViewById<TextView>(R.id.header_greeting).text =
                getString(R.string.header_greeting, student.name.split(" ")[0])
        }

        setupRefreshForumDialog()
        setupSwipeRefreshContainer()
        setupLogoutButtonAndDialog()
        setupRecyclerView()
        subscribeObservers()

        viewModel.publishEvent(HomeViewEvent.GetGslcForum)
    }

    override fun onResume() {
        super.onResume()
        viewModel.publishEvent(HomeViewEvent.GetGslcForum)
    }

    private fun setupRefreshForumDialog() {
        mRefreshForumDialog = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_AlertDialog)
            .setTitle(getString(R.string.forum_refresh_confirm_title))
            .setMessage(getString(R.string.forum_refresh_confirm_message))
            .setNeutralButton(getString(R.string.dialog_neutral_btn)) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(getString(R.string.dialog_positive_btn)) { _, _ ->
                viewModel.publishEvent(
                    HomeViewEvent.RefreshForumListCache
                )
            }
            .setOnCancelListener { mSwipeRefreshContainer.isRefreshing = false }
            .create()
    }

    private fun setupSwipeRefreshContainer() {
        mSwipeRefreshContainer = findViewById(R.id.forum_swipe_refresh_container)
        mSwipeRefreshContainer.setOnRefreshListener { mRefreshForumDialog.show() }
    }

    private fun setupLogoutButtonAndDialog() {
        mLogoutDialog = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_AlertDialog)
            .setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setNeutralButton(getString(R.string.dialog_neutral_btn)) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(getString(R.string.dialog_positive_btn)) { _, _ ->
                viewModel.publishEvent(
                    HomeViewEvent.Logout
                )
            }
            .create()

        mLogoutButton = findViewById(R.id.btn_logout)
        mLogoutButton.setOnClickListener { mLogoutDialog.show() }
    }

    private fun setupRecyclerView() {
        mForumItemAdapter = ForumItemAdapter(this, sharedPrefs)
        mForumRecyclerView = findViewById(R.id.forum_item_container)
        mForumRecyclerView.layoutManager = LinearLayoutManager(this)
        mForumRecyclerView.adapter = mForumItemAdapter
    }

    private fun subscribeObservers() {
        viewModel.forumSyncWork.observe(this, {
            if (it.isNotEmpty()) {
                val work = it[it.size - 1]
                if (work.state == WorkInfo.State.RUNNING) {
                    mSwipeRefreshContainer.isRefreshing = true
                    forumCacheUpdating = true
                    mHeaderMessage.text = getString(R.string.list_forum_updating)
                    Toast.makeText(
                        this,
                        getString(R.string.list_forum_updating_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    forumCacheUpdating = false
                    viewModel.publishEvent(HomeViewEvent.GetGslcForum)
                }
            }
        })

        viewModel.forumListState.observe(this, {
            when (it) {
                is DataState.Loading -> {
                    mSwipeRefreshContainer.isRefreshing = true
                }
                is DataState.Success -> {
                    mForumItemAdapter.updateData(it.data.list)
                    if (!forumCacheUpdating) {
                        mHeaderMessage.text =
                            if (it.data.unreplied == 0L) getString(R.string.all_forum_replied) else getString(
                                R.string.unreplied_forum_count,
                                it.data.unreplied.toString()
                            )
                        mSwipeRefreshContainer.isRefreshing = false
                    }
                }
                is DataState.Error -> {
                    Toast.makeText(this, it.exception.message, Toast.LENGTH_SHORT).show()
                    if (!forumCacheUpdating) mSwipeRefreshContainer.isRefreshing = false
                }
                else -> {
                }
            }
        })

        viewModel.logoutState.observe(this, {
            when (it) {
                is DataState.Success -> {
                    startActivity(
                        Intent(this, IntroActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
                else -> {
                }
            }
        })
    }
}