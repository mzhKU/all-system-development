package ch.mzh.avclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private int test;

    @Test
    void dailyEndpointReturnsMockTimeSeries() throws Exception {
        mockMvc.perform(get("/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Meta Data']").exists());
        assertTrue(1==0);
    }
}