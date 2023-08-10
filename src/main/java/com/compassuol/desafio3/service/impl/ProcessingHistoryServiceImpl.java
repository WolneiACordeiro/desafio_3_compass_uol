package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.Post;
import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.entity.ProcessingHistory;
import com.compassuol.desafio3.exception.ResourceNotFoundException;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.repository.ProcessingHistoryRepository;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;

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
    public ProcessingHistoryDto createProcess(Long postId, ProcessingHistoryDto processingHistoryDto) {
        ProcessingHistory processingHistory = mapToEntity(processingHistoryDto);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        processingHistory.setPost(post);
        processingHistory.setDate(LocalDateTime.now());
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
