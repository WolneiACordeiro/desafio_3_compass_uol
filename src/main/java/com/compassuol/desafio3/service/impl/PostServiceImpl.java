package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Comment;
import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.entity.ProcessingHistory;
import com.compassuol.desafio3.payload.CommentDto;
import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.repository.CommentRepository;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.repository.ProcessingHistoryRepository;
import com.compassuol.desafio3.service.CommentService;
import com.compassuol.desafio3.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private ProcessingHistoryRepository processingHistoryRepository;
    private final WebClient webClient;
    private ModelMapper mapper;
    private CommentService commentService;
    private final JmsTemplate jmsTemplate;
    public PostServiceImpl(PostRepository postRepository,
                           CommentRepository commentRepository,
                           ProcessingHistoryRepository processingHistoryRepository,
                           ModelMapper mapper,
                           WebClient.Builder webClientBuilder,
                           CommentService commentService,
                           JmsTemplate jmsTemplate
    ){
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.processingHistoryRepository = processingHistoryRepository;
        this.mapper = mapper;
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
        this.commentService = commentService;
        this.jmsTemplate = jmsTemplate;
    }

    /*@Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
    }*/

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream().map(post -> {
            Set<CommentDto> commentDtos = commentRepository.findByPostId(post.getId()).stream()
                    .map(comment -> commentService.mapToDTO(comment))
                    .collect(Collectors.toSet());

            PostDto postDto = mapToDTO(post);
            postDto.setComments(commentDtos);

            return postDto;
        }).collect(Collectors.toList());
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
                .uri("/posts/{postId}", postId)
                .exchangeToMono(response -> Mono.just(response.statusCode().is2xxSuccessful()));
    }

    public Mono<Void> handlePositiveCase(Long postId) {
        jmsTemplate.convertAndSend("POST_OK", postId);
        return Mono.empty();
    }

    public Mono<Void> handleNegativeCase(Long postId) {
        jmsTemplate.convertAndSend("FAILED", postId);
        return Mono.empty();
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
