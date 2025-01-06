package bulletin.bulletin.domain.bible.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/") // 루트 URL 요청 처리
    public String home() {
        return "test"; // resources/templates/test.html을 렌더링
    }

    @GetMapping("/post")
    public String biblePostMain() {
        return "bible-post";
    }

    @GetMapping("/powerpoint/download")
    public String bibleDownload() {
        return "bible-download";
    }
}
