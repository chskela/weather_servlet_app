package utils

import com.password4j.Password

object PasswordUtils {

    fun hashPassword(password: String): String {
        return Password.hash(password).withArgon2().result
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return Password.check(password, hash).withArgon2()
    }

    fun validatePassword(password: String): Boolean {
        return password.length in 8..32 && password.contains("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!#\$%&? \"])".toRegex())
    }
}