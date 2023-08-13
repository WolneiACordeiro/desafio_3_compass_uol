package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.entity.ProcessingHistory;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.repository.ProcessingHistoryRepository;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProcessingHistoryServiceImpl implements ProcessingHistoryService {
    private ProcessingHistoryRepository processingHistoryRepository;
    private PostRepository postRepository;
    private ModelMapper mapper;
    public  ProcessingHistoryServiceImpl(ProcessingHistoryRepository  processingHistoryRepository, PostRepository postRepository, ModelMapper mapper){
        this.processingHistoryRepository = processingHistoryRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    @Async
    public ProcessingHistoryDto createProcessQueue(Long postId, PostState status, ProcessingHistoryDto processingHistoryDto) {
            ProcessingHistory processingHistory = mapToEntity(processingHistoryDto);
            processingHistory.setPostId(postId);
            processingHistory.setDate(LocalDateTime.now());
            processingHistory.setStatus(String.valueOf(status));
            ProcessingHistory newProcess = processingHistoryRepository.save(processingHistory);
            return mapToDTO(newProcess);
    }

    //Entity to DTO
    private ProcessingHistoryDto mapToDTO(ProcessingHistory processingHistory){
        ProcessingHistoryDto processingHistoryDto = mapper.map(processingHistory, ProcessingHistoryDto.class);
        return processingHistoryDto;
    }

    //DTO to Entity
    private ProcessingHistory mapToEntity(ProcessingHistoryDto processingHistoryDto){
        ProcessingHistory processingHistory = mapper.map(processingHistoryDto, ProcessingHistory.class);
        return processingHistory;
    }
}
