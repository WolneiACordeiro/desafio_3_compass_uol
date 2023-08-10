package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Comment;
import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.payload.CommentDto;
import com.compassuol.desafio3.repository.CommentRepository;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private ModelMapper mapper;

    private final WebClient webClient;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper modelMapper, WebClient.Builder webClientBuilder) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = modelMapper;
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
    }

    @Override
    public Mono<List<CommentDto>> createComment(Long postId) {
        return webClient.get()
                .uri("/comments?postId={postId}", postId)
                .retrieve()
                .bodyToFlux(CommentDto.class)
                .collectList()
                .flatMap(comments -> {
                    List<Comment> commentEntities = comments.stream()
                            .map(commentDto -> mapToEntity(commentDto))
                            .collect(Collectors.toList());
                    commentEntities = commentRepository.saveAll(commentEntities);
                    List<CommentDto> commentDtos = commentEntities.stream()
                            .map(this::mapToDTO)
                            .collect(Collectors.toList());
                    return Mono.just(commentDtos);
                });
    }

    private CommentDto mapToDTO(Comment comment){
        CommentDto commentDto = mapper.map(comment, CommentDto.class);
        return commentDto;
    }

    private  Comment mapToEntity(CommentDto commentDto){
        Comment comment = mapper.map(commentDto, Comment.class);
        return comment;
    }
}
