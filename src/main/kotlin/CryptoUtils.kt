import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import java.util.Base64

object CryptoUtils {

    // Extrae IV de los primeros 16 bytes, luego desencripta
    fun desencriptar(datos: ByteArray, clave: SecretKey): ByteArray {
        val iv = datos.copyOfRange(0, 16)
        val contenido = datos.copyOfRange(16, datos.size)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, clave, IvParameterSpec(iv))
        return cipher.doFinal(contenido)
    }
}