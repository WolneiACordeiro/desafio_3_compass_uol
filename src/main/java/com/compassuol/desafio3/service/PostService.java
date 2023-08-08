package com.compassuol.desafio3.service;

import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void fetchAndSavePost(Long postId) {
        String apiUrl = "https://jsonplaceholder.typicode.com/posts/" + postId;

        RestTemplate restTemplate = new RestTemplate();
        Post post = restTemplate.getForObject(apiUrl, Post.class);

        if (post != null) {
            postRepository.save(post);
        }
    }
}
