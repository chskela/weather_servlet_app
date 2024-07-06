package exception

class BadSessionException(message: String) : RuntimeException("Bad session: $message")