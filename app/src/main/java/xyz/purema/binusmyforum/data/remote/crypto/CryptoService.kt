package xyz.purema.binusmyforum.data.remote.crypto

interface CryptoService {
    fun encryptParam(str: String): String
    fun decryptParam(str: String): String
}