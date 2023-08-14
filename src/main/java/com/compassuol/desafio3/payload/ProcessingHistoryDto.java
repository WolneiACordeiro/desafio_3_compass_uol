package com.compassuol.desafio3.payload;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessingHistoryDto {
    private Long id;
    private Long postId;
    private LocalDateTime date;
    private String status;
}
