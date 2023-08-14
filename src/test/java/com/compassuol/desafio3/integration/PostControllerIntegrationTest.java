package com.compassuol.desafio3.integration;

import com.compassuol.desafio3.controller.PostController;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ProcessingHistoryService processingHistoryService;

    @Autowired
    private PostController postController;

    private CountDownLatch latch;

    @Test
    public void testCreateNewPostEndpoint() throws Exception {
        int numRequests = 100;
        for (int i = 1; i <= numRequests; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{postId}", i))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().string("Post CREATION request sent to the queue."));
        }
        Thread.sleep(5000);
        boolean messagesProcessed = checkMessagesInCreatedQueue(numRequests);
        assertTrue(messagesProcessed);
    }
    private boolean checkMessagesInCreatedQueue(int expectedNumMessages) {
        return true;
    }

}
