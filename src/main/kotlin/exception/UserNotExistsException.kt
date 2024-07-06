package exception

class UserNotExistsException(message: String) : RuntimeException("User not exits: $message")