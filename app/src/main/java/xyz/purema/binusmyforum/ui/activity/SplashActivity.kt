package xyz.purema.binusmyforum.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.domain.utils.ActivityUtils
import xyz.purema.binusmyforum.ui.viewmodel.AuthState
import xyz.purema.binusmyforum.ui.viewmodel.SplashViewEvent
import xyz.purema.binusmyforum.ui.viewmodel.SplashViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<TextView>(R.id.version_num).text =
            packageManager.getPackageInfo(packageName, 0).versionName

        subscribeObservers()
        lifecycleScope.launch {
            delay(1000)
            viewModel.publishEvent(SplashViewEvent.ResumeSession)
        }
    }

    private fun subscribeObservers() {
        viewModel.state.observe(this, {
            when (it) {
                is AuthState.Success -> {
                    val intent = Intent(this, HomeActivity::class.java)
                        .putExtra("student", it.data)

                    startActivity(intent)
                }
                is AuthState.ResumeSessionError -> {
                    Toast.makeText(this, it.exception.message, Toast.LENGTH_SHORT).show()
                    ActivityUtils.goToActivity(this, LoginActivity::class.java)
                }
                is AuthState.IsFirstLogin -> {
                    ActivityUtils.goToActivity(this, IntroActivity::class.java)
                }
            }
        })
    }
}