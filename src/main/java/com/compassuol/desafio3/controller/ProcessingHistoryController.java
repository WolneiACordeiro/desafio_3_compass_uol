package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.payload.CommentDto;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/processing")
public class ProcessingHistoryController {
    private final ProcessingHistoryService processingHistoryService;
    public ProcessingHistoryController(ProcessingHistoryService processingHistoryService) {
        this.processingHistoryService = processingHistoryService;
    }

    @GetMapping
    public List<ProcessingHistoryDto> getAllProcess(){
        return processingHistoryService.getAllProcess();
    }

}
