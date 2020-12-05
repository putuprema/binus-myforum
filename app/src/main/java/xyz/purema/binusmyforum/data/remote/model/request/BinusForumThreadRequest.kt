package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusForumThreadRequest(
    var acadCareer: String,
    var classNbr: String,
    var emplid: String,
    var forumTypeId: String,
    var strm: String,
    var studentType: String,
    var sessionIdNum: String = "",
    var topicId: String = "0"
) {
    fun encryptParam(cryptoService: CryptoService) = apply {
        acadCareer = cryptoService.encryptParam(acadCareer)
        classNbr = cryptoService.encryptParam(classNbr)
        emplid = cryptoService.encryptParam(emplid)
        forumTypeId = cryptoService.encryptParam(forumTypeId)
        strm = cryptoService.encryptParam(strm)
        studentType = cryptoService.encryptParam(studentType)
        sessionIdNum = cryptoService.encryptParam(sessionIdNum)
        topicId = cryptoService.encryptParam(topicId)
    }
}