package exception

import jakarta.servlet.ServletException

class SessionNotFoundException(message: String) : ServletException("Session not found: $message")