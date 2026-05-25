package inovmidi.dto.response

data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val user: UserInfo
)

data class UserInfo(
    val id: Long,
    val fullName: String,
    val username: String,
    val email: String,
    val publishedArticlesCount: Int
)