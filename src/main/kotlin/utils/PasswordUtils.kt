package utils

import com.password4j.Password

object PasswordUtils {

    fun hashPassword(password: String): String {
        return Password.hash(password).withArgon2().result
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return Password.check(password, hash).withArgon2()
    }
}