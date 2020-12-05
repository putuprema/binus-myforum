package xyz.purema.binusmyforum.data.remote.model.request

class BinusRefreshTokenRequest(
    var refreshToken: String
) : BinusAuthRequest(GrantType.refresh_token) {
    override fun toMapParams(): HashMap<String, Any> {
        val map = super.toMapParams()
        map["refresh_token"] = refreshToken
        return map
    }
}