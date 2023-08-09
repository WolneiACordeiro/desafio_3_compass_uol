package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /*@PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto){
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }*/

    @PostMapping("/{id}")
    public Mono<ResponseEntity<String>> fetchAndSavePost(@PathVariable Long id) {
        System.out.println("Received request for post ID: " + id);
        return postService.createPost(id)
                .doOnNext(postDto -> System.out.println("Async operation completed for post ID: " + id))
                .thenReturn(ResponseEntity.ok("Fetching and saving post in progress"));
    }
}
