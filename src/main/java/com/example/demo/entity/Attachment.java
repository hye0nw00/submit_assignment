package com.example.demo.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String originName;
    @Column(nullable = false)
    private String fileExtension;
    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String studentId;
    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private LocalDateTime submitDate;
    @Column(nullable = false)
    private Long articleId;

    @Builder
    public Attachment(Long id, String fileName, String originName,
                      String fileExtension, Long fileSize, String studentId,
                      String studentName, LocalDateTime submitDate, Long articleId){
        this.id = id;
        this.fileName = fileName;
        this.originName = originName;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.studentId = studentId;
        this.studentName = studentName;
        this.submitDate = submitDate;
        this.articleId = articleId;
    }

    public Attachment(String fileName, String originName,
                      String fileExtension, Long fileSize,
                      String studentId, String studentName,
                      Long articleId){
        this.fileName = fileName;
        this.originName = originName;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.studentId = studentId;
        this.studentName = studentName;
        this.articleId = articleId;
        this.submitDate = LocalDateTime.now();
    }
}
