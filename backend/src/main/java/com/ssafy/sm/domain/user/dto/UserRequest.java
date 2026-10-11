package com.ssafy.sm.domain.user.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserRequest {

	// 회원가입용 내부 클래스
	@Getter
	@Setter
	@ToString
	public static class Join {
		private Long userNo;
		private String email;
		private String pw;
		private String nickname;
		private Date birth;
		private String languageCode;
	}

	// 로그인용 내부 클래스
	@Getter
	@Setter
	@ToString
	public static class Login {
		private String email;
		private String pw;
		private boolean isAdmin;
	}

	// 회원 수정 내부 클래스
	@Getter
	@Setter
	@ToString
	public static class Update {
		private Long userNo;
		private String pw;
		private String nickname;
		private Date birth;
		private String languageCode;
        private String profileImg;
	}

	// 회원 삭제 내부 클래스
	@Getter
	@Setter
	@ToString
	public static class Delete {
		private String pw;
	}
}