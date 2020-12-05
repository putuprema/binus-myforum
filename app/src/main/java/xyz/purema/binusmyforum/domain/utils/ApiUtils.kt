package xyz.purema.binusmyforum.domain.utils

import android.util.Log
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.HttpException
import xyz.purema.binusmyforum.data.remote.model.response.BinusResponseEnvelope
import xyz.purema.binusmyforum.domain.exception.AppException

class ApiUtils(private val gson: Gson) {
    fun getGenericApiResponse(body: ResponseBody): BinusResponseEnvelope<*> {
        return gson.fromJson(body.charStream(), BinusResponseEnvelope::class.java)
    }

    fun handleRequestError(ex: Exception): AppException {
        Log.d("handleRequestError", "Exception caught:", ex)
        return try {
            when (ex) {
                is HttpException -> {
                    val errorBody = ex.response()?.errorBody()
                    if (errorBody != null) {
                        val response = getGenericApiResponse(errorBody)

                        val errMessage: String = response.message ?: response.errorDescription
                        ?: "Duh, BINUSMAYA-nya error lagi nih. Sabar yak, ntar coba lagi."

                        AppException(errMessage, "")
                    } else {
                        AppException(
                            "Duh, BINUSMAYA-nya error lagi nih. Sabar yak, ntar coba lagi.",
                            "UNKNOWN"
                        )
                    }
                }
                is AppException -> ex
                else -> AppException(
                    "Duh, BINUSMAYA-nya error lagi nih. Sabar yak, ntar coba lagi.",
                    "UNKNOWN"
                )
            }
        } catch (ex: Exception) {
            Log.e("Exception", "Exception caught: ", ex)
            AppException("Duh, BINUSMAYA-nya error lagi nih. Sabar yak, ntar coba lagi.", "UNKNOWN")
        }
    }
}