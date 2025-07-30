package com.diary.backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactForwardController {

    @GetMapping({"/", "/mypage", "/login", "/register", "/write", "/list", "/diary/**"})
    public String forwardToReact() {
        return "forward:/index.html";
    }

}
