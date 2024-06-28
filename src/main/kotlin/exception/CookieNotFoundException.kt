package exception

import jakarta.servlet.ServletException

class CookieNotFoundException(message: String) : ServletException("Cookie not found: $message")