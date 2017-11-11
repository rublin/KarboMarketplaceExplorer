package org.rublin.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rublin.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

@Ignore
@SpringBootTest(classes = Main.class)
@WebAppConfiguration
public class OrderRestControllerTest {

    @Autowired
    private WebApplicationContext context;

//    @Autowired
//    private ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();
    }

    @Test
    public void findOptimalSellOrders() throws Exception {

    }

    @Test
    public void findOptimalBuyOrders() throws Exception {
    }

}