package com.ssafy.sm.domain.user.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String pw;

    @Column(nullable = false)
    private String nickname;

    private String birth;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    // 비즈니스 로직: 회원 정보 수정
    public void updateUserInfo(String pw, String nickname, String birth, String languageCode, String profileImg) {
        if (pw != null) this.pw = pw;
        if (nickname != null) this.nickname = nickname;
        if (birth != null) this.birth = birth;
        if (languageCode != null) this.languageCode = languageCode;
        if (profileImg != null) this.profileImg = profileImg;
    }

    // 비즈니스 로직: 회원 삭제(Soft Delete)
    public void deleteUser() {
        this.isDeleted = true;
    }
}
