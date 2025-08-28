package com.diary.backend.diary;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public ResponseEntity<Diary> saveDiary(@RequestBody Diary diary,
                                           @AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttribute("email");
        diary.setUserId(userId);
        return ResponseEntity.ok(diaryRepository.save(diary));
    }

    @GetMapping
    public ResponseEntity<List<Diary>> getMyDiaries(@AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttribute("email");
        return ResponseEntity.ok(diaryRepository.findByUserIdOrderByDateDesc(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diary> getOne(@PathVariable Long id,
                                        @AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttribute("email");
        return diaryRepository.findById(id)
                .filter(d -> d.getUserId().equals(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diary> update(@PathVariable Long id,
                                        @RequestBody Diary req,
                                        @AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttribute("email");
        return diaryRepository.findById(id)
                .filter(d -> d.getUserId().equals(userId))
                .map(d -> {
                    d.setTitle(req.getTitle());
                    d.setContent(req.getContent());
                    d.setSummary(req.getSummary());
                    d.setDate(req.getDate());
                    d.setScore(req.getScore());
                    return ResponseEntity.ok(diaryRepository.save(d));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttribute("email");

        var opt = diaryRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var diary = opt.get();
        if (!userId.equals(diary.getUserId())) return ResponseEntity.notFound().build();

        diaryRepository.delete(diary);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diary>> listByUser(@PathVariable String userId,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        String me = principal.getAttribute("email");
        if (!me.equals(userId)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(diaryRepository.findByUserIdOrderByDateDesc(userId));
    }
}
