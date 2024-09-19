package utils

object LoginUtils {

    fun validateLogin(login: String): Boolean {
        // This is the regex for canonical email addresses according to RFC 5322 (precluding comments):
        return login.contains(
            """
                [!#-'*+/-9=?A-Z^-~-]+(\.[!#-'*+/-9=?A-Z^-~-]+)*|"([]!#-[^-~ \t]|(\\[\t -~]))+")@([!#-'*+/-9=?A-Z^-~-]+(\.[!#-'*+/-9=?A-Z^-~-]+)*|\[[\t -Z^-~]*])
                """.trimIndent()
                .toRegex(RegexOption.IGNORE_CASE)
        )
    }
}
