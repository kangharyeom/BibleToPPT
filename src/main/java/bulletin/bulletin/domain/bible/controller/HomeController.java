package bulletin.bulletin.domain.bible.controller;

import bulletin.bulletin.domain.bible.BibleFullName;
import bulletin.bulletin.domain.bible.service.BibleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Log4j2
public class HomeController {
    private final BibleService bibleService;

    @GetMapping("/") // 루트 URL 요청 처리
    public String home() {
        return "test"; // resources/templates/test.html을 렌더링
    }

    @GetMapping("/post")
    public String biblePostMain() {
        return "bible-post";
    }

    @GetMapping("/powerpoint/download")
    public String bibleDownload(Model model) {
        // 성경 책 이름 목록 가져오기
        List<String> bibleBooks = Arrays.stream(BibleFullName.values())
                .map(Enum::name)
                .toList();

        // 장 번호 범위 생성 (예: 1-150)
        Map<String, List<String>> chapterRanges = new HashMap<>();
        for (BibleFullName book : BibleFullName.values()) {
            // 각 성경 책별로 실제 장 수를 DB에서 조회
            List<String> chapters = bibleService.getChaptersByBook(book.name());
            chapterRanges.put(book.name(), chapters);
        }
        log.info("Bible [{}]", chapterRanges.toString());

        // 절 번호 범위도 필요한 경우 (예: 1-176)
        Map<String, Map<String, List<String>>> verseRanges = new HashMap<>();
        for (BibleFullName book : BibleFullName.values()) {
            Map<String, List<String>> chapterVerses = new HashMap<>();
            List<String> chapters = chapterRanges.get(book.name());

            if (chapters != null) {
                for (String chapter : chapters) {
                    // 각 장별로 실제 절 수를 DB에서 조회
                    List<String> verses = bibleService.getVersesByBookAndChapter(book.name(), chapter);
                    if (verses == null) {
                        verses = new ArrayList<>();
                    }
                    chapterVerses.put(chapter, verses);
                }
            }
            verseRanges.put(book.name(), chapterVerses);
        }

        // Model에 데이터 추가
        model.addAttribute("bibleBooks", bibleBooks);
        model.addAttribute("chapterRanges", chapterRanges);
        model.addAttribute("verseRanges", verseRanges);

        return "bible-download";
    }
}
