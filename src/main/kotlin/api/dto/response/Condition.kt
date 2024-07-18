package api.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    val icon: String,
    val text: String
)