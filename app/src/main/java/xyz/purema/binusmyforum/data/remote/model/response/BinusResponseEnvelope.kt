package xyz.purema.binusmyforum.data.remote.model.response

import com.google.gson.annotations.SerializedName

open class BinusResponseEnvelope<T>(
    var error: String? = null,
    @SerializedName("error_description")
    var errorDescription: String? = null,
    var code: String? = null,
    var message: String? = null,
    var data: T? = null
)