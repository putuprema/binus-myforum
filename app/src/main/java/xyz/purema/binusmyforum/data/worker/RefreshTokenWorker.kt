package xyz.purema.binusmyforum.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.repository.StudentRepository

class RefreshTokenWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val studentRepository: StudentRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d(this::class.java.simpleName, "Refreshing auth token...")

        var attempt = 0
        while (true) {
            try {
                studentRepository.refreshToken()
                return Result.success()
            } catch (ex: AppException) {
                attempt += 1
                if (attempt > 5) throw ex
            }
        }
    }
}