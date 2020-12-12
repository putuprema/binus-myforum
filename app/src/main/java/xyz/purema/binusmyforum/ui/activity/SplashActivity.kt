package xyz.purema.binusmyforum.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.data.service.AppUpdateService
import xyz.purema.binusmyforum.domain.DataState
import xyz.purema.binusmyforum.domain.utils.ActivityUtils
import xyz.purema.binusmyforum.ui.viewmodel.AuthState
import xyz.purema.binusmyforum.ui.viewmodel.SplashViewEvent
import xyz.purema.binusmyforum.ui.viewmodel.SplashViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var appUpdateService: AppUpdateService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<TextView>(R.id.version_num).text =
            packageManager.getPackageInfo(packageName, 0).versionName

        checkForAppUpdate()
        subscribeObservers()
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

    private fun checkForAppUpdate() {
        appUpdateService.updateCheckState.observe(this) {
            when (it) {
                is DataState.Success -> {
                    val appUpdateInfo = it.data
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                        if (appUpdateInfo.updatePriority() >= 5 && appUpdateInfo.isUpdateTypeAllowed(
                                AppUpdateType.IMMEDIATE
                            )
                        ) {
                            appUpdateService.appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this,
                                AppUpdateService.REQUEST_CODE_APP_UPDATE
                            )
                        } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                            appUpdateService.appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.FLEXIBLE,
                                this,
                                AppUpdateService.REQUEST_CODE_APP_UPDATE
                            )
                        }
                    } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        appUpdateService.appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            AppUpdateService.REQUEST_CODE_APP_UPDATE
                        )
                    } else {
                        resumeSession()
                    }
                }
                is DataState.Error -> {
                    Log.e(
                        SplashActivity::class.java.simpleName,
                        "Exception caught when checking for app update: ${it.exception.message}",
                        it.exception
                    )
                    resumeSession()
                }
                else -> {
                }
            }
        }
    }

    private fun resumeSession(withDelay: Boolean = true) {
        lifecycleScope.launch {
            if (withDelay) delay(1000)
            viewModel.publishEvent(SplashViewEvent.ResumeSession)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppUpdateService.REQUEST_CODE_APP_UPDATE) {
            resumeSession(false)
        }
    }
}