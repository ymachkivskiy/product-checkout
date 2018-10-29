package org.jm.interview.pccheckout.acceptance;

import org.jm.interview.pccheckout.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@AutoConfigureMockMvc
public class AcceptanceTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void name() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/products")).andExpect(status().isOk());


    }


    // write test cases here
}