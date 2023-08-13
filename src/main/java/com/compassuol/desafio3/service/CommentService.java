package com.compassuol.desafio3.service;

import com.compassuol.desafio3.payload.CommentDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommentService {
    Mono<Void> processBasedOnDataExistence(Long postId);
    Mono<List<CommentDto>> createComment(Long postId);
}
