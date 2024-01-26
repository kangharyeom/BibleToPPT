package bulletin.bulletin.domain.bible.controller;

import bulletin.bulletin.domain.bible.service.BibleService;
import bulletin.bulletin.domain.bible.entity.Bible;
import dto.BiblePPTDownPostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/bibles")
public class BibleController {
    private final BibleService bibleService;

    @PostMapping
    public ResponseEntity<String> biblePost () {
        for(int i=1; i<67; i++){
            String filePath = "C:\\Users\\PC\\Downloads\\bible\\bibletxt\\number\\"+i+".txt";
            log.info("filePath:{}",filePath);
            bibleService.insertTextFromFile(filePath);
        }
        return ResponseEntity.ok("success");
    }

    @PatchMapping
    public ResponseEntity<String> biblePatch(){
        bibleService.changeFullName();
        return ResponseEntity.ok("success");
    }

    @GetMapping("/powerpoint/down")
    public ResponseEntity<List<List<Bible>>> getBible (@RequestBody List<BiblePPTDownPostDto> biblePPTDownPostDtoList) throws IOException {

        List<List<Bible>> biblesList = new ArrayList<>();
        List<Bible> bibles;
        for (int i = 0; i < biblePPTDownPostDtoList.size(); i++) {
            log.info("Dto:{}", biblePPTDownPostDtoList.get(i).toString());
            bibles = bibleService.getBible(
                    biblePPTDownPostDtoList.get(i).getTitle(),
                    biblePPTDownPostDtoList.get(i).getStartChapter(),
                    biblePPTDownPostDtoList.get(i).getStartVerse(),
                    biblePPTDownPostDtoList.get(i).getEndChapter(),
                    biblePPTDownPostDtoList.get(i).getEndVerse()
            );
            biblesList.add(bibles);
        }
        bibleService.makePPT(biblesList);

        return ResponseEntity.ok(biblesList);
    }
}
