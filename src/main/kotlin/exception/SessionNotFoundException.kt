package exception

class SessionNotFoundException(message: String) : RuntimeException("Session not found: $message")