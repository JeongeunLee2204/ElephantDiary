package com.diary.backend.diary;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDate date;
    private Integer score;

    private String userId;

    public Diary() {}

    public Diary(String title, String content, LocalDate date, String userId, Integer score) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.userId = userId;
        this.score=score;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getScore() { return score; }
    public void setScore(String userId) { this.score = score; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
