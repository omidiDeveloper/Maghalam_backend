package inovmidi.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var fullName: String,

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false)
     var role : String = "USER" , // USER یا ADMIN

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var publishedArticlesCount: Int = 0,

    @Column(nullable = false)
    var darkMode: Boolean = false,

    @Column(nullable = false, length = 50)
    var fontSize: String = "Vazir",

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val articles: MutableList<Article> = mutableListOf()
)
