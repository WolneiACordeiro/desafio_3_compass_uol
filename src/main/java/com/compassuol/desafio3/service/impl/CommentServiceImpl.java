package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Comment;
import com.compassuol.desafio3.payload.CommentDisplayDto;
import com.compassuol.desafio3.payload.CommentDto;
import com.compassuol.desafio3.repository.CommentRepository;
import com.compassuol.desafio3.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final WebClient webClient;
    private final ModelMapper mapper;

    private final JmsTemplate jmsTemplate;

    public CommentServiceImpl(CommentRepository commentRepository,
                              ModelMapper modelMapper,
                              WebClient.Builder webClientBuilder,
                              JmsTemplate jmsTemplate) {
        this.commentRepository = commentRepository;
        this.mapper = modelMapper;
        this.webClient = webClientBuilder.build();
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public List<CommentDto> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream().map(comment -> mapToDTO(comment)).collect(Collectors.toList());
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
                    jmsTemplate.convertAndSend("ENABLED", postId);
                    return Mono.just(commentDtos);
                });
    }

    @Override
    public Mono<Void> processBasedOnDataExistence(Long postId) {
        Duration timeoutDuration = Duration.ofSeconds(5);
        return checkIfDataExists(postId, timeoutDuration)
                .flatMap(dataExists -> {
                    if (dataExists) {
                        return handlePositiveCase(postId);
                    } else {
                        return handleNegativeCase(postId);
                    }
                });
    }

    public Mono<Boolean> checkIfDataExists(Long postId, Duration timeoutDuration) {
        return webClient.head()
                .uri("/comments?postId={postId}", postId)
                .exchangeToMono(response -> Mono.just(response.statusCode().is2xxSuccessful()))
                .timeout(timeoutDuration)
                .onErrorResume(TimeoutException.class, error -> {
                    return Mono.just(false);
                })
                .onErrorResume(WebClientRequestException.class, error -> {
                    return Mono.just(false);
                });
    }

    public Mono<Void> handlePositiveCase(Long postId) {
        jmsTemplate.convertAndSend("COMMENTS_OK", postId);
        return Mono.empty();
    }

    public Mono<Void> handleNegativeCase(Long postId) {
        jmsTemplate.convertAndSend("FAILED", postId);
        return Mono.empty();
    }

    private CommentDto mapToDTO(Comment comment){
        CommentDto commentDto = mapper.map(comment, CommentDto.class);
        return commentDto;
    }

    private  Comment mapToEntity(CommentDto commentDto){
        Comment comment = mapper.map(commentDto, Comment.class);
        return comment;
    }

    public CommentDisplayDto mapCommentToDisplayDTO(Comment comment) {
        CommentDisplayDto commentDisplayDto = mapper.map(comment, CommentDisplayDto.class);
        return commentDisplayDto;
    }

}
