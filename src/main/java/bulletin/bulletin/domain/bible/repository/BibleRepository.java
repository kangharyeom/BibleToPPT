package bulletin.bulletin.domain.bible.repository;

import bulletin.bulletin.domain.bible.entity.Bible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BibleRepository extends JpaRepository<Bible, Long> {
    @Query(value =
            "SELECT * FROM bibles " +
                    "WHERE title = :title AND bible_id BETWEEN " +
                    "   (SELECT bible_id FROM bibles WHERE title = :title AND chapter = :startChapter AND verse = :startVerse) " +
                    "   AND " +
                    "   (SELECT bible_id FROM bibles WHERE title = :title AND chapter = :endChapter AND verse = :endVerse) " +
                    "ORDER BY bible_id ASC", nativeQuery = true)
    List<Bible> findByBiblePPTDownPostDto (
            @Param("title") String title,
            @Param("startChapter") String startChapter,
            @Param("startVerse") String startVerse,
            @Param("endChapter") String endChapter,
            @Param("endVerse") String endVerse
    );

    @Query(value = "UPDATE bibles SET title = :fullTitle WHERE title = :shortTitle", nativeQuery = true)
    void changeShortNameToFullName(@Param("shortTitle") String shortTitle, @Param("fullTitle") String fullTitle);

    @Query(value = "UPDATE bibles SET content = REGEXP_REPLACE(content, '<[^>]+> ', '') WHERE content REGEXP '<[^>]+>'", nativeQuery = true)
    void deleteAllow();

    @Query("SELECT DISTINCT b.chapter FROM Bible b WHERE b.title = :title ORDER BY b.chapter")
    List<String> findDistinctChaptersByTitle(@Param("title") String title);

    @Query("SELECT DISTINCT b.verse FROM Bible b WHERE b.title = :title AND b.chapter = :chapter ORDER BY b.verse")
    List<String> findDistinctVersesByTitleAndChapter(
            @Param("title") String title,
            @Param("chapter") String chapter
    );
}
