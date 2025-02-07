package bulletin.bulletin.domain.bible.controller;

import bulletin.bulletin.domain.bible.dto.BiblePPTDownPostDto;
import bulletin.bulletin.domain.bible.service.BibleService;
import bulletin.bulletin.domain.bible.entity.Bible;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${custom.resource.path.use}")
    private boolean customResourcePathUse;
    @Value("${bible.file.path}")
    private String bibleFilePath;
    @Value("${classpath.bible.file.path}")
    private String classpathBibleFilePath;

    @PostMapping
    public ResponseEntity<String> biblePost() {
        String filePath;
        try {
            for (int i = 1; i < 67; i++) {
                if (customResourcePathUse) {
                    filePath = bibleFilePath;

                } else {
                    filePath = classpathBibleFilePath;

                }
                filePath += i + ".txt";
                bibleService.insertTextFromClasspathFile(filePath);
            }
            log.info("BIBLE LIST CREATED");
        } catch (Exception e) {
            log.debug("BIBLE POST ERROR" + e);

            return ResponseEntity.ok("Couldn't create Bible List");
        }

        return ResponseEntity.ok("success");
    }

    @PatchMapping
    public ResponseEntity<String> changeShortNameToFullName() {
        bibleService.changeShortNameToFullName();
        return ResponseEntity.ok("success");
    }

    @PostMapping("/powerpoint/download")
    public ResponseEntity<?> getBible(@RequestBody List<BiblePPTDownPostDto> biblePPTDownPostDtoList) throws IOException {

        List<List<Bible>> biblesList = new ArrayList<>();
        List<Bible> bibles;
        try {
            for (BiblePPTDownPostDto biblePPTDownPostDto : biblePPTDownPostDtoList) {
                log.info("Dto:{}", biblePPTDownPostDto.toString());
                bibles = bibleService.getBible(
                        biblePPTDownPostDto.getTitle(),
                        biblePPTDownPostDto.getStartChapter(),
                        biblePPTDownPostDto.getStartVerse(),
                        biblePPTDownPostDto.getEndChapter(),
                        biblePPTDownPostDto.getEndVerse()
                );
                biblesList.add(bibles);
            }
            bibleService.makePPT(biblesList);
        } catch (Exception e) {
            log.debug("getBible SYSTEM ERROR" + e);
            return ResponseEntity.ok("SYSTEM ERROR");
        }

        return ResponseEntity.ok(biblesList);
    }
}
