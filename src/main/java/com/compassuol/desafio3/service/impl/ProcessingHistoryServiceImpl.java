package com.compassuol.desafio3.service.impl;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.entity.ProcessingHistory;
import com.compassuol.desafio3.payload.ProcessingHistoryDisplayDto;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.repository.PostRepository;
import com.compassuol.desafio3.repository.ProcessingHistoryRepository;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessingHistoryServiceImpl implements ProcessingHistoryService {
    private final ProcessingHistoryRepository processingHistoryRepository;
    private final ModelMapper mapper;
    public  ProcessingHistoryServiceImpl(ProcessingHistoryRepository  processingHistoryRepository, PostRepository postRepository, ModelMapper mapper){
        this.processingHistoryRepository = processingHistoryRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProcessingHistoryDto> getAllProcess() {
        List<ProcessingHistory> processingHistories = processingHistoryRepository.findAll();
        return processingHistories.stream().map(processingHistory -> mapToDTO(processingHistory)).collect(Collectors.toList());
    }

    @Override
    public ProcessingHistory findFirstByPostIdOrderByDateDesc(Long postId) {
        return processingHistoryRepository.findFirstByPostIdOrderByDateDesc(postId);
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

    @Override
    public ResponseEntity<String> getFirstStatus(@PathVariable Long postId) {
        ProcessingHistory firstHistory = findFirstByPostIdOrderByDateDesc(postId);

        if (firstHistory != null) {
            String firstStatus = firstHistory.getStatus();
            return ResponseEntity.ok(firstStatus);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ProcessingHistoryDto mapToDTO(ProcessingHistory processingHistory){
        ProcessingHistoryDto processingHistoryDto = mapper.map(processingHistory, ProcessingHistoryDto.class);
        return processingHistoryDto;
    }

    private ProcessingHistory mapToEntity(ProcessingHistoryDto processingHistoryDto){
        ProcessingHistory processingHistory = mapper.map(processingHistoryDto, ProcessingHistory.class);
        return processingHistory;
    }

    public ProcessingHistoryDisplayDto mapProcessHistoryToDisplayDTO(ProcessingHistory processingHistory) {
        ProcessingHistoryDisplayDto processingHistoryDisplayDto = mapper.map(processingHistory, ProcessingHistoryDisplayDto.class);
        return processingHistoryDisplayDto;
    }
}
