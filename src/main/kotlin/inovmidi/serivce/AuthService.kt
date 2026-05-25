package inovmidi.serivce


import inovmidi.dto.request.LoginRequest
import inovmidi.dto.request.RegisterRequest
import inovmidi.dto.response.AuthResponse
import inovmidi.dto.response.UserInfo
import inovmidi.entity.User
import inovmidi.exception.BadRequestException
import inovmidi.repository.UserRepository
import inovmidi.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        // Validate passwords match
        if (request.password != request.confirmPassword) {
            throw BadRequestException("Passwords do not match")
        }

        // Check if username exists
        if (userRepository.existsByUsername(request.username)) {
            throw BadRequestException("Username is already taken")
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.email)) {
            throw BadRequestException("Email is already registered")
        }

        // Validate keywords count (minimum 3)
        // This validation is for article generation, not registration

        // Create new user
        val user = User(
            fullName = request.fullName,
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password)
        )

        val savedUser = userRepository.save(user)

        // Authenticate user
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        // Generate JWT token
        val token = jwtTokenProvider.generateToken(authentication)

        return AuthResponse(
            token = token,
            user = UserInfo(
                id = savedUser.id!!,
                fullName = savedUser.fullName,
                username = savedUser.username,
                email = savedUser.email,
                publishedArticlesCount = savedUser.publishedArticlesCount
            )
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        // Authenticate user
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.usernameOrEmail,
                request.password
            )
        )

        SecurityContextHolder.getContext().authentication = authentication

        // Generate JWT token
        val token = jwtTokenProvider.generateToken(authentication)

        // Get user details
        val user = userRepository.findByUsernameOrEmail(request.usernameOrEmail, request.usernameOrEmail)
            .orElseThrow { BadRequestException("User not found") }

        return AuthResponse(
            token = token,
            user = UserInfo(
                id = user.id!!,
                fullName = user.fullName,
                username = user.username,
                email = user.email,
                publishedArticlesCount = user.publishedArticlesCount
            )
        )
    }
}