package com.ssafy.sm.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sm.domain.board.dto.BoardRequest;
import com.ssafy.sm.domain.board.dto.BoardResponse;
import com.ssafy.sm.domain.board.service.BoardService;
import com.ssafy.sm.domain.user.dto.UserResponse;
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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    private MockHttpSession adminSession;
    private UserResponse adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new UserResponse();
        adminUser.setUserNo(1L);
        adminUser.setNickname("관리자");
        adminUser.setAdmin(true);

        adminSession = new MockHttpSession();
        adminSession.setAttribute("loginUser", adminUser);
    }

    @Test
    @DisplayName("관리자가 게시글을 정상적으로 생성해야 한다")
    void createBoardTest() throws Exception {
        BoardRequest.Create createDto = new BoardRequest.Create();
        createDto.setBoardTitle("테스트 제목");
        createDto.setBoardContent("테스트 내용");

        String boardJson = objectMapper.writeValueAsString(createDto);
        MockMultipartFile boardPart = new MockMultipartFile(
                "board", "", MediaType.APPLICATION_JSON_VALUE, boardJson.getBytes());
        MockMultipartFile filePart = new MockMultipartFile(
                "files", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());

        mockMvc.perform(multipart("/api/boards")
                        .file(boardPart)
                        .file(filePart)
                        .session(adminSession))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("게시글 작성 완료"))
                .andDo(print());

        verify(boardService).createBoard(any(BoardRequest.Create.class), any());
    }

    @Test
    @DisplayName("전체 게시글 목록 조회가 성공해야 한다")
    void getAllBoardsTest() throws Exception {
        BoardResponse.List listDto = new BoardResponse.List();
        listDto.setBoardNo(1L);
        listDto.setBoardTitle("제목");
        listDto.setCreatedAt(LocalDateTime.now());

        given(boardService.getAllBoards()).willReturn(Collections.singletonList(listDto));

        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].boardTitle").value("제목"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 상세 조회가 성공해야 한다")
    void getBoardDetailTest() throws Exception {
        BoardResponse.Detail detailDto = new BoardResponse.Detail();
        detailDto.setBoardNo(1L);
        detailDto.setBoardTitle("상세 제목");

        given(boardService.getBoardDetail(1L)).willReturn(detailDto);

        mockMvc.perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.boardTitle").value("상세 제목"))
                .andDo(print());
    }

    @Test
    @DisplayName("관리자가 게시글을 삭제할 수 있어야 한다")
    void deleteBoardTest() throws Exception {
        given(boardService.deleteBoard(1L)).willReturn(true);

        mockMvc.perform(delete("/api/boards/1")
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시물 삭제 성공"))
                .andDo(print());

        verify(boardService).deleteBoard(1L);
    }
}