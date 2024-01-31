package bulletin.bulletin.domain.bible.service;

import bulletin.bulletin.domain.bible.BibleFullName;
import bulletin.bulletin.domain.bible.BibleShortName;
import bulletin.bulletin.domain.bible.entity.Bible;
import bulletin.bulletin.domain.bible.repository.BibleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @Transactional
    public void insertTextFromFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            // txt의 charset이 EUC-KR임
            Files.lines(path, Charset.forName("EUC-KR")).forEach(line -> {
                columnDistinguish(line);
            });
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
        for(int j =1; j<str.length; j++){
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

    public void changeShortNameToFullName(){
        BibleShortName[] bibleShortName = BibleShortName.values();
        BibleFullName[] bibleFullName = BibleFullName.values();

        for (int i=0; i<bibleShortName.length; i++) {
            String shortName = String.valueOf(bibleShortName[i]);
            String fullName = String.valueOf(bibleFullName[i]);
            bibleRepository.changeShortNameToFullName(shortName, fullName);
        }

        // <> 안에있는 소제목 제거
        bibleRepository.deleteAllow();
    }

    public void makePPT(List<List<Bible>> biblesList) throws IOException {
        // 저장 위치 설정
        String homeDirectory = System.getProperty("user.home");
        String downloadDirectory = homeDirectory + "/Downloads";

        // 파일 이름 설정
        String filePath = downloadDirectory + "/output.pptx";

        XMLSlideShow ppt = new XMLSlideShow();

        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout titleLayout = defaultMaster.getLayout(SlideLayout.TITLE);

        // 슬라이드 생성
        XSLFSlide slide = ppt.createSlide(titleLayout);
        XSLFGroupShape groupShape = slide.createGroup();

        // 슬라이드 제목 생성
        XSLFTextShape title1 = slide.getPlaceholder(0);
        XSLFTextShape title2 = slide.getPlaceholder(1);
        title1.setText(biblesList.get(0).get(0).getTitle());
        title2.setText(biblesList.get(0).get(0).getContent());
        title1.setAnchor(new Rectangle2D.Double(10, 400, 180, 130));

        groupShape.addShape(title1);
        groupShape.addShape(title2);

        // 도형 생성 (예: 직사각형)
        XSLFAutoShape shape1 = slide.createAutoShape();
        XSLFAutoShape shape2 = slide.createAutoShape();
        XSLFSimpleShape simpleShape1 = shape1;
        XSLFSimpleShape simpleShape2 = shape2;

        // 슬라이드 색상 지정
        Color backgroundColor = new Color(250, 80, 195);
        slide.getBackground().setFillColor(backgroundColor);

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
        simpleShape1.setLineColor(skyBlue);
        simpleShape2.setLineColor(skyBlue);

        // 도형 배경색 설정
        Color darkBlue = new Color(25, 80, 120); // RGB로 진한 파랑색
        shape1.setFillColor(Color.WHITE);
        shape2.setFillColor(darkBlue);

        groupShape.addShape(shape1);
        groupShape.addShape(shape2);

        // PPT 다운로드
        FileOutputStream out = new FileOutputStream(filePath);
        ppt.write(out);
        log.info("PPT DOWN");
    }

    @Transactional
    public List<Bible> getBible(String title, String startChapter, String startVerse, String endChapter, String endVerse){
        return bibleRepository.findByBiblePPTDownPostDto(title, startChapter, startVerse, endChapter, endVerse);
    }
}
