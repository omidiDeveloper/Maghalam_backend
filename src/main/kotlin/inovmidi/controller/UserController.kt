package inovmidi.controller

import inovmidi.dto.response.UserProfileResponse
import inovmidi.serivce.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class AppearanceSettingsRequest(
    val darkMode: Boolean?,
    val fontSize: String?
)

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/profile")
    fun getUserProfile(): ResponseEntity<UserProfileResponse> {
        val profile = userService.getUserProfile()
        return ResponseEntity.ok(profile)
    }

    @PutMapping("/appearance")
    fun updateAppearanceSettings(
        @RequestBody request: AppearanceSettingsRequest
    ): ResponseEntity<UserProfileResponse> {
        val profile = userService.updateAppearanceSettings(
            darkMode = request.darkMode,
            fontSize = request.fontSize
        )
        return ResponseEntity.ok(profile)
    }
}