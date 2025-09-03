package org.example.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Schema(example = "test@example.com", description = "사용자 이메일")
    private String email;

    @Schema(example = "1234", description = "비밀번호")
    private String password;
}
