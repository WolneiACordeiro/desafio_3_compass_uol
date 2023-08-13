package com.compassuol.desafio3.integration;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private CountDownLatch latch;

    @Test
    public void testCreateProcessingHistoryEndpoint() throws Exception {
        int numRequests = 100;

        for (int i = 0; i < numRequests; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{postId}", i))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("Post creation request sent to the queue."));
        }
    }

    @Test
    public void testCreateProcessingHistoryEndpointAll() throws Exception {
        int numRequests = 100; // Number of requests to send
        CountDownLatch latch = new CountDownLatch(numRequests);

        // Configure listener for ENABLED queue
        jmsTemplate.setReceiveTimeout(1000);
        jmsTemplate.setReceiveTimeout(1000);

        for (int i = 0; i < numRequests; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{postId}", i))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("Post creation request sent to the queue."));
        }

        // Wait for messages to be processed
        latch.await(10, TimeUnit.SECONDS);
    }

    @JmsListener(destination = "ENABLED")
    public void consumeFromQueueEnabled(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.ENABLED, processingHistoryDto);
        // Signal that the message is processed
        latch.countDown();
    }
}
