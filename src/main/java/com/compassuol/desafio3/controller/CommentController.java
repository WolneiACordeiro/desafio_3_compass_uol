package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{id}")
    public Mono<ResponseEntity<String>> fetchAndSaveComments(@PathVariable Long id) {
        return commentService.createComment(id)
                .then(Mono.just(ResponseEntity.ok("Fetching and saving comments in progress")));
    }
}
