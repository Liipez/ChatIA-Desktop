package br.com.chatia.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeDocument {

    private String id;
    private String title;
    private String content;
    private List<String> keywords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KnowledgeDocument() {
    }

    public KnowledgeDocument(String title, String content) {
        this.title = title;
        this.content = content;
        this.keywords = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}