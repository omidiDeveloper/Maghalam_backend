package inovmidi.dto.response

data class UserProfileResponse(
    val id: Long,
    val fullName: String,
    val username: String,
    val email: String,
    val publishedArticlesCount: Int,
    val darkMode: Boolean,
    val fontSize: String
)