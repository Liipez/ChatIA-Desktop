package br.com.chatia.model;

import java.time.LocalDateTime;
public class Message {
    private String id;
    private String conversationId;
    private String userId;
    private String role;
    private String content;
    private LocalDateTime createdAt;

    public Message() {
    }

    public Message(String conversationId, String userId, String role, String content) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}