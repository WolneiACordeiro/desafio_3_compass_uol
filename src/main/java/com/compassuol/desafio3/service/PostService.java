package com.compassuol.desafio3.service;

import com.compassuol.desafio3.payload.PostDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PostService {
    Mono<Boolean> createEmptyPost(Long postId);
    Mono<Void> processBasedOnDataExistence(Long postId);
    Mono<PostDto> createPost(Long postId);
    List<PostDto> getAllPosts();

}
