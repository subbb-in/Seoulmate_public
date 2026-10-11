package com.ssafy.sm.domain.board.controller;

import java.io.IOException;
import java.util.List;

import com.ssafy.sm.common.util.SecurityUtil;
import com.ssafy.sm.domain.user.dto.UserResponse;
import com.ssafy.sm.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.domain.board.dto.BoardRequest;
import com.ssafy.sm.domain.board.dto.BoardResponse;
import com.ssafy.sm.domain.board.service.BoardService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
	private final BoardService boardService;
	private final UserService userService;

	// 게시글 작성 (POST /api/boards) - admin only
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Void>> createBoard(
            @RequestPart(value = "board") BoardRequest.Create createDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Long userNo = SecurityUtil.getCurrentUserNo();
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("관리자만 작성할 수 있습니다"));
        }

        UserResponse user = userService.getUser(userNo);
        createDto.setUserNo(userNo);
        createDto.setBoardWriter(user.getNickname());

        try {
            boardService.createBoard(createDto, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("게시글 작성 완료", null));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("파일 업로드 중 오류가 발생했습니다."));
        }
    }

	// 게시글 전체 조회 (GET /api/boards)
	@GetMapping
	public ResponseEntity<ApiResponse<List<BoardResponse.List>>> getAllBoards() {
		List<BoardResponse.List> list = boardService.getAllBoards();
		return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", list));
	}

	// 게시글 상세 조회 (GET /api/boards/{boardNo})
	@GetMapping("/{boardNo}")
	public ResponseEntity<ApiResponse<BoardResponse.Detail>> getBoardDetail(@PathVariable Long boardNo) {
		try {
			BoardResponse.Detail detail = boardService.getBoardDetail(boardNo);
			return ResponseEntity.ok(ApiResponse.success("게시글 상세 조회 성공", detail));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
		}
	}

	// 게시글 수정 (PATCH /api/boards/{boardNo})
	@PatchMapping("/{boardNo}")
	public ResponseEntity<ApiResponse<Void>> updateBoard(@PathVariable Long boardNo,
			@RequestBody BoardRequest.Update updateDto) {

		if (!isAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("관리자만 게시글을 수정할 수 있습니다."));
		}

		try {
			updateDto.setBoardNo(boardNo);
			boardService.updateBoard(updateDto);
			return ResponseEntity.ok(ApiResponse.success("게시글 수정 성공", null));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
		}
	}

	// 게시글 삭제 (DELETE /api/boards/{boardNo})
	@DeleteMapping("/{boardNo}")
	public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable("boardNo") Long boardNo) {

		if (!isAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("관리자만 삭제할 수 있습니다"));
		}

		boolean deleted = boardService.deleteBoard(boardNo);
		if (deleted) {
			return ResponseEntity.ok(ApiResponse.success("게시물 삭제 성공", null));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("해당 게시글 없음"));
		}
	}

	private boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}
}
