package exception

class LocationExistsException(message: String)  : RuntimeException("Location exits: $message")
