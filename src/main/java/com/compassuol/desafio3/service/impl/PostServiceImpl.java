package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.payload.CommentDisplayDto;
import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.payload.ProcessingHistoryDisplayDto;
import com.compassuol.desafio3.repository.CommentRepository;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.repository.ProcessingHistoryRepository;
import com.compassuol.desafio3.service.CommentService;
import com.compassuol.desafio3.service.PostService;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private ProcessingHistoryRepository processingHistoryRepository;
    private final WebClient webClient;
    private ModelMapper mapper;
    private CommentService commentService;
    private ProcessingHistoryService processingHistoryService;
    private final JmsTemplate jmsTemplate;
    @Value("${external.api.base-url}")
    private String externalApiBaseUrl;
    public PostServiceImpl(PostRepository postRepository,
                           CommentRepository commentRepository,
                           ProcessingHistoryRepository processingHistoryRepository,
                           ModelMapper mapper,
                           WebClient.Builder webClientBuilder,
                           CommentService commentService,
                           ProcessingHistoryService processingHistoryService,
                           JmsTemplate jmsTemplate
    ){
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.processingHistoryRepository = processingHistoryRepository;
        this.mapper = mapper;
        this.webClient = webClientBuilder.build();
        this.commentService = commentService;
        this.processingHistoryService = processingHistoryService;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().sorted(Comparator.comparing(Post::getId)).map(post -> {
            List<CommentDisplayDto> commentDtos = commentRepository.findByPostIdOrderByIdAsc(post.getId()).stream()
                    .map(comment -> commentService.mapCommentToDisplayDTO(comment))
                    .collect(Collectors.toList());
            List<ProcessingHistoryDisplayDto> processDtos = processingHistoryRepository.findByPostIdOrderByDateAsc(post.getId()).stream()
                    .map(processingHistory -> processingHistoryService.mapProcessHistoryToDisplayDTO(processingHistory))
                    .collect(Collectors.toList());
            PostDto postDto = mapToDTO(post);
            postDto.setComments(commentDtos);
            postDto.setHistory(processDtos);
            return postDto;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isPostExists(Long id) {
        return postRepository.existsById(id);
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
                    jmsTemplate.convertAndSend("COMMENTS_FIND", postId);
                    return Mono.just(mapToDTO(newPost));
                });
    }

    @Override
    public Mono<Boolean> createEmptyPost(Long postId) {
        Post newPost = new Post();
        newPost.setId(postId);
        newPost.setTitle(null);
        newPost.setBody(null);

        return Mono.fromCallable(() -> {
            try {
                postRepository.save(newPost);
                jmsTemplate.convertAndSend("POST_FIND", postId);
                return true;
            } catch (Exception e) {
                jmsTemplate.convertAndSend("FAILED", postId);
                return false;
            }
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
                .uri("/posts/{postId}", postId)
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
        jmsTemplate.convertAndSend("POST_OK", postId);
        return Mono.empty();
    }

    public Mono<Void> handleNegativeCase(Long postId) {
        jmsTemplate.convertAndSend("FAILED", postId);
        return Mono.empty();
    }

    private PostDto mapToDTO(Post post){
        PostDto postDto = mapper.map(post, PostDto.class);
        return postDto;
    }

    private Post mapToEntity(PostDto postDto){
        Post post = mapper.map(postDto, Post.class);
        return post;
    }
}
