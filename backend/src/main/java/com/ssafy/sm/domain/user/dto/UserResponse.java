package com.ssafy.sm.domain.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private Long userNo;
	private String email;
	private String nickname;
	private LocalDate birth;
	private String languageCode;
	private LocalDateTime createdAt;
    private boolean isAdmin;
    private String profileImg;
}