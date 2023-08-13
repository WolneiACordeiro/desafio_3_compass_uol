package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.service.PostService;
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
    private final JmsTemplate jmsTemplate;

    public PostController(PostService postService,
                          JmsTemplate jmsTemplate) {
        this.postService = postService;
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> createNewPost(@PathVariable Long postId) {
        jmsTemplate.convertAndSend("CREATED", postId);
        return ResponseEntity.ok("Post CREATION request sent to the queue.");
    }

    @PostMapping("/test")
    public void testCreateProcessingHistoryEndpointBrutal() throws Exception {
        int numRequests = 100;
        for (int i = 1; i <= numRequests; i++) {
            jmsTemplate.convertAndSend("CREATED", i);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId) {
        jmsTemplate.convertAndSend("DISABLED", postId);
        return ResponseEntity.ok("Post DISABLE request sent to the queue.");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> reprocessPost(@PathVariable Long postId) {
        jmsTemplate.convertAndSend("UPDATING", postId);
        return ResponseEntity.ok("Post UPDATE request sent to the queue.");
    }

    @GetMapping
    public List<PostDto> getAllPosts(){
        return postService.getAllPosts();
    }


}
