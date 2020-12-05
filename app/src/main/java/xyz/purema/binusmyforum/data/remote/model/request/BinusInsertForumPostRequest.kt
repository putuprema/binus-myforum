package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusInsertForumPostRequest(
    var forumTypeId: String,
    var message: String,
    var postReplyTo: String,
    var studentType: String,
    var subject: String,
    var threadId: String,
    var userId: String
) {
    fun encryptParams(cryptoService: CryptoService) = apply {
        forumTypeId = cryptoService.encryptParam(forumTypeId)
        message = cryptoService.encryptParam(message)
        postReplyTo = cryptoService.encryptParam(postReplyTo)
        studentType = cryptoService.encryptParam(studentType)
        subject = cryptoService.encryptParam(subject)
        threadId = cryptoService.encryptParam(threadId)
        userId = cryptoService.encryptParam(userId)
    }
}