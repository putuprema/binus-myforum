package xyz.purema.binusmyforum.data.service

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.purema.binusmyforum.domain.DataState
import javax.inject.Inject

class AppUpdateService @Inject constructor(
    @ApplicationContext context: Context
) {
    val appUpdateManager = AppUpdateManagerFactory.create(context)

    private val _updateCheckState: MutableLiveData<DataState<AppUpdateInfo>> =
        MutableLiveData(DataState.Loading)
    val updateCheckState: LiveData<DataState<AppUpdateInfo>> get() = _updateCheckState

    private val _updateInstallState: MutableLiveData<Int> = MutableLiveData()
    val updateInstallState: LiveData<Int> get() = _updateInstallState

    private val installStateUpdatedListener =
        InstallStateUpdatedListener { state ->
            _updateInstallState.value = state.installStatus()

            if (state.installStatus() == InstallStatus.DOWNLOADED
                || state.installStatus() == InstallStatus.CANCELED
                || state.installStatus() == InstallStatus.FAILED
                || state.installStatus() == InstallStatus.INSTALLED
            ) {
                cleanup()
            }
        }

    init {
        appUpdateManager.registerListener(installStateUpdatedListener)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { _updateCheckState.value = DataState.Success(it) }
            .addOnFailureListener { _updateCheckState.value = DataState.Error(it) }
    }

    private fun cleanup() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    companion object {
        const val REQUEST_CODE_APP_UPDATE = 100
    }
}