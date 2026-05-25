package inovmidi.dto.response

import java.time.LocalDateTime

data class ArticleResponse(
    val id: Long,
    val title: String,
    val author: String,
    val keywords: List<String>,
    val language: String,
    val description: String,
    val generatedContent: String,
    val generatedAbstract: String,
    val wordCount: Int,
    val isPublished: Boolean,
    val createdAt: LocalDateTime
)