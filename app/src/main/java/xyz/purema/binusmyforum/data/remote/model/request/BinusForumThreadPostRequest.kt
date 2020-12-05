package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusForumThreadPostRequest(
    var acadCareer: String,
    var strm: String,
    var studentType: String,
    var threadID: String,
    var page: String = "1"
) {
    var limit: String = "1000"

    fun encryptParams(cryptoService: CryptoService) = apply {
        acadCareer = cryptoService.encryptParam(acadCareer)
        strm = cryptoService.encryptParam(strm)
        studentType = cryptoService.encryptParam(studentType)
        threadID = cryptoService.encryptParam(threadID)
        page = cryptoService.encryptParam(page)
        limit = cryptoService.encryptParam(limit)
    }
}