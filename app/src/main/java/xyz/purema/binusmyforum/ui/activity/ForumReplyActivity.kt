package xyz.purema.binusmyforum.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import xyz.purema.binusmyforum.BinusMyForumApplication
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.domain.DataState
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import xyz.purema.binusmyforum.domain.utils.ActivityUtils
import xyz.purema.binusmyforum.domain.utils.TextUtils
import xyz.purema.binusmyforum.ui.dialog.LoadingDialog
import xyz.purema.binusmyforum.ui.viewmodel.ForumReplyViewEvent
import xyz.purema.binusmyforum.ui.viewmodel.ForumReplyViewModel
import java.net.URLDecoder
import kotlin.math.pow

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ForumReplyActivity : AppCompatActivity() {
    private lateinit var forum: ForumThread
    private lateinit var replyInput: EditText
    private lateinit var sendReplyBtnContainer: LinearLayout
    private lateinit var btnReply: MaterialButton
    private lateinit var btnAttachFile: MaterialButton
    private lateinit var yourReplyText: TextView
    private lateinit var yourReplyHeader: TextView
    private lateinit var loadingDlg: LoadingDialog
    private lateinit var attachmentInfoContainer: CardView
    private lateinit var attachmentFileNameTextView: TextView
    private lateinit var attachmentFileSizeTextView: TextView
    private lateinit var btnDeleteAttachment: MaterialButton
    private var attachment: Uri? = null

    private val viewModel: ForumReplyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setStatusBarBackground(this, R.drawable.gradient_bg)
        setContentView(R.layout.activity_forum_reply)

        loadingDlg = LoadingDialog(this, getString(R.string.sending_reply))

        forum = intent.getParcelableExtra("forum_thread")!!

        val toolbar = findViewById<MaterialToolbar>(R.id.material_toolbar)
        toolbar.title = forum.subject
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val forumMessage = URLDecoder.decode(forum.creatorMessage, "UTF-8")
        findViewById<TextView>(R.id.forum_creator_message).text = TextUtils.renderHtml(forumMessage)

        attachmentInfoContainer = findViewById(R.id.attachment_info_container)
        attachmentInfoContainer.visibility = View.GONE
        attachmentFileNameTextView = findViewById(R.id.attachment_file_name)
        attachmentFileSizeTextView = findViewById(R.id.attachment_file_size)
        btnDeleteAttachment = findViewById(R.id.btn_delete_attachment)

        yourReplyHeader = findViewById(R.id.forum_reply_header)
        yourReplyText = findViewById(R.id.forum_your_reply_text)
        btnAttachFile = findViewById(R.id.btn_reply_attach_file)
        btnReply = findViewById(R.id.btn_send_reply)
        sendReplyBtnContainer = findViewById(R.id.send_reply_btn_container)
        replyInput = findViewById(R.id.forum_reply_input)

        if (StringUtils.isNotEmpty(forum.replyMessage)) {
            replyInput.visibility = View.GONE
            sendReplyBtnContainer.visibility = View.GONE
            btnAttachFile.visibility = View.GONE

            yourReplyHeader.text = getString(R.string.your_reply)

            var yourReply = URLDecoder.decode(forum.replyMessage, "UTF-8")
            yourReply = yourReply.replace(
                "<blockquote>.*</blockquote>".toRegex(RegexOption.DOT_MATCHES_ALL),
                ""
            )

            yourReplyText.text = TextUtils.renderHtml(yourReply)
        } else {
            yourReplyText.visibility = View.GONE
            btnReply.setOnClickListener { replyForum() }
            btnAttachFile.setOnClickListener { pickAttachment() }
            btnDeleteAttachment.setOnClickListener { clearAttachment() }
        }

//        yourReplyText.visibility = View.GONE
//        btnReply.setOnClickListener { replyForum() }
//        btnAttachFile.setOnClickListener { pickAttachment() }
//        btnDeleteAttachment.setOnClickListener { clearAttachment() }

        subscribeObservers()
    }

    private fun replyForum() {
        val replyInputText = replyInput.text.toString()
        if (StringUtils.isEmpty(replyInputText)) {
            Toast.makeText(this, getString(R.string.empty_reply_message), Toast.LENGTH_SHORT).show()
        } else {
            forum.replyMessage = replyInputText
            viewModel.publishEvent(ForumReplyViewEvent.ReplyForum(forum, attachment))
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) pickAttachment()
            else handleFilePermissionDenied()
        }

    private val attachmentPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                val cursor = contentResolver.query(it, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val fileName =
                        cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                    val fileSize =
                        cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE))
                            .toLong()

                    // File can't exceed limit constant
                    if (fileSize / (10.0.pow(6)) > BinusMyForumApplication.UPLOAD_FILE_SIZE_LIMIT_MB) {
                        Toast.makeText(
                            this,
                            getString(
                                R.string.upload_file_over_limit,
                                BinusMyForumApplication.UPLOAD_FILE_SIZE_LIMIT_MB.toString()
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        attachment = it
                        attachmentFileNameTextView.text = fileName
                        attachmentFileSizeTextView.text = FileUtils.byteCountToDisplaySize(fileSize)
                        attachmentInfoContainer.visibility = View.VISIBLE
                        btnAttachFile.visibility = View.GONE
                    }
                }
                cursor?.close()
            }
        }

    private fun pickAttachment() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                attachmentPicker.launch(
                    arrayOf(
                        "image/*",
                        "audio/*",
                        "video/*",
                        "text/*",
                        "application/vnd.rar",
                        "application/zip",
                        "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/msword",
                        "application/vnd.ms-excel",
                        "application/vnd.ms-powerpoint"
                    )
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                handleFilePermissionDenied()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun clearAttachment() {
        attachment = null
        attachmentInfoContainer.visibility = View.GONE
        btnAttachFile.visibility = View.VISIBLE
    }

    private fun subscribeObservers() {
        viewModel.replyState.observe(this, {
            when (it) {
                is DataState.Loading -> {
                    loadingDlg.show()
                }
                is DataState.Success -> {
                    loadingDlg.dismiss()
                    Toast.makeText(this, getString(R.string.reply_sent), Toast.LENGTH_SHORT).show()
                    finish()
                }
                is DataState.Error -> {
                    loadingDlg.dismiss()
                    Toast.makeText(this, it.exception.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        })
    }

    private fun handleFilePermissionDenied() {
        Toast.makeText(this, getString(R.string.open_file_permission_denied), Toast.LENGTH_SHORT)
            .show()
    }
}