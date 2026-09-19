package FastFoodApp.utils

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Utilidades de hashing seguro para contraseñas.
 *
 * Estrategia: SHA-256(salt + password)
 * - Compatible con KMP (commonMain) sin librerías externas.
 * - Salt único por usuario generado con SecureRandom.
 * - El salt se almacena junto al hash en la BD.
 *
 * NOTA: Para producción de alta seguridad, migrar a BCrypt/Argon2 en el target JVM.
 */
object HashUtils {

    private const val ALGORITHM = "SHA-256"

    /**
     * Genera un salt aleatorio de 32 bytes (64 caracteres hex).
     */
    fun generarSalt(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    /**
     * Genera el hash SHA-256 de [salt] + [password].
     * @return Hash en formato hexadecimal de 64 caracteres.
     */
    fun hashPassword(password: String, salt: String): String {
        val input = salt + password
        val digest = MessageDigest.getInstance(ALGORITHM)
        return digest.digest(input.toByteArray(Charsets.UTF_8)).toHex()
    }

    /**
     * Verifica si [passwordIngresado] coincide con el [hashAlmacenado] usando [salt].
     * Usa comparación de tiempo constante para evitar timing attacks.
     */
    fun verificarPassword(passwordIngresado: String, hashAlmacenado: String, salt: String): Boolean {
        val hashInput = hashPassword(passwordIngresado, salt)
        return constantTimeEquals(hashInput, hashAlmacenado)
    }

    /**
     * Comparación de strings en tiempo constante para prevenir timing attacks.
     */
    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}
