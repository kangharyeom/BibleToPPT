package bulletin.bulletin.domain.bible.service;

import bulletin.bulletin.domain.bible.BibleFullName;
import bulletin.bulletin.domain.bible.BibleShortName;
import bulletin.bulletin.domain.bible.entity.Bible;
import bulletin.bulletin.domain.bible.repository.BibleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class BibleService {
    private final BibleRepository bibleRepository;

    public void makePPT(List<List<Bible>> biblesList) throws IOException {
        // 저장 위치 설정
        String homeDirectory = System.getProperty("user.home");
        String downloadDirectory = homeDirectory + "/Downloads";

        // 파일 이름 설정
        String filePath = downloadDirectory + "/output.pptx";

        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout titleLayout = defaultMaster.getLayout(SlideLayout.TITLE);

        for (List<Bible> bibles : biblesList) {
            for (Bible bible : bibles) {
                // 도형 생성 (예: 직사각형)
                XSLFSlide slide = ppt.createSlide(titleLayout);
                XSLFAutoShape shape1 = slide.createAutoShape();
                XSLFAutoShape shape2 = slide.createAutoShape();

                // 도형 타입 설정 (직사각형)
                shape1.setShapeType(ShapeType.ROUND_RECT);
                shape2.setShapeType(ShapeType.ROUND_RECT);

                // 도형 크기 및 위치 설정
                shape1.setAnchor(new Rectangle2D.Double(10, 400, 700, 130));
                shape2.setAnchor(new Rectangle2D.Double(10, 400, 180, 130));

                // 도형 두께 설정
                shape1.setLineWidth(6.0);
                shape2.setLineWidth(6.0);
                Color skyBlue = new Color(179, 244, 255); // RGB로 진한 파랑색

                // 도형 배경색 설정
                Color darkBlue = new Color(25, 80, 120); // RGB로 진한 파랑색
                shape1.setFillColor(Color.WHITE);
                shape2.setFillColor(darkBlue);

                // 제목 텍스트를 포함할 도형
                XSLFAutoShape titleShape = slide.createAutoShape();
                titleShape.setShapeType(ShapeType.ROUND_RECT);
                titleShape.setAnchor(new Rectangle2D.Double(10, 400, 180, 130));
                titleShape.setLineWidth(6.0);

                // 제목 텍스트 설정
                XSLFTextParagraph titlePara = titleShape.addNewTextParagraph();
                XSLFTextRun titleRun = titlePara.addNewTextRun();
                titleRun.setText(bible.getTitle());
                titlePara.setTextAlign(TextParagraph.TextAlign.CENTER);
                titleShape.setVerticalAlignment(VerticalAlignment.MIDDLE);

                // 내용 텍스트 도형
                XSLFAutoShape contentShape = slide.createAutoShape();
                contentShape.setAnchor(new Rectangle2D.Double(190, 400, 520, 130));

                // 내용 텍스트 설정
                XSLFTextParagraph contentPara = contentShape.addNewTextParagraph();
                XSLFTextRun contentRun = contentPara.addNewTextRun();
                contentRun.setText(bible.getContent());
                contentPara.setTextAlign(TextParagraph.TextAlign.CENTER);
                contentShape.setVerticalAlignment(VerticalAlignment.MIDDLE);

                // 폰트 색상 설정 (선택사항)
                titleRun.setFontColor(Color.BLACK);
                contentRun.setFontColor(Color.BLACK);

                // 제목 폰트 설정
                titleRun.setFontSize(30.0);  // 포인트 단위
                titleRun.setFontFamily("맑은 고딕");  // 폰트 패밀리 설정
                titleRun.setBold(true);  // 굵게 설정

                // 내용 폰트 설정
                contentRun.setFontSize(30.0);  // 포인트 단위
                contentRun.setFontFamily("맑은 고딕");  // 폰트 패밀리 설정
                contentRun.setBold(true);  // 굵게 설정

                // 슬라이드 색상 지정
                Color backgroundColor = new Color(250, 80, 195);
                slide.getBackground().setFillColor(backgroundColor);
            }
        }
        // PPT 다운로드
        FileOutputStream out = new FileOutputStream(filePath);
        ppt.write(out);
        log.info("PPT DOWN");
    }

    @Transactional
    public void insertTextFromFile(String classpathFilePath) {
        Path path = Paths.get(classpathFilePath);
        try {
            Files.lines(path, Charset.forName("EUC-KR")).forEach(line -> {
                columnDistinguish(line);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void insertTextFromClasspathFile(String classpathFilePath) {
        try {
            Resource resource = new ClassPathResource(classpathFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Charset.forName("EUC-KR")));

            reader.lines().forEach(line -> {
                columnDistinguish(line);
            });

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // txt 파일의 line에서 chapter, verse, title, content를 추출하는 로직
    @Transactional
    private void columnDistinguish(String line) {
        Bible bible = new Bible();
        StringBuilder title = new StringBuilder();   // ex) 요한복음
        StringBuilder chapter = new StringBuilder(); // ex) 1장
        StringBuilder verse = new StringBuilder();   // ex) 1절
        StringBuilder content = new StringBuilder(); // ex) 가나다 라마바 사아자

        // 띄어쓰기 단위로 분리 (결과 ["테1:1", "가나다", "라마바", "사아자", "차카하"])
        String[] str = line.split(" ");

        // : 단위로 분리 (결과 ["요1", "1"])
        String[] str1 = str[0].split(":");

        // verse 추출
        verse.append(str1[1]);
        log.info("verse:{}", verse);

        // title, chapter 추출
        // "요1" 을 문자와 숫자로 나눈다.
        for (int i = 0; i < str1[0].length(); i++) {
            char ch = str1[0].charAt(i);

            // ch의 값이 문자면 title
            if (Character.isLetter(ch)) {
                title.append(ch);

                // ch의 값이 숫자면 chapter
            } else if (Character.isDigit(ch)) {
                chapter.append(ch);
            }
        }
        log.info("title:{}", title);
        log.info("chapter:{}", chapter);

        // content 추출
        // str[0]을 제외한 나머지가 content 입니다.
        for (int j = 1; j < str.length; j++) {
            content.append(str[j] + " ");
        }
        log.info("content:{}", content);

        // DB 저장
        bible.setTitle(String.valueOf(title));
        bible.setChapter(String.valueOf(chapter));
        bible.setVerse(String.valueOf(verse));
        bible.setContent(String.valueOf(content));

        bibleRepository.save(bible);
    }

    public void changeShortNameToFullName() {
        BibleShortName[] bibleShortName = BibleShortName.values();
        BibleFullName[] bibleFullName = BibleFullName.values();

        for (int i = 0; i < bibleShortName.length; i++) {
            String shortName = String.valueOf(bibleShortName[i]);
            String fullName = String.valueOf(bibleFullName[i]);
            bibleRepository.changeShortNameToFullName(shortName, fullName);
        }

        // <> 안에있는 소제목 제거
        bibleRepository.deleteAllow();
    }


    @Transactional
    public List<Bible> getBible(String title, String startChapter, String startVerse, String endChapter, String endVerse) {
        return bibleRepository.findByBiblePPTDownPostDto(title, startChapter, startVerse, endChapter, endVerse);
    }
}
