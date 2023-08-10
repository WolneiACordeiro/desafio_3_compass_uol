package com.compassuol.desafio3.service;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;

public interface ProcessingHistoryService {
    ProcessingHistoryDto createProcess(Long postId, ProcessingHistoryDto processingHistoryDto);
}
