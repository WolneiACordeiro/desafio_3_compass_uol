package com.compassuol.desafio3.service;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.entity.ProcessingHistory;
import com.compassuol.desafio3.payload.ProcessingHistoryDisplayDto;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;

import java.util.List;

public interface ProcessingHistoryService {
    ProcessingHistoryDto createProcessQueue(Long postId, PostState status, ProcessingHistoryDto processingHistoryDto);
    List<ProcessingHistoryDto> getAllProcess();
    ProcessingHistoryDisplayDto mapProcessHistoryToDisplayDTO(ProcessingHistory processingHistory);
}
