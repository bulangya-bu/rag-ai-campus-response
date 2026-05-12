package com.example.webshiyan2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String citationIds;

    @Column(columnDefinition = "TEXT")
    private String citationTitles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatSession getSession() {
        return session;
    }

    public void setSession(ChatSession session) {
        this.session = session;
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

    public String getCitationIds() {
        return citationIds;
    }

    public void setCitationIds(String citationIds) {
        this.citationIds = citationIds;
    }

    public String getCitationTitles() {
        return citationTitles;
    }

    public void setCitationTitles(String citationTitles) {
        this.citationTitles = citationTitles;
    }
}
