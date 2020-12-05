package xyz.purema.binusmyforum.data.remote.model.request

open class BinusAuthRequest(
    var grantType: GrantType,
    var clientId: String = "binus"
) {
    enum class GrantType { password, refresh_token }

    open fun toMapParams(): HashMap<String, Any> = hashMapOf(
        Pair("grant_type", grantType),
        Pair("client_id", clientId)
    )
}