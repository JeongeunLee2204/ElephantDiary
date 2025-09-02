package com.diary.backend.diary;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.ALWAYS)  // ✅ null 필드도 항상 응답에 포함
@Entity
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 10000)
    private String content;

    private String summary;

    private LocalDate date;

    private Integer score;

    private String userId;

    public Diary() {}

    public Diary(String title, String content, String summary, LocalDate date, String userId, Integer score) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.date = date;
        this.userId = userId;
        this.score = score;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
