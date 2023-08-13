package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Comment;
import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.payload.CommentDisplayDto;
import com.compassuol.desafio3.payload.CommentDto;
import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.repository.CommentRepository;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;

    private final WebClient webClient;
    private ModelMapper mapper;

    private final JmsTemplate jmsTemplate;

    public CommentServiceImpl(CommentRepository commentRepository,
                              ModelMapper modelMapper,
                              WebClient.Builder webClientBuilder,
                              JmsTemplate jmsTemplate) {
        this.commentRepository = commentRepository;
        this.mapper = modelMapper;
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
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
        return checkIfDataExists(postId)
                .flatMap(dataExists -> {
                    if (dataExists) {
                        return handlePositiveCase(postId);
                    } else {
                        return handleNegativeCase(postId);
                    }
                });
    }

    public Mono<Boolean> checkIfDataExists(Long postId) {
        return webClient.head()
                .uri("/comments?postId={postId}", postId)
                .exchangeToMono(response -> Mono.just(response.statusCode().is2xxSuccessful()));
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
