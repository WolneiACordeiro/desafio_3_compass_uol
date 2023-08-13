package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.payload.CommentDto;
import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> getAllComments(){
        return commentService.getAllComments();
    }
}
