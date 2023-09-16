package com.gsm.blabla.global;

import com.gsm.blabla.content.dao.ContentDetailRepository;
import com.gsm.blabla.content.dao.ContentRepository;
import com.gsm.blabla.content.domain.Content;
import com.gsm.blabla.content.domain.ContentDetail;
import com.gsm.blabla.crew.dao.ScheduleRepository;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class RepositoryTestSupport {

    @Autowired
    protected ContentRepository contentRepository;

    @Autowired
    protected ContentDetailRepository contentDetailRepository;

    @Autowired
    protected ScheduleRepository scheduleRepository;

    protected ContentDetail createContentDetail(Content content, String title, String description,
        LocalTime startedAt, LocalTime stoppedAt, LocalTime endedAt, Long sequence) {
        ContentDetail contentDetail = ContentDetail.builder()
            .content(content)
            .title(title)
            .description(description)
            .contentUrl("https://www.youtube.com/watch?v=sHpGT4SQwgw")
            .guideSentence("나는 오스틴 입니다. About the Fit의 창업자 입니다.")
            .targetSentence("I'm Jules Ostin. I'm the founder of About the Fit.")
            .startedAt(startedAt)
            .stoppedAt(stoppedAt)
            .endedAt(endedAt)
            .sequence(sequence)
            .build();
        return contentDetail;
    }

    protected Content createContent(String title, String description, String language, Long sequence) {
        return Content.builder()
            .title(title)
            .description(description)
            .language(language)
            .thumbnailURL("https://img.youtube.com/vi/sHpGT4SQwgw/hqdefault.jpg")
            .sequence(sequence)
            .build();
    }
}
