package inovmidi.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import inovmidi.entity.Article
import inovmidi.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun findByUserAndIsPublished(user: User, isPublished: Boolean): List<Article>
    fun findByUser(user: User): List<Article>
    @Query("""
        SELECT DISTINCT a FROM Article a 
        WHERE a.user = :user 
        AND (
            LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(a.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(a.keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(a.abstract) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
    """)
    fun searchArticles(@Param("user") user: User, @Param("searchTerm") searchTerm: String): List<Article>
}



