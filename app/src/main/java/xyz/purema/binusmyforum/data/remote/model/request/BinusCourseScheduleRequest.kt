package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusCourseScheduleRequest(
    var acadCareer: String,
    var emplid: String,
    var institution: String,
    var strm: String,
    var studentType: String
) {
    fun encryptParams(cryptoService: CryptoService) = apply {
        acadCareer = cryptoService.encryptParam(acadCareer)
        emplid = cryptoService.encryptParam(emplid)
        institution = cryptoService.encryptParam(institution)
        strm = cryptoService.encryptParam(strm)
        studentType = cryptoService.encryptParam(studentType)
    }
}