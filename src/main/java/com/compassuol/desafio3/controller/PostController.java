package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.QueueConsumer;
import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.service.PostService;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@EnableJms
public class PostController {
    private PostService postService;
    private final QueueConsumer queueConsumer;
    private final JmsTemplate jmsTemplate;
    private final ProcessingHistoryService processingHistoryService;

    public PostController(PostService postService, QueueConsumer queueConsumer, JmsTemplate jmsTemplate, ProcessingHistoryService processingHistoryService) {
        this.postService = postService;
        this.queueConsumer = queueConsumer;
        this.jmsTemplate = jmsTemplate;
        this.processingHistoryService = processingHistoryService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> createProcessingHistory(@PathVariable Long postId) {
        postService.createEmptyPost(postId).subscribe();
        return ResponseEntity.ok("Processing history creation request sent to the queue.");
    }

    @JmsListener(destination = "CREATED")
    public void consumeFromQueueCreated(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.CREATED, processingHistoryDto);
        postService.processBasedOnDataExistence(postId).subscribe();
    }

    @JmsListener(destination = "POST_FIND")
    public void consumeFromQueuePostFind(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.POST_FIND, processingHistoryDto);
        postService.createPost(postId).subscribe(newPostDto -> {
            processingHistoryService.createProcessQueue(postId, PostState.ENABLED, processingHistoryDto);
        });
    }

    @JmsListener(destination = "POST_OK")
    public void consumeFromQueuePostOk(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.POST_OK, processingHistoryDto);
    }

    @JmsListener(destination = "FAILED")
    public void consumeFromQueueFailed(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.FAILED, processingHistoryDto);
    }

}
