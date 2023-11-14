package com.gsm.blabla.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.admin.api.AdminController;
import com.gsm.blabla.admin.application.FcmService;
import com.gsm.blabla.agora.api.AgoraController;
import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.auth.api.AuthController;
import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.content.dto.ContentDetailDto;
import com.gsm.blabla.content.dto.ContentsResponseDto;
import com.gsm.blabla.crew.api.CrewController;
import com.gsm.blabla.crew.api.ScheduleController;
import com.gsm.blabla.crew.application.CrewService;
import com.gsm.blabla.content.api.ContentController;
import com.gsm.blabla.content.application.ContentService;
import com.gsm.blabla.crew.application.ScheduleService;
import com.gsm.blabla.report.api.ReportController;
import com.gsm.blabla.report.application.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = {
        CrewController.class,
        AgoraController.class,
        ReportController.class,
        AdminController.class,
        ContentController.class,
        AdminController.class,
        AuthController.class,
        ScheduleController.class
    },
    properties = "spring.profiles.active=test"
)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CrewService crewService;

    @MockBean
    protected AgoraService agoraService;

    @MockBean
    protected ReportService reportService;

    @MockBean
    protected ContentService contentService;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected ScheduleService scheduleService;

    @MockBean
    protected FcmService fcmService;

    protected ContentsResponseDto createContentsResponseDto(
            Long id, String title, String description, Double progress) {
        return ContentsResponseDto.builder()
                .id(id)
                .title(title)
                .description(description)
                .thumbnailUrl("https://img.youtube.com/vi/29ZlFKsc_ps/hqdefault.jpg")
                .progress(progress)
                .build();
    }

    protected ContentDetailDto createcontentDetailDto(
            String title, String description, Boolean isCompleted) {
        return ContentDetailDto.builder()
                .title(title)
                .description(description)
                .thumbnailUrl("https://img.youtube.com/vi/0B2JUyXVp-c/hqdefault.jpg")
                .isCompleted(isCompleted)
                .build();
    }

}
