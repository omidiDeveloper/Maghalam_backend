package inovmidi.controller


import inovmidi.entity.Article
import inovmidi.entity.User
import inovmidi.repository.ArticleRepository
import inovmidi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = ["*"])
class AdminController {
    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val articleRepository: ArticleRepository? = null

    @get:GetMapping("/users")
    val allUsers: ResponseEntity<MutableList<MutableMap<String?, Any?>?>?>
        // دریافت لیست تمام کاربران
        get() {
            val users: MutableList<User?> = userRepository!!.findAll()
            val userList = users.stream().map<MutableMap<String?, Any?>?> { user: User? ->
                val userMap: MutableMap<String?, Any?> = HashMap<String?, Any?>()
                userMap.put("id", user!!.id.toString())
                userMap.put("username", user.username.toString())
                userMap.put("email", user.email.toString())
                userMap.put("createdAt", user.createdAt.toString())
                userMap
            }.toList()
            return ResponseEntity.ok<MutableList<MutableMap<String?, Any?>?>?>(userList)
        }

    @get:GetMapping("/articles")
    val allArticles: ResponseEntity<MutableList<MutableMap<String?, Any?>?>?>
        // دریافت لیست تمام مقالات (همه یوزرها)
        get() {
            val articles: MutableList<Article?> = articleRepository!!.findAll()
            val articleList = articles.stream().map<MutableMap<String?, Any?>?> { article: Article? ->
                val articleMap: MutableMap<String?, Any?> = HashMap<String?, Any?>()
                articleMap.put("id", article!!.id.toString())
                articleMap.put("title", article!!.title.toString())
                articleMap.put("content", article!!.content.toString())
                articleMap.put("status", article!!.isPublished.toString())
                articleMap.put("createdAt", article!!.createdAt.toString())
                articleMap["userId"] = article.user.id.toString()
                articleMap.put("username", article.user.username.toString())
                articleMap.put("userEmail", article.user.email.toString())
                articleMap
            }.toList()
            return ResponseEntity.ok<MutableList<MutableMap<String?, Any?>?>?>(articleList)
        }
}