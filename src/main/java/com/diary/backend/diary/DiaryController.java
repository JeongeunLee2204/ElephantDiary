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

    /** 생성 */
    @PostMapping
    public ResponseEntity<Diary> saveDiary(@RequestBody Diary diary,
                                           @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String userId = principal.getAttribute("email");
        diary.setUserId(userId);

        // content가 null이어도 summary 항상 세팅
        diary.setSummary(buildSummary(diary.getContent()));

        return ResponseEntity.ok(diaryRepository.save(diary));
    }

    /** 내 목록 */
    @GetMapping
    public ResponseEntity<List<Diary>> getMyDiaries(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String userId = principal.getAttribute("email");
        return ResponseEntity.ok(diaryRepository.findByUserIdOrderByDateDesc(userId));
    }

    /** 단건 조회: 숫자 ID만 */
    @GetMapping("/id/{id:\\d+}")
    public ResponseEntity<Diary> getOne(@PathVariable Long id,
                                        @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String userId = principal.getAttribute("email");

        var opt = diaryRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var d = opt.get();
        if (!Objects.equals(d.getUserId(), userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(d);
    }

    /** 수정(PUT이지만 부분 수정) */
    @PutMapping("/id/{id:\\d+}")
    public ResponseEntity<Diary> update(@PathVariable Long id,
                                        @RequestBody Diary req,
                                        @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String userId = principal.getAttribute("email");

        var opt = diaryRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var d = opt.get();
        if (!Objects.equals(d.getUserId(), userId)) {
            return ResponseEntity.status(403).build();
        }

        // 부분 수정
        if (req.getTitle() != null) d.setTitle(req.getTitle());

        if (req.getContent() != null) {
            d.setContent(req.getContent());
            d.setSummary(buildSummary(req.getContent())); // content 변경 시 summary 재계산
        } else {
            // 과거 데이터 등으로 summary가 비어있을 수 있으니 보정
            if (d.getSummary() == null) {
                d.setSummary(buildSummary(d.getContent()));
            }
        }

        if (req.getDate() != null)  d.setDate(req.getDate());
        if (req.getScore() != null) d.setScore(req.getScore());

        return ResponseEntity.ok(diaryRepository.save(d));
    }

    /** 삭제 */
    @DeleteMapping("/id/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String userId = principal.getAttribute("email");

        var opt = diaryRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var d = opt.get();
        if (!Objects.equals(d.getUserId(), userId)) {
            return ResponseEntity.status(403).build();
        }

        diaryRepository.delete(d);
        return ResponseEntity.noContent().build();
    }

    /** 특정 사용자(이메일) 목록: '.' 포함 허용 */
    @GetMapping("/by-user/{userId:.+}")
    public ResponseEntity<List<Diary>> listByUser(@PathVariable String userId,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String me = principal.getAttribute("email");
        if (!Objects.equals(me, userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(diaryRepository.findByUserIdOrderByDateDesc(userId));
    }

    /** 요약 생성 유틸 */
    private static String buildSummary(String content) {
        String trimmed = (content == null) ? "" : content.strip();
        return trimmed.isEmpty()
                ? "(빈 일기)"
                : trimmed.substring(0, Math.min(20, trimmed.length()));
    }
}
