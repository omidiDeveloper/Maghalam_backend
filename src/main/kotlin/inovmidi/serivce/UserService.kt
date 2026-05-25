package inovmidi.serivce

import inovmidi.dto.response.UserProfileResponse
import inovmidi.exception.ResourceNotFoundException
import inovmidi.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserProfile(): UserProfileResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not found") }

        return UserProfileResponse(
            id = user.id!!,
            fullName = user.fullName,
            username = user.username,
            email = user.email,
            publishedArticlesCount = user.publishedArticlesCount,
            darkMode = user.darkMode,
            fontSize = user.fontSize
        )
    }

    @Transactional
    fun updateAppearanceSettings(darkMode: Boolean?, fontSize: String?): UserProfileResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not found") }

        darkMode?.let { user.darkMode = it }
        fontSize?.let { user.fontSize = it }

        userRepository.save(user)

        return getUserProfile()
    }
}
