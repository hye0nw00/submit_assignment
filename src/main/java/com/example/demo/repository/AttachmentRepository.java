package com.example.demo.repository;

import com.example.demo.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository
        extends JpaRepository<Attachment, Long> {
    @Query(value = "select * from attachment where file_name = :path", nativeQuery = true)
    public List<Attachment> findByOriginPath(@Param(value = "path") String path);
}
