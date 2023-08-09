package com.compassuol.desafio3.payload;

import lombok.Data;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String body;
}
