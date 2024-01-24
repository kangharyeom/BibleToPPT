package bulletin.bulletin.domain.bible.repository;

import bulletin.bulletin.domain.bible.entity.Bible;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibleRepository extends JpaRepository<Bible, Long> {
}
