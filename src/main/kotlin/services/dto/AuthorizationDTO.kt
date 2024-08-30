package services.dto

import models.entities.User

data class AuthorizationDTO(
    val email: String, val password: String
)

fun AuthorizationDTO.toUser() = User(login = email, password = password)