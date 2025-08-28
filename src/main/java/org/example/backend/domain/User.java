package org.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class User extends BaseEntity{
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false, length = 50, unique = true)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // google 로그인 여부
    @ColumnDefault("false")
    @Column(name = "is_social")
    private Boolean isSocial;

    // 계정 비활성화
    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private List<Attendance> attendances = new ArrayList<>();

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void changeEmail(String email) {
        this.email = email;
    }
}
