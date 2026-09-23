package com.miktattooink.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpointsStayOpen() throws Exception {
        mockMvc.perform(get("/api/works")).andExpect(status().isOk());
        mockMvc.perform(get("/api/availability")).andExpect(status().isOk());
    }

    @Test
    void bookingsListIsNoLongerPublic() throws Exception {
        mockMvc.perform(get("/api/bookings")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void adminEndpointsRequireLogin() throws Exception {
        mockMvc.perform(get("/api/admin/bookings"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("WWW-Authenticate"));
        mockMvc.perform(delete("/api/admin/bookings/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(get("/api/admin/bookings").with(httpBasic("tatuatore", "sbagliata")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminManagesSlotsThroughTheApi() throws Exception {
        String day = java.time.LocalDate.now().plusDays(60).toString();
        String body = "{\"date\":\"" + day + "\",\"startTime\":\"09:30\",\"endTime\":\"10:15\"}";

        mockMvc.perform(post("/api/admin/slots").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/admin/slots").with(httpBasic("tatuatore", "password-di-test-lunga"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime").value("09:30"))
                .andExpect(jsonPath("$.endTime").value("10:15"))
                .andExpect(jsonPath("$.booked").value(false));

        mockMvc.perform(post("/api/admin/slots").with(httpBasic("tatuatore", "password-di-test-lunga"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/api/admin/days/" + day + "/slots").with(httpBasic("tatuatore", "password-di-test-lunga")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.removed").value(1));
    }

    @Test
    void adminCanListBookings() throws Exception {
        mockMvc.perform(get("/api/admin/bookings").with(httpBasic("tatuatore", "password-di-test-lunga")))
                .andExpect(status().isOk());
    }
}
