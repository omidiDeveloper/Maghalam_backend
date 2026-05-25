package inovmidi.controller


import inovmidi.dto.request.ArticleGenerationRequest
import inovmidi.dto.response.ArticleResponse
import inovmidi.service.ArticleService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles")
class ArticleController(
    private val articleService: ArticleService,
) {

    @PostMapping("/generate")
    fun generateArticle(
        @Valid @RequestBody request: ArticleGenerationRequest
    ): ResponseEntity<ArticleResponse> {
        val response = articleService.generateArticle(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getArticle(@PathVariable id: Long): ResponseEntity<ArticleResponse> {
        val response = articleService.getArticle(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getUserArticles(): ResponseEntity<List<ArticleResponse>> {
        val articles = articleService.getUserArticles()
        return ResponseEntity.ok(articles)
    }

    @PostMapping("/{id}/publish")
    fun publishArticle(@PathVariable id: Long): ResponseEntity<ArticleResponse> {
        val response = articleService.publishArticle(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/download")
    fun downloadArticle(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "pdf") format: String
    ): ResponseEntity<ByteArray> {
        return when (format.lowercase()) {
            "html" -> {
                val htmlContent = articleService.downloadArticleAsHtml(id)
                val htmlBytes = htmlContent.toByteArray(Charsets.UTF_8)

                val headers = HttpHeaders()
                headers.contentType = MediaType.TEXT_HTML
                headers.setContentDispositionFormData("attachment", "article_${id}.html")
                headers.contentLength = htmlBytes.size.toLong()

                ResponseEntity.ok()
                    .headers(headers)
                    .body(htmlBytes)
            }
            "pdf" -> {
                val pdfBytes = articleService.downloadArticleAsPdf(id)

                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_PDF
                headers.setContentDispositionFormData("attachment", "article_${id}.pdf")
                headers.contentLength = pdfBytes.size.toLong()

                ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes)
            }
            else -> {
                ResponseEntity.badRequest().build()
            }
        }
    }

    @GetMapping("/search")
    fun searchArticles(@RequestParam(required = false, defaultValue = "") query: String): ResponseEntity<List<ArticleResponse>> {
        val articles = articleService.searchArticles(query)
        return ResponseEntity.ok(articles)
    }
}