package xyz.purema.binusmyforum.data.service

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppUpdateService @Inject constructor(
    @ApplicationContext context: Context
) {
    val appUpdateManager = AppUpdateManagerFactory.create(context)

    private val _appUpdateInfo: MutableLiveData<AppUpdateInfo> = MutableLiveData()
    val appUpdateInfo: LiveData<AppUpdateInfo> get() = _appUpdateInfo

    private val _updateInstallState: MutableLiveData<Int> = MutableLiveData()
    val updateInstallState: LiveData<Int> get() = _updateInstallState

    private val installStateUpdatedListener =
        InstallStateUpdatedListener { state -> _updateInstallState.value = state.installStatus() }

    init {
        appUpdateManager.registerListener(installStateUpdatedListener)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { _appUpdateInfo.value = it }
    }

    fun cleanup() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    companion object {
        const val REQUEST_CODE_APP_UPDATE = 100
    }
}