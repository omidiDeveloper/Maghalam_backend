package inovmidi.entity


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "articles")
data class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var author: String,

    @Column(nullable = false, length = 500)
    var keywords: String,

    @Column(nullable = false, length = 50)
    var language: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    var content: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var abstract: String = "",

    @Column(nullable = false)
    var wordCount: Int = 0,

    @Column(nullable = false)
    var isPublished: Boolean = false,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
)
