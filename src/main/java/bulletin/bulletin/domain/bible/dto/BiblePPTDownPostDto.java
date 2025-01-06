package bulletin.bulletin.domain.bible.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BiblePPTDownPostDto {
    // 성경구절
    private String title;
    private String startChapter;
    private String startVerse;
    private String endChapter;
    private String endVerse;

}
