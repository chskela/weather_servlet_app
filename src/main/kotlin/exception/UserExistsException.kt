package exception

class UserExistsException(message: String) : RuntimeException("User exits: $message")