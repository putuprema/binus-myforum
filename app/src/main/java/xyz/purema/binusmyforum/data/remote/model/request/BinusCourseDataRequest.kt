package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusCourseDataRequest(
    var acadCareer: String,
    var emplId: String,
    var strm: String,
    var userId: String
) {
    fun encryptParams(cryptoService: CryptoService) = apply {
        acadCareer = cryptoService.encryptParam(acadCareer)
        emplId = cryptoService.encryptParam(emplId)
        strm = cryptoService.encryptParam(strm)
        userId = cryptoService.encryptParam(userId)
    }
}