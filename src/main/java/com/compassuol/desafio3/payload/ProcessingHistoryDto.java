package com.compassuol.desafio3.payload;

import com.compassuol.desafio3.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessingHistoryDto {
    private Long id;
    private Long postId;
    private LocalDateTime date;
    private String status;
}
