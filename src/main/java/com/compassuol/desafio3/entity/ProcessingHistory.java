package com.compassuol.desafio3.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "tb_processingHistory")
public class ProcessingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "status_date", nullable = false)
    private Date date;
    private PostState status;
}
