package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.QueueConsumer;
import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.entity.ProcessingHistory;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.service.PostService;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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

    /*@PostMapping("/{id}")
    public Mono<ResponseEntity<String>> fetchAndSavePost(@PathVariable Long id) {
        /*System.out.println("Received request for post ID: " + id);
        return postService.createPost(id)
                .doOnNext(postDto -> System.out.println("Async operation completed for post ID: " + id))
                .map(postDto -> ResponseEntity.status(HttpStatus.CREATED).body("Fetching and saving post in progress"));
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("COMEÇOU");
        jmsTemplate.convertAndSend("CREATED", "ESTOU AQUI");
        return null;
    }*/

    @PostMapping("/{postId}")
    public ResponseEntity<String> createProcessingHistory(@PathVariable Long postId) {
        jmsTemplate.convertAndSend("CREATED", postId);
        return ResponseEntity.ok("Processing history creation request sent to the queue.");
    }
    @JmsListener(destination = "CREATED")
    public void consumeFromQueueCreated(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.CREATED, processingHistoryDto);
    }

}
