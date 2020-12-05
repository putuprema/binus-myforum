package xyz.purema.binusmyforum.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.domain.exception.EmptyFieldException
import xyz.purema.binusmyforum.domain.utils.ValidationUtils
import xyz.purema.binusmyforum.ui.dialog.LoadingDialog
import xyz.purema.binusmyforum.ui.viewmodel.AuthStage
import xyz.purema.binusmyforum.ui.viewmodel.LoginViewEvent
import xyz.purema.binusmyforum.ui.viewmodel.LoginViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loadingDlg: LoadingDialog

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingDlg = LoadingDialog(this, "Sedang login...")
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)

        val lastEmail = sharedPrefs.lastEmail
        if (lastEmail != null) {
            emailInput.setText(lastEmail.replace("@binus.ac.id", ""))
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_login).setOnClickListener { doLogin() }

        setupObservers()
    }

    private fun doLogin() {
        try {
            val emailInputValue = emailInput.text.toString()
            val passwordInputValue = passwordInput.text.toString()

            // fields should not be empty
            ValidationUtils.throwIfEmptyField(
                arrayOf(
                    arrayOf("Email", emailInputValue),
                    arrayOf("Password", passwordInputValue)
                )
            )

            viewModel.publishEvent(
                LoginViewEvent.Login(
                    "$emailInputValue@binus.ac.id",
                    passwordInputValue
                )
            )
        } catch (ex: EmptyFieldException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(this, {
            when (it) {
                is AuthStage.Login -> {
                    loadingDlg.show()
                }
                is AuthStage.SyncAccountData -> {
                    loadingDlg.text = "Sinkronisasi data..."
                }
                is AuthStage.Done -> {
                    loadingDlg.dismiss()

                    val intent = Intent(this, HomeActivity::class.java)
                        .putExtra("student", it.student)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    startActivity(intent)
                }
                is AuthStage.Error -> {
                    loadingDlg.dismiss()
                    Toast.makeText(this, it.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}