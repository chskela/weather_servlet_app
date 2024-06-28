package exception

import jakarta.servlet.ServletException

class BadSessionException(message: String) : ServletException("Bad session: $message")