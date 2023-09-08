package com.gsm.blabla.admin.api;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsm.blabla.global.ControllerTestSupport;
import com.gsm.blabla.global.WithCustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdminControllerTest extends ControllerTestSupport {

    @DisplayName("[GET] health check 여부를 확인한다.")
    @Test
    @WithCustomMockUser
    void getHealthCheck() throws Exception {
        mockMvc.perform(
            get("/admin/health-check")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value("OK"));
    }
}
