package exception

class CookieNotFoundException(message: String) : RuntimeException("Cookie not found: $message")