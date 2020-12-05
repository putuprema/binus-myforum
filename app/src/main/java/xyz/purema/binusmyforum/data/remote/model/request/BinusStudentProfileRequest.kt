package xyz.purema.binusmyforum.data.remote.model.request

import xyz.purema.binusmyforum.data.remote.crypto.CryptoService

class BinusStudentProfileRequest(
    var binusianId: String
) {
    fun encryptParams(cryptoService: CryptoService) = apply {
        binusianId = cryptoService.encryptParam(binusianId)
    }
}