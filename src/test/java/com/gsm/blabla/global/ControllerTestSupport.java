package com.gsm.blabla.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsm.blabla.admin.api.AdminController;
import com.gsm.blabla.agora.api.AgoraController;
import com.gsm.blabla.agora.application.AgoraService;
import com.gsm.blabla.auth.api.AuthController;
import com.gsm.blabla.auth.application.AuthService;
import com.gsm.blabla.crew.api.CrewController;
import com.gsm.blabla.crew.application.CrewService;
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
        AuthController.class
    },
    properties = "spring.profiles.active=local"
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
    protected AuthService authService;
}
