package inovmidi.serivce

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.service.OpenAiService
import inovmidi.exception.ArticleGenerationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

data class GeneratedArticleContent(
    val content: String,
    val abstract: String,
    val wordCount: Int
)

@Service
class AIService(
    @Value("\${app.openai.api-key}")
    private val apiKey: String,

    @Value("\${app.openai.model}")
    private val model: String
) {

    private val openAiService: OpenAiService by lazy {
        OpenAiService(apiKey, Duration.ofSeconds(60))
    }

    fun generateArticle(
        title: String,
        author: String,
        keywords: String,
        language: String,
        description: String
    ): GeneratedArticleContent {
        try {
            val prompt = buildPrompt(title, author, keywords, language, description)

            val messages = listOf(
                ChatMessage("system", "You are a professional article writer. Generate high-quality, well-structured articles based on the given requirements."),
                ChatMessage("user", prompt)
            )

            val request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(0.7)
                .maxTokens(2000)
                .build()

            val response = openAiService.createChatCompletion(request)
            val generatedText = response.choices.firstOrNull()?.message?.content
                ?: throw ArticleGenerationException("No content generated")

            // Parse the response to extract content and abstract
            val (content, abstract) = parseGeneratedContent(generatedText)
            val wordCount = content.split("\\s+".toRegex()).size

            return GeneratedArticleContent(
                content = content,
                abstract = abstract,
                wordCount = wordCount
            )

        } catch (ex: Exception) {
            throw ArticleGenerationException("Failed to generate article: ${ex.message}")
        }
    }

    private fun buildPrompt(
        title: String,
        author: String,
        keywords: String,
        language: String,
        description: String
    ): String {
        val languageInstruction = when (language.lowercase()) {
            "farsi", "persian" -> "Write the article in Persian (Farsi) language."
            "english" -> "Write the article in English language."
            else -> "Write the article in $language language."
        }

        return """
            Generate a professional article with the following specifications:
            
            Title: $title
            Author: $author
            Keywords: $keywords
            Language: $language
            Description: $description
            
            $languageInstruction
            
            Requirements:
            1. The article should be approximately 300 words
            2. Include an abstract (summary) of about 50-80 words at the beginning
            3. Use the provided keywords naturally throughout the article
            4. Follow academic writing standards
            5. Structure the article with clear paragraphs
            
            Format your response as follows:
            ABSTRACT:
            [Write the abstract here]
            
            CONTENT:
            [Write the full article content here]
        """.trimIndent()
    }

    private fun parseGeneratedContent(generatedText: String): Pair<String, String> {
        val abstractRegex = "ABSTRACT:\\s*(.+?)(?=CONTENT:|$)".toRegex(RegexOption.DOT_MATCHES_ALL)
        val contentRegex = "CONTENT:\\s*(.+)".toRegex(RegexOption.DOT_MATCHES_ALL)

        val abstractMatch = abstractRegex.find(generatedText)
        val contentMatch = contentRegex.find(generatedText)

        val abstract = abstractMatch?.groupValues?.get(1)?.trim()
            ?: generatedText.take(200) // Fallback: first 200 chars

        val content = contentMatch?.groupValues?.get(1)?.trim()
            ?: generatedText // Fallback: entire text

        return Pair(content, abstract)
    }
}