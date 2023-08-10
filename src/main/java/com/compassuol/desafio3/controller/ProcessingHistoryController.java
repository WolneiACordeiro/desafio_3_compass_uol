package com.compassuol.desafio3.controller;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/processing")
public class ProcessingHistoryController {
    private final ProcessingHistoryService processingHistoryService;
    public ProcessingHistoryController(ProcessingHistoryService processingHistoryService) {
        this.processingHistoryService = processingHistoryService;
    }

    /*@PostMapping("/{postId}/{status}")
    public ResponseEntity<String> createProcessingHistory(
            @PathVariable Long postId,
            @PathVariable String status) {
        try {
            PostState postState = PostState.valueOf(status.toUpperCase());
            ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
            processingHistoryDto.setStatus(String.valueOf(postState));

            ProcessingHistoryDto savedProcessingHistory = processingHistoryService.createProcess(postId, processingHistoryDto);

            if (savedProcessingHistory != null) {
                return ResponseEntity.ok("Processing history created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create processing history");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }
    }*/

}
