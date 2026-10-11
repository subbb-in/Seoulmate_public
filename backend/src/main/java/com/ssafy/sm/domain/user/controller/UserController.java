package com.ssafy.sm.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sm.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.domain.user.dto.UserRequest;
import com.ssafy.sm.domain.user.dto.UserResponse;
import com.ssafy.sm.domain.user.exception.DuplicateIdException;
import com.ssafy.sm.domain.user.service.UserService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    // 회원가입
    // POST /api/users/signup
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody UserRequest.Join joinDto) {
        try {
            userService.createUser(joinDto);
            return ResponseEntity.ok(ApiResponse.success("회원가입 성공", null));
        } catch (DuplicateIdException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 회원 정보 조회
    // GET /api/users/{userNo}
    @GetMapping("/{userNo}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(@PathVariable Long userNo) {
        UserResponse user = userService.getUser(userNo);
        if (user != null) {
            return ResponseEntity.ok(ApiResponse.success("회원 정보 조회 성공", user));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("회원을 찾을 수 없습니다."));
    }

    // 회원 정보 수정
    // PATCH /api/users/{userNo}
    @PatchMapping(value = "/{userNo}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestPart(value = "user") String userString,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @PathVariable("userNo") Long userNo) {

        Long currentUserNo = SecurityUtil.getCurrentUserNo();
        if (!currentUserNo.equals(userNo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("권한이 없습니다."));
        }

        try {
            UserRequest.Update updateDto = objectMapper.readValue(userString, UserRequest.Update.class);
            userService.updateUser(userNo, updateDto, file);
            return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 성공", null));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("파일 업로드 중 오류가 발생했습니다."));
        } catch (Exception ex) {
            ex.getStackTrace(); // 에러 확인용 로그(260108 - printStackTrace() -> getStackTrace로 변경)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버에서 오류가 발생했습니다."));
        }
    }

//    // 텍스트 정보 수정(닉네임)
//    // PATCH /api/users/{userNo}/info
//    @PatchMapping("/{userNo}/info")
//    public ResponseEntity<ApiResponse<UserResponse>> updateUserInfo(
//            @PathVariable("userNo") Long userNo,
//            @RequestBody UserRequest.Update updateDto, HttpSession session
//    ) {
//        // 세션 권한 확인
//        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
//        if (loginUser == null || !loginUser.getUserNo().equals(userNo)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("권한이 없습니다."));
//        }
//
//        userService.updateUserInfo(userNo, updateDto); // 서비스에 텍스트 수정 메서드 필요
//        return ResponseEntity.ok(ApiResponse.success("정보 수정 성공", null));
//    }
//
//    // 프로필 사진 수정
//    // PATCH /api/users/{userNo}/image
//    @PatchMapping(value = "/{userNo}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse<UserResponse>> updateUserImage(@PathVariable("userNo") Long userNo,
//            @RequestPart("file") MultipartFile file, HttpSession session
//    ) {
//        // 세션 권한 확인
//        UserResponse loginUser = (UserResponse) session.getAttribute("loginUser");
//        if (loginUser == null || !loginUser.getUserNo().equals(userNo)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("권한이 없습니다."));
//        }
//
//        // 사진 수정 메서드
//        userService.updateUserImage(userNo, file);
//
//        UserResponse updatedUser = userService.getUser(userNo);
//        session.setAttribute("loginUser", updatedUser);
//        return ResponseEntity.ok(ApiResponse.success("사진 수정 성공", updatedUser));
//    }


    // 회원 삭제
    // POST /api/users/{userNo}
    @PostMapping("/{userNo}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable Long userNo, @RequestBody UserRequest.Delete deleteDto) {
        Long currentUserNo = SecurityUtil.getCurrentUserNo();
        if (!currentUserNo.equals(userNo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("권한이 없습니다."));
        }

        try {
            userService.deleteUser(userNo, deleteDto);
            return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 성공", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    // 현재 로그인한 사용자 정보 조회
    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        Long currentUserNo = SecurityUtil.getCurrentUserNo();
        UserResponse user = userService.getUser(currentUserNo);
        return ResponseEntity.ok(ApiResponse.success("로그인 확인", user));
    }
}
