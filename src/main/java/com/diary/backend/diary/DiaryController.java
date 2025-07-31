package com.diary.backend.diary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryRepository diaryRepository;

    public DiaryController(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @PostMapping
    public ResponseEntity<Diary> saveDiary(@RequestBody Diary diary) {
        return ResponseEntity.ok(diaryRepository.save(diary));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Diary>> getDiaries(@PathVariable String userId) {
        return ResponseEntity.ok(diaryRepository.findByUserId(userId));
    }
}
