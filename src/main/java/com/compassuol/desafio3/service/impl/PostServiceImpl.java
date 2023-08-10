package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.service.PostService;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private final WebClient webClient;
    private ModelMapper mapper;
    private final JmsTemplate jmsTemplate;
    private final ProcessingHistoryService processingHistoryService;
    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper, WebClient.Builder webClientBuilder, JmsTemplate jmsTemplate, ProcessingHistoryService processingHistoryService){
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
        this.jmsTemplate = jmsTemplate;
        this.processingHistoryService = processingHistoryService;
    }

    @Override
    public Mono<PostDto> createPost(Long postId) {
            return webClient.get()
                    .uri("/posts/{postId}", postId)
                    .retrieve()
                    .bodyToMono(PostDto.class)
                    .flatMap(apiPostDto -> {
                        Post post = mapToEntity(apiPostDto);
                        Post newPost = postRepository.save(post);
                        return Mono.just(mapToDTO(newPost));
                    });
    }

    //Entity to DTO
    private PostDto mapToDTO(Post post){
        PostDto postDto = mapper.map(post, PostDto.class);
        return postDto;
    }

    //DTO to Entity
    private Post mapToEntity(PostDto postDto){
        Post post = mapper.map(postDto, Post.class);
        return post;
    }
}
