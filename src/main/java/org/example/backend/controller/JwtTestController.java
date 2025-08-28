package org.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtTestController {
    @GetMapping("/api/protected")
    public String protectedMethod() {
        return "JWT 인증 성공!";
    }
}
