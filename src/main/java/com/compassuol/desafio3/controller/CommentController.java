package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.payload.CommentDto;
import com.compassuol.desafio3.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
