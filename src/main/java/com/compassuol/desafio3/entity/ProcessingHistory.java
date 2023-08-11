package com.compassuol.desafio3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_processing")
public class ProcessingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "post_id", nullable = false)
    //private Post post;
    private Long postId;
    @JoinColumn(name = "date_process", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;
    private String status;
}
