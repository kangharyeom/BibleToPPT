package bulletin.bulletin.domain.bible.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@Builder
public class BiblePPTDownResponseDto {
    // 성경구절
    private String title;
    private String startChapter;
    private String startVerse;
    private String endChapter;
    private String endVerse;

}
