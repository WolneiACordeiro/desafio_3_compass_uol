package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.service.PostService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/{id}")
    public ResponseEntity<String> fetchAndSavePost(@PathVariable(name = "id") Long id) {
        postService.fetchAndSavePost(id);
        return ResponseEntity.ok("Post fetched and saved successfully");
    }
}
