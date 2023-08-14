package com.compassuol.desafio3.payload;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessingHistoryDisplayDto {
    private Long id;
    private LocalDateTime date;
    private String status;
}
