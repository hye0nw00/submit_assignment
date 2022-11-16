package com.example.demo.service;

import com.example.demo.StorageProperties;
import com.example.demo.entity.Article;
import com.example.demo.entity.Attachment;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private ArticleRepository articleRepository;
    private AttachmentRepository attachmentRepository;
    private StorageProperties properties;

    @Autowired
    public ArticleService(ArticleRepository articleRepository,
                          AttachmentRepository attachmentRepository,
                          StorageProperties properties){
        this.articleRepository = articleRepository;
        this.attachmentRepository = attachmentRepository;
        this.properties = properties;
    }

    public void submit(Long articleId, String studentId,
                       String studentName, MultipartFile file){
        if(!file.isEmpty()){
            //파일 복사
            String fileName = file.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String originPath = UUID.randomUUID().toString().replaceAll("-","") + fileExtension;
            Long fileSize = file.getSize();

            Path destinationFile = Paths.get(properties.getLocation())
                    .resolve(Paths.get(originPath))
                    .normalize().toAbsolutePath();
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //첨부파일 데이터베이스 명세
            Attachment attachment =
                    new Attachment(originPath, fileName,
                            fileExtension, fileSize,
                            studentId, studentName,
                            articleId);
            attachmentRepository.save(attachment);
        }
    }

    public List<Attachment> showSubmitList(Long articleId){
        if(!articleRepository.findById(articleId).isPresent())
            return null;
        return attachmentRepository.findAll().stream()
                .filter(attachment -> attachment.getArticleId()==articleId)
                .collect(Collectors.toList());
    }

    public Optional<Attachment> getSubmit(Long articleId, String studentId, String studentName){
        return showSubmitList(articleId).stream()
                .filter(attachment -> attachment.getStudentId().equals(studentId))
                .filter(attachment -> attachment.getStudentName().equals(studentName))
                .findAny();
    }

    public List<Article> getArticles(){
        return articleRepository.findAll();
    }

    public Article writeArticle(String title, String content, LocalDate startdate, LocalDate enddate){
        Article article = new Article(title, content, startdate, enddate);
        articleRepository.save(article);
        return article;
    }

    public Article getArticle(Long id){
        Optional<Article> article = articleRepository.findById(id);
        if(article.isPresent()) return article.get();
        return null;
    }

    public Attachment findByOriginPath(String path){
        List<Attachment> attachments = attachmentRepository.findByOriginPath(path);
        if(attachments.size() == 0) return null;
        return attachments.get(0);
    }

    public Resource loadAsResource(String filename){
        try {
            Path file = Paths.get(properties.getLocation()).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
