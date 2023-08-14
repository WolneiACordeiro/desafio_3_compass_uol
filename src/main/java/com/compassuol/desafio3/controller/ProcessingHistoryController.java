package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
