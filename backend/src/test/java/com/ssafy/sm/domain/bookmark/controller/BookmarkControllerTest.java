package com.ssafy.sm.domain.bookmark.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.domain.bookmark.dto.BookmarkResponse;
import com.ssafy.sm.domain.bookmark.service.BookmarkService;
import com.ssafy.sm.domain.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookmarkService bookmarkService;

    private MockHttpSession session;
    private UserResponse loginUser;

    @BeforeEach
    void setUp() {
        loginUser = new UserResponse();
        loginUser.setUserNo(1L);
        loginUser.setNickname("즐겨찾기왕");

        session = new MockHttpSession();
        session.setAttribute("loginUser", loginUser);
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 찜 토글을 시도하면 401 에러를 반환해야 한다")
    void toggleBookmarkUnauthorizedTest() throws Exception {
        mockMvc.perform(post("/api/bookmarks/100"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("찜 추가 시 성공 메시지와 true를 반환해야 한다")
    void toggleBookmarkAddTest() throws Exception {
        given(bookmarkService.toggleBookmark(anyLong(), anyLong())).willReturn(true);

        mockMvc.perform(post("/api/bookmarks/100")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("찜 목록에 추가되었습니다."))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("내 찜 목록 조회가 정상적으로 수행되어야 한다")
    void getMyBookmarksTest() throws Exception {
        BookmarkResponse response = new BookmarkResponse();
        response.setTitle("남산타워");

        given(bookmarkService.getMyBookmarks(anyLong())).willReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/bookmarks")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("찜 목록 조회 성공"))
                .andExpect(jsonPath("$.data[0].title").value("남산타워"))
                .andDo(print());
    }
}