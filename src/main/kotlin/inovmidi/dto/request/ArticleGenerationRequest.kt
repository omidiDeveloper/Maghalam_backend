package inovmidi.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class ArticleGenerationRequest(
    @field:NotBlank(message = "Article title is required")
    val title: String,

    @field:NotBlank(message = "Author name is required")
    val author: String,

    @field:NotEmpty
    val keywords: List<@NotBlank String>,


    val language: String = "Farsi",

    @field:NotBlank(message = "Article description is required")
    @field:Size(min = 20, message = "Description must be at least 20 characters")
    val description: String
)
