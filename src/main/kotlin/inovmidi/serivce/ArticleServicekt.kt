package inovmidi.service

import inovmidi.dto.request.ArticleGenerationRequest
import inovmidi.dto.response.ArticleResponse
import inovmidi.entity.Article
import inovmidi.repository.ArticleRepository
import inovmidi.repository.UserRepository
import inovmidi.serivce.HtmlTemplateService
import inovmidi.serivce.PdfService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val pdfService: PdfService,
    private val htmlTemplateService: HtmlTemplateService,
    private val restTemplate: RestTemplate,
    @Value("\${openai.api.key}") private val apiKey: String,
    @Value("\${openai.api.url}") private val apiUrl: String
) {

    @Transactional
    fun generateArticle(request: ArticleGenerationRequest): ArticleResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }

        // Generate abstract
        val generatedAbstract = callOpenAI(
            "Write a concise abstract (150-200 words) for an article about: ${request.title}. " +
                    "Keywords: ${request.keywords.joinToString(", ")}. Language: ${request.language}"
        )

        // Generate content
        val generatedContent = callOpenAI(
            "Write a detailed article about: ${request.title}. " +
                    "Keywords: ${request.keywords.joinToString(", ")}. " +
                    "Language: ${request.language}. " +
                    "Description: ${request.description}. " +
                    "Make it comprehensive and well-structured."
        )

        val article = Article(
            title = request.title,
            author = request.author,
            keywords = request.keywords.joinToString(", "),
            language = request.language,
            description = request.description,
            content = generatedContent,
            abstract = generatedAbstract,
            wordCount = generatedContent.split("\\s+".toRegex()).size,
            user = user,
            isPublished = false,
            createdAt = LocalDateTime.now()
        )

        val savedArticle = articleRepository.save(article)
        return mapToResponse(savedArticle)
    }

    private fun callOpenAI(prompt: String): String {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("Authorization", "Bearer $apiKey")

        val requestBody = mapOf(
            "model" to "gpt-4o",
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            ),
            "temperature" to 0.7
        )

        val entity = HttpEntity(requestBody, headers)

        val response = restTemplate.postForObject(apiUrl, entity, Map::class.java)
            ?: throw RuntimeException("OpenAI API returned null response")

        if (response.containsKey("error")) {
            val error = response["error"] as Map<*, *>
            throw RuntimeException("OpenAI error: ${error["message"]}")
        }

        val choices = response["choices"] as? List<*>
            ?: throw RuntimeException("Invalid response: missing choices")

        val firstChoice = choices[0] as Map<*, *>
        val message = firstChoice["message"] as Map<*, *>
        val content = message["content"] as String

        return content
    }

    fun getArticle(id: Long): ArticleResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val article = articleRepository.findById(id)
            .orElseThrow { RuntimeException("Article not found") }

        if (article.user.username != username) {
            throw RuntimeException("Access denied")
        }

        return mapToResponse(article)
    }

    fun getUserArticles(): List<ArticleResponse> {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }

        return articleRepository.findByUser(user).map { mapToResponse(it) }
    }

    @Transactional
    fun publishArticle(id: Long): ArticleResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val article = articleRepository.findById(id)
            .orElseThrow { RuntimeException("Article not found") }

        if (article.user.username != username) {
            throw RuntimeException("Access denied")
        }

        article.isPublished = true
        val updatedArticle = articleRepository.save(article)
        return mapToResponse(updatedArticle)
    }

    fun downloadArticleAsHtml(id: Long): String {
        val username = SecurityContextHolder.getContext().authentication.name
        val article = articleRepository.findById(id)
            .orElseThrow { RuntimeException("Article not found") }

        if (article.user.username != username) {
            throw RuntimeException("Access denied")
        }

        return htmlTemplateService.generateArticleHtml(article)
    }

    fun downloadArticleAsPdf(id: Long): ByteArray {
        val username = SecurityContextHolder.getContext().authentication.name
        val article = articleRepository.findById(id)
            .orElseThrow { RuntimeException("Article not found") }

        if (article.user.username != username) {
            throw RuntimeException("Access denied")
        }

        return pdfService.generatePdf(article)
    }

    private fun mapToResponse(article: Article): ArticleResponse {
        return ArticleResponse(
            id = article.id ?: 0,
            title = article.title,
            author = article.author,
            keywords = article.keywords.split(", "),
            language = article.language,
            description = article.description,
            generatedContent = article.content,
            generatedAbstract = article.abstract,
            wordCount = article.wordCount,
            isPublished = article.isPublished,
            createdAt = article.createdAt,
        )
    }

    fun searchArticles(searchTerm: String): List<ArticleResponse> {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }

        if (searchTerm.isBlank()) {
            return articleRepository.findByUser(user).map { mapToResponse(it) }
        }

        return articleRepository.searchArticles(user, searchTerm.trim()).map { mapToResponse(it) }
    }
}
