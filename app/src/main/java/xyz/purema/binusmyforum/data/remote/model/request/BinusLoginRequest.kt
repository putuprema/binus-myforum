package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusLoginRequest(
    var username: String,
    var password: String
) : BinusAuthRequest(GrantType.password) {
    fun encryptParams(cryptoService: CryptoService) = apply {
        username = cryptoService.encryptParam(username)
        password = cryptoService.encryptParam(password)
    }

    override fun toMapParams(): HashMap<String, Any> {
        val map = super.toMapParams()
        map["username"] = username
        map["password"] = password
        return map
    }
}