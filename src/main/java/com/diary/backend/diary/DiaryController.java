package com.diary.backend.diary;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
        System.out.println("📌 POST /api/diary 들어옴");
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        // 로그인 사용자
        String userId = principal.getAttribute("email");
        diary.setUserId(userId);

        // ✅ content가 null이어도 summary는 항상 세팅되도록 보장
        String content = diary.getContent();
        String trimmed = (content == null) ? "" : content.strip();
        String summary = trimmed.isEmpty()
                ? "(빈 일기)"
                : trimmed.substring(0, Math.min(20, trimmed.length()));
        diary.setSummary(summary);
        System.out.println("SUMMARY 저장됨: " + diary.getSummary());

        return ResponseEntity.ok(diaryRepository.save(diary));
    }

    @GetMapping
    public ResponseEntity<List<Diary>> getMyDiaries(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String userId = principal.getAttribute("email");
        return ResponseEntity.ok(diaryRepository.findByUserIdOrderByDateDesc(userId));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Diary> getOne(@PathVariable Long id,
                                        @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String userId = principal.getAttribute("email");
        return diaryRepository.findById(id)
                .filter(d -> Objects.equals(d.getUserId(), userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Diary> update(@PathVariable Long id,
                                        @RequestBody Diary req,
                                        @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String userId = principal.getAttribute("email");

        return diaryRepository.findById(id)
                .filter(d -> Objects.equals(d.getUserId(), userId))
                .map(d -> {
                    if (req.getTitle() != null) d.setTitle(req.getTitle());

                    if (req.getContent() != null) {
                        d.setContent(req.getContent());
                        String trimmed = req.getContent().strip();
                        String summary = trimmed.isEmpty()
                                ? "(빈 일기)"
                                : trimmed.substring(0, Math.min(20, trimmed.length()));
                        d.setSummary(summary);
                    }

                    if (req.getDate() != null)  d.setDate(req.getDate());
                    if (req.getScore() != null) d.setScore(req.getScore());

                    return ResponseEntity.ok(diaryRepository.save(d));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String userId = principal.getAttribute("email");
        var opt = diaryRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var diary = opt.get();
        if (!Objects.equals(userId, diary.getUserId())) return ResponseEntity.notFound().build();

        diaryRepository.delete(diary);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId:.+}")
    public ResponseEntity<List<Diary>> listByUser(@PathVariable String userId,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String me = principal.getAttribute("email");
        if (!Objects.equals(me, userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(diaryRepository.findByUserIdOrderByDateDesc(userId));
    }
}
