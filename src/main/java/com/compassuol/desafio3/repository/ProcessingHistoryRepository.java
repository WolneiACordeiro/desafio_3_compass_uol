package com.compassuol.desafio3.repository;

import com.compassuol.desafio3.entity.ProcessingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingHistoryRepository extends JpaRepository<ProcessingHistory, Long> {
    List<ProcessingHistory> findByPostIdOrderByDateAsc(Long postId);
    ProcessingHistory findFirstByPostIdOrderByDateDesc(Long postId);
}
