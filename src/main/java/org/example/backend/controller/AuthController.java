package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.backend.config.JwtTokenProvider;
import org.example.backend.domain.User;
import org.example.backend.domain.UserRepository;
import org.example.backend.dto.common.ApiResponse;
import org.example.backend.dto.request.LoginRequest;
import org.example.backend.dto.request.SignupRequest;
import org.example.backend.dto.response.LoginResponse;
import org.example.backend.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "새로운 사용자 등록")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> singup(@RequestBody SignupRequest request) {
        String email = request.getEmail();
        String password = passwordEncoder.encode(request.getPassword());
        String nickname = request.getNickname();

        // 이메일 중복 체크
        if(userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(ErrorCode.DUPLICATE_EMAIL));
        }

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isActive(true)
                .isSocial(false)
                .build();

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "회원가입이 완료되었습니다.", HttpStatus.CREATED));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력하여 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.INVALID_PASSWORD));
        }

        // Access Token + Refresh Token 생성
        String accessToken = jwtTokenProvider.createToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createToken(user.getEmail());

        LoginResponse response = new LoginResponse(accessToken, refreshToken, user.getEmail(), user.getNickname());

        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다", HttpStatus.OK));
    }
}
