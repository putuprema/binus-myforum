package xyz.purema.binusmyforum.data.remote.model.response

import com.google.gson.annotations.SerializedName

class BinusLoginResponse(
    @SerializedName("access_token")
    var accessToken: String,
    @SerializedName("token_type")
    var tokenType: String,
    @SerializedName("expires_in")
    var expiresIn: Long,
    @SerializedName("refresh_token")
    var refreshToken: String
) : BinusResponseEnvelope<Any>()