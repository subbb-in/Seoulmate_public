package com.ssafy.sm.domain.user.service;

import com.ssafy.sm.domain.user.dto.UserRequest;
import com.ssafy.sm.domain.user.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
	
	// 1 - createUser
	void createUser(UserRequest.Join joinDto);

	// 1 - login
	UserResponse login(UserRequest.Login loginDto);

	// 1 - getUser
	UserResponse getUser(Long userNo);

	// 2 - updateUser
	void updateUser(Long userNo, UserRequest.Update updateDto, MultipartFile file) throws IOException;

	// 텍스트 수정
	void updateUserInfo(Long userNo, UserRequest.Update updateDto);

	// 프로필 사진 수정
	void updateUserImage(Long userNo, MultipartFile file);

    // 3- deleteUser
    void deleteUser(Long userNo, UserRequest.Delete deleteDto);
}