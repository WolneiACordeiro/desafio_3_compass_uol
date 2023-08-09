package com.compassuol.desafio3.service;

import com.compassuol.desafio3.payload.PostDto;
import reactor.core.publisher.Mono;

public interface PostService {
    Mono<PostDto> createPost(Long postId);

}
