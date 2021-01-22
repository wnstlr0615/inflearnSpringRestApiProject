package com.joon.demoinflearnrestapi.index;

import com.joon.demoinflearnrestapi.common.BaseControllerTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class IndexControllerTest extends BaseControllerTest {
    @Test
    public void index() throws Exception {
            mvc.perform(get("/api/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists())
        ;

    }
}