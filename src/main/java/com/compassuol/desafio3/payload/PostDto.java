package com.compassuol.desafio3.payload;

import lombok.Data;

import java.util.List;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String body;
    private List<CommentDisplayDto> comments;
    private List<ProcessingHistoryDisplayDto> history;
}
