package com.example.demo.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime createAt;
    @Column(nullable = false)
    private LocalDate startdate;
    @Column(nullable = false)
    private LocalDate enddate;

    @Builder
    public Article(String title, String content, LocalDateTime createAt,
                   LocalDate startdate, LocalDate enddate){
        this.title = title;
        this.content = content;
        this.createAt = createAt;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public Article(String title, String content, LocalDate startdate, LocalDate enddate){
        this.title = title;
        this.content = content;
        createAt = LocalDateTime.now();
        this.startdate = startdate;
        this.enddate = enddate;
    }
}
