package com.compassuol.desafio3.payload;

import lombok.Data;

import java.util.Set;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String body;
    private Set<CommentDto> comments;
}
