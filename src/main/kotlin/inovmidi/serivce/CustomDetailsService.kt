package inovmidi.serivce

import inovmidi.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsernameOrEmail(username, username)
            .orElseThrow { UsernameNotFoundException("User not found with username or email: $username") }

        return User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(emptyList())
            .build()
    }
}
