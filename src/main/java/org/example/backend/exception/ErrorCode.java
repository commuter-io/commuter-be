package org.example.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U001", "이미 가입하신 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U002", "이미 존재하는 닉네임입니다."),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "U003", "사용자명은 필수입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U004", "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.NOT_FOUND, "U005", "비밀번호가 일치하지 않습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U006", "인증에 실패했습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "U007", "이메일 정보가 없습니다"),
    USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "U008", "비활성화 회원입니다."),
    SOCIAL_USER_EMAIL_OR_PASSWORD_CHANGE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "U009", "소셜 로그인 사용자는 비밀번호 변경이 불가능합니다."),
    PASSWORD_RESET_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "U009", "비밀번호 리셋 토큰이 올바르지 않습니다."),
    PASSWORD_RESET_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "U010", "비밀번호 리셋 토큰이 만료되었습니다."),

    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"A001", "유효하지 않은 액세스 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"A002", "만료된 엑세스 토큰입니다."),

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"R001", "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED,"R002", "Redis에 해당 리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED,"R003", "Redis에 토큰과 맞지 않는 토큰입니다."),

    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "이메일 발송에 실패했습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "E002", "인증 코드가 만료되었습니다."),
    EMAIL_VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "E003", "유효하지 않은 인증 코드입니다."),
    EMAIL_VERIFICATION_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "E004", "인증 코드를 입력해주세요."),
    EMAIL_SEND_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "E005", "이메일 발송 횟수를 초과했습니다. 5분 후 다시 시도해주세요."),

    EMPTY_PDF(HttpStatus.NOT_FOUND, "P001", "빈 PDF 입니다."),
    TOO_MANY_CHARACTER(HttpStatus.BAD_REQUEST, "P002", "PDF 글자수를 초과하셨습니다."),
    INVALID_PDF_FORMAT(HttpStatus.BAD_REQUEST, "P003", "PDF 형식이 아닙니다."),
    PDF_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "P004", "PDF 파일 크기가 100MB를 초과합니다."),
    NO_CHARACTER(HttpStatus.BAD_REQUEST, "P005", "이미지로만 구성된 PDF는 문제를 생성할 수 없습니다."),
    JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "J001", "JSON 직렬화에 실패했습니다."),
    JSON_DESERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "J002", "JSON 역직렬화에 실패했습니다."),

    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "폴더를 찾을 수 없습니다."),

    // Subway API related
    API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "외부 API 호출에 실패했습니다."),
    API_RESULT_ERROR(HttpStatus.BAD_REQUEST, "S002", "외부 API가 에러를 반환했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
