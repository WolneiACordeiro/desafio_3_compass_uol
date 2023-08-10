package com.compassuol.desafio3.repository;

import com.compassuol.desafio3.entity.ProcessingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessingHistoryRepository extends JpaRepository<ProcessingHistory, Long> {
}
