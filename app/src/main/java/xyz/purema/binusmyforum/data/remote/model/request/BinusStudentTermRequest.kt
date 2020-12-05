package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusStudentTermRequest(
    var acadCareer: String,
    var binusianID: String,
    var institution: String,
    var studentType: String
) {
    fun encryptParams(cryptoService: CryptoService) = apply {
        acadCareer = cryptoService.encryptParam(acadCareer)
        binusianID = cryptoService.encryptParam(binusianID)
        institution = cryptoService.encryptParam(institution)
        studentType = cryptoService.encryptParam(studentType)
    }
}