package com.ssafy.sm.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sm.domain.user.dto.UserRequest;
import com.ssafy.sm.domain.user.dto.UserResponse;
import com.ssafy.sm.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponse testUser;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        testUser = UserResponse.builder()
                .userNo(1L)
                .email("test@ssafy.com")
                .nickname("테스터")
                .birth(LocalDate.of(1995, 5, 15))
                .languageCode("ko")
                .createdAt(LocalDateTime.now())
                .isAdmin(false)
                .build();

        session = new MockHttpSession();
        session.setAttribute("loginUser", testUser);
    }

    @Test
    @DisplayName("회원가입(signup) API가 정상적으로 호출되어야 한다")
    void signupTest() throws Exception {
        UserRequest.Join joinDto = new UserRequest.Join();
        joinDto.setEmail("new@ssafy.com");
        joinDto.setPw("password");
        joinDto.setNickname("새유저");

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입 성공"))
                .andDo(print());

        verify(userService).createUser(any(UserRequest.Join.class));
    }

    @Test
    @DisplayName("로그인 성공 시 세션에 유저 정보가 저장되는지 확인한다")
    void loginTest() throws Exception {
        UserRequest.Login loginDto = new UserRequest.Login();
        loginDto.setEmail("test@ssafy.com");
        loginDto.setPw("password123");

        given(userService.login(any(UserRequest.Login.class))).willReturn(testUser);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("테스터"))
                .andExpect(request().sessionAttribute("loginUser", testUser))
                .andDo(print());
    }

    @Test
    @DisplayName("PATCH 멀티파트 요청을 통한 회원 수정 로직을 검증한다")
    void updateUserMultipartTest() throws Exception {
        // UserController에서 @RequestPart("user") String으로 받으므로 JSON 문자열 준비
        String userJson = "{\"nickname\":\"수정닉네임\", \"languageCode\":\"en\"}";

        MockMultipartFile filePart = new MockMultipartFile(
                "file", "image.png", MediaType.IMAGE_PNG_VALUE, "image_data".getBytes());
        MockMultipartFile userPart = new MockMultipartFile(
                "user", "", MediaType.APPLICATION_JSON_VALUE, userJson.getBytes());

        // MockMvc에서 PATCH 멀티파트를 위해 multipart 요청 후 method를 PATCH로 명시
        mockMvc.perform(multipart("/api/users/1")
                        .file(filePart)
                        .file(userPart)
                        .session(session)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보 수정 성공"))
                .andDo(print());

        verify(userService).updateUser(eq(1L), any(UserRequest.Update.class), any());
    }

    @Test
    @DisplayName("로그인 상태 확인(me) API가 세션 정보를 정상 반환해야 한다")
    void meApiTest() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@ssafy.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 요청 시 서비스가 호출되고 성공 응답을 주어야 한다")
    void deleteUserTest() throws Exception {
        UserRequest.Delete deleteDto = new UserRequest.Delete();
        deleteDto.setPw("password123");

        mockMvc.perform(post("/api/users/1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 탈퇴 성공"))
                .andDo(print());

        verify(userService).deleteUser(eq(1L), any(UserRequest.Delete.class));
    }
}