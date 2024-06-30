package exception

import jakarta.servlet.ServletException

class UserExistsException(message: String) : ServletException("User exits: $message")