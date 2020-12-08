package xyz.purema.binusmyforum.data.remote.crypto

import android.util.Base64
import com.google.android.gms.common.util.Hex
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.min

/**
 * Service used for encrypting request body fields before sending it to BINUSMAYA
 * or decrypting response coming from BINUSMAYA.
 * @param secretKeyString Secret key string, can be obtained from BINUS Mobile decompiled APK
 * @param ivString String used for IV (Initialization Vector), can be obtained from BINUS Mobile decompiled APK
 */
class CryptoServiceImpl(
    secretKeyString: String,
    ivString: String
) : CryptoService {
    // encode maximum first 32 characters of the secret key string to byte array
    private val key = Hex.bytesToStringLowercase(
        MessageDigest.getInstance("SHA-256").digest(secretKeyString.toByteArray())
    ).encodeToByteArray(endIndex = min(32, secretKeyString.length))

    // encode maximum first 16 characters of the IV string to byte array
    private val iv = ivString.encodeToByteArray(endIndex = min(16, ivString.length))

    // init AES cipher, secret key spec, and iv parameter spec
    private val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    private val secretKeySpec = SecretKeySpec(key, "AES")
    private val ivParameterSpec = IvParameterSpec(iv)

    /**
     * Main function for encryption/decryption
     * @param opMode Whether to encrypt (`Cipher.ENCRYPT_MODE`) or decrypt (`Cipher.DECRYPT_MODE`)
     * @param originalString Raw string to encrypt or Base64 encoded cipher text to decrypt
     * @return Base64 encoded cipher text or raw decrypted string
     */
    private fun encryptDecrypt(opMode: Int, originalString: String): String {
        cipher.init(opMode, secretKeySpec, ivParameterSpec)

        val result = when (opMode) {
            Cipher.ENCRYPT_MODE -> Base64.encodeToString(
                cipher.doFinal(originalString.toByteArray()),
                Base64.DEFAULT
            )
            Cipher.DECRYPT_MODE -> cipher.doFinal(Base64.decode(originalString, Base64.DEFAULT))
                .toString()
            else -> throw IllegalArgumentException("Unknown op mode!")
        }

        return result.replace("\n", "")
    }

    override fun encryptParam(str: String): String = encryptDecrypt(Cipher.ENCRYPT_MODE, str)
    override fun decryptParam(str: String): String = encryptDecrypt(Cipher.DECRYPT_MODE, str)
}