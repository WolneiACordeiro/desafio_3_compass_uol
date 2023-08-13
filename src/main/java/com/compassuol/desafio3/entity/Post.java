package com.compassuol.desafio3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_posts")
public class Post {
    @Id
    @NotNull
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "title", nullable = true)
    private String title;
    @Column(name = "body", nullable = true, columnDefinition = "TEXT")
    private String body;
}
