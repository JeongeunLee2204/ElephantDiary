package com.diary.backend.Login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    /*@GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }*/


    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String name = principal.getAttribute("name");
            String email = principal.getAttribute("email");
            String picture=principal.getAttribute("picture");

            System.out.println("로그인 성공!");
            System.out.println("사용자 이름: " + name);
            System.out.println("이메일: " + email);
            System.out.println("권한있음?"+principal.getAuthorities());
            model.addAttribute("userName", name);
            model.addAttribute("email",email);
            model.addAttribute("picture", picture);
        } else {
            System.out.println("로그인되지 않은 사용자입니다.");
        }

        return "redirect:/mypage";
    }

    @GetMapping("/api/user/me")
    @ResponseBody
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인되지 않음");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", principal.getAttribute("name"));
        userInfo.put("email", principal.getAttribute("email"));
        userInfo.put("picture", principal.getAttribute("picture"));

        return ResponseEntity.ok(userInfo);
    }


}
