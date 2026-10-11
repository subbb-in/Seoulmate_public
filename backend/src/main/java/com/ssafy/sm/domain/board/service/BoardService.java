package com.ssafy.sm.domain.board.service;

import com.ssafy.sm.domain.board.dto.BoardRequest;
import com.ssafy.sm.domain.board.dto.BoardResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BoardService {
	// 게시글 생성
    void createBoard(BoardRequest.Create createDto, List<MultipartFile> files) throws IOException;
    // 게시글 리스트
    List<BoardResponse.List> getAllBoards();
    // 게시글 상세 조회
    BoardResponse.Detail getBoardDetail(Long boardNo);
    // 게시글 수정
    void updateBoard(BoardRequest.Update updateDto);
    // 게시글 삭제
    boolean deleteBoard(Long boardNo);
}
