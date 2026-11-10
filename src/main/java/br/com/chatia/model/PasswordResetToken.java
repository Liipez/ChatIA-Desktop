package br.com.chatia.model;

import java.time.LocalDateTime;

/**
 * Token temporário de recuperação de senha.
 * O código enviado ao usuário NUNCA é salvo; apenas o seu hash (SHA-256).
 */
public class PasswordResetToken {

    private String id;
    private String userId;
    private String tokenHash;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int attempts;
    private boolean used;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String userId, String tokenHash, LocalDateTime expiresAt) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.attempts = 0;
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
