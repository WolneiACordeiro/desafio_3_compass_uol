package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.payload.PostDto;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.service.PostService;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private final WebClient webClient;
    private ModelMapper mapper;
    private final JmsTemplate jmsTemplate;
    public PostServiceImpl(PostRepository postRepository,
                           ModelMapper mapper,
                           WebClient.Builder webClientBuilder,
                           JmsTemplate jmsTemplate
    ){
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
        this.jmsTemplate = jmsTemplate;
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
                    jmsTemplate.convertAndSend("POST_OK", postId);
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
                jmsTemplate.convertAndSend("CREATED", postId);
                return true;
            } catch (Exception e) {
                //jmsTemplate.convertAndSend("FAILED", postId);
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
        jmsTemplate.convertAndSend("POST_FIND", postId);
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
