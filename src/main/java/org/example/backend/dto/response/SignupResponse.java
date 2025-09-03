package org.example.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponse {
    @Schema(example = "test@example.com")
    private String email;

    @Schema(example = "testuser")
    private String nickname;

    @Schema(example = "회원가입 성공")
    private String message;
}
