package com.diary.backend.list;

import com.diary.backend.diary.DiaryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ListController {

    private final DiaryRepository diaryRepository;

    public ListController(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserMeResponse> me(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getAttribute("email");
        String name  = principal.getAttribute("name");
        return ResponseEntity.ok(new UserMeResponse(email, email, name));
    }

    @GetMapping("/diary/{userId}")
    public ResponseEntity<List<DiaryListItem>> list(
            @PathVariable String userId,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String me = principal.getAttribute("email");
        if (!userId.equals(me)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DiaryListItem> items = diaryRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(d -> new DiaryListItem(
                        d.getId(),
                        d.getTitle(),
                        d.getContent(),
                        d.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toString(),
                        d.getScore()
                ))
                .toList();

        return ResponseEntity.ok(items);
    }

    public record UserMeResponse(String userId, String email, String name) {}
    public record DiaryListItem(Long id, String title, String content, String date, Integer score) {}
}
