package com.compassuol.desafio3.entity;

import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueueMessage {
    private Long postId;
    private ProcessingHistoryDto processingHistoryDto;
}
