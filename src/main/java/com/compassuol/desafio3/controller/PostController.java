package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.service.PostService;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@EnableJms
public class PostController {
    private PostService postService;
    private final ProcessingHistoryService processingHistoryService;
    private final JmsTemplate jmsTemplate;


    public PostController(PostService postService,
                          JmsTemplate jmsTemplate, ProcessingHistoryService processingHistoryService) {
        this.postService = postService;
        this.processingHistoryService = processingHistoryService;
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> createNewPost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The Id must be between 1 and 100.");
        }

        if (postService.isPostExists(postId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The post with the following Id is already registered in the database : " + postId);
        }

        jmsTemplate.convertAndSend("CREATED", postId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post CREATION request sent to the queue.");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The Id must be between 1 and 100.");
        }

        if (!postService.isPostExists(postId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This post is not present in the database : " + postId);
        }

        String firstStatus = processingHistoryService.getFirstStatus(postId).getBody();
        if ("ENABLED".equals(firstStatus)) {
            jmsTemplate.convertAndSend("DISABLED", postId);
            return ResponseEntity.status(HttpStatus.OK).body("Post DISABLE request sent to the queue.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot disable the post because it is not in ENABLED state.");
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> reprocessPost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The Id must be between 1 and 100.");
        }

        if (!postService.isPostExists(postId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This post is not present in the database : " + postId);
        }

        String firstStatus = processingHistoryService.getFirstStatus(postId).getBody();
        if ("ENABLED".equals(firstStatus) || "DISABLED".equals(firstStatus)) {
            jmsTemplate.convertAndSend("UPDATING", postId);
            return ResponseEntity.status(HttpStatus.OK).body("Post UPDATE request sent to the queue.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot disable the post because it is not in ENABLED state.");
        }
    }

    @GetMapping
    public List<PostDto> getAllPosts(){
        return postService.getAllPosts();
    }


}
