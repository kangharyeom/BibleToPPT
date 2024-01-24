package bulletin.bulletin.domain.bible.controller;

import bulletin.bulletin.domain.bible.service.BibleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/bibles")
public class BibleController {
    private final BibleService bibleService;

    @PostMapping("/insert")
    public void bibleInsert () {
        for(int i=1; i<67; i++){
            String filePath = "C:\\Users\\PC\\Downloads\\bible\\bibletxt\\number\\"+i+".txt";
            log.info("filePath:{}",filePath);
            bibleService.insertTextFromFile(filePath);
        }
    }
}
