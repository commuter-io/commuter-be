package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.config.JwtTokenProvider;
import org.example.backend.domain.User;
import org.example.backend.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public String signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = passwordEncoder.encode(body.get("password"));
        String nickname = body.get("nickname");

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isActive(true)
                .isSocial(false)
                .build();

        userRepository.save(user);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body) {
        User user = userRepository.findByEmail(body.get("email"))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일"));

        if (!passwordEncoder.matches(body.get("password"), user.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        return jwtTokenProvider.createToken(user.getEmail());
    }
}
