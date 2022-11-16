package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.Attachment;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    private ArticleService articleService;

    @Autowired
    public MainController(ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String mainpage(Model model){
        model.addAttribute("articles", articleService.getArticles());
        return "index";
    }

    @GetMapping("/check")
    public String checkpage(@RequestParam Long id, Model model){
        if(articleService.getArticle(id) == null){
            return "redirect:/";
        }
        String title = articleService.getArticle(id).getTitle();
        List<Attachment> attachmentList = articleService.showSubmitList(id);
        model.addAttribute("title", title);
        model.addAttribute("attaches", attachmentList);
        return "checksubmit";
    }

    @GetMapping("/submit")
    public String submitpage(@RequestParam Long id, Model model){
        if(articleService.getArticle(id) == null){
            return "redirect:/";
        }
        Article article = articleService.getArticle(id);
        if(java.sql.Date.valueOf(article.getStartdate()).getTime() > new Date().getTime()
                || java.sql.Date.valueOf(article.getEnddate()).getTime() < new Date().getTime()){
            model.addAttribute("msg", "과제 제출 기간이 아닙니다.");
            model.addAttribute("url", "/");
            return "alert";
        }
        model.addAttribute("title", article.getTitle());
        model.addAttribute("id", article.getId());
        return "submit";
    }

    @GetMapping("/show")
    public String showArticle(@RequestParam Long id, Model model){
        if(articleService.getArticle(id) == null){
            return "redirect:/";
        }
        Article article = articleService.getArticle(id);
        model.addAttribute("title", article.getTitle());
        model.addAttribute("content", article.getContent());
        model.addAttribute("id", article.getId());
        model.addAttribute("start", article.getStartdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("end", article.getEnddate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "showarticle";
    }

    @GetMapping("/write")
    public String writepage(){
        return "writearticle";
    }

    @PostMapping("/write")
    public String writeprocess(String title, String content, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startdate, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate enddate){
        articleService.writeArticle(title, content, startdate, enddate);
        return "redirect:/";
    }

    @PostMapping("/submit")
    public String send(Long articleId, String studentId,
                     String studentName, MultipartFile file, Model model){
        if(articleService.getSubmit(articleId, studentId, studentName).isPresent()){
            model.addAttribute("msg", "이미 제출한 인원입니다.");
            model.addAttribute("url", "/show?id="+articleId);
            return "alert";
        }
        Article article = articleService.getArticle(articleId);
        if(java.sql.Date.valueOf(article.getStartdate()).getTime() > new Date().getTime()
                || java.sql.Date.valueOf(article.getEnddate()).getTime() < new Date().getTime()){
            model.addAttribute("msg", "과제 제출 기간이 아닙니다.");
            model.addAttribute("url", "/");
            return "alert";
        }
        articleService.submit(articleId, studentId, studentName, file);
        return "redirect:/show?id="+articleId;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(HttpServletRequest request, @PathVariable String filename) {
        Attachment attachments = articleService.findByOriginPath(filename);
        Resource file = articleService.loadAsResource(filename);
        String userAgent = request.getHeader("User-Agent");
        if(userAgent.indexOf("Trident") > -1)
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + URLEncoder.encode(attachments.getOriginName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20") + "\"").body(file);
        else
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + new String(attachments.getOriginName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"").body(file);
    }
}
