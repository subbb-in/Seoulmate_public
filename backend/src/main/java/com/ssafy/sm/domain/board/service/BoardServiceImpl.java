package com.ssafy.sm.domain.board.service;

import com.ssafy.sm.common.util.FileStore;
import com.ssafy.sm.domain.board.dto.BoardRequest;
import com.ssafy.sm.domain.board.dto.BoardResponse;
import com.ssafy.sm.domain.board.entity.Board;
import com.ssafy.sm.domain.board.entity.BoardImage;
import com.ssafy.sm.domain.board.repository.BoardRepository;
import com.ssafy.sm.domain.user.entity.User;
import com.ssafy.sm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final FileStore fileStore;

	@Override
	@Transactional
	public void createBoard(BoardRequest.Create createDto, List<MultipartFile> files) throws IOException {
		User user = userRepository.findById(createDto.getUserNo())
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Board board = Board.builder()
				.boardTitle(createDto.getBoardTitle())
				.boardContent(createDto.getBoardContent())
				.boardWriter(createDto.getBoardWriter())
				.viewCnt(0)
				.createdAt(LocalDateTime.now())
				.isDeleted(false)
				.user(user)
				.build();

		if (files != null && !files.isEmpty()) {
			List<FileStore.UploadFile> uploadFiles = fileStore.storeFiles(files);
			int sortOrder = 1;
			for (FileStore.UploadFile uploadFile : uploadFiles) {
				BoardImage image = BoardImage.builder()
						.originName(uploadFile.getOriginName())
						.boardImgName(uploadFile.getStoredName())
						.imgPath("/uploads/" + uploadFile.getStoredName())
						.sortOrder(sortOrder++)
						.isThumbnail(sortOrder == 2) // 첫 번째 이미지를 썸네일로
						.build();
				board.addImage(image);
			}
		}

		boardRepository.save(board);
	}

	@Override
	public List<BoardResponse.List> getAllBoards() {
		return boardRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc().stream()
				.map(this::convertToListResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public BoardResponse.Detail getBoardDetail(Long boardNo) {
		Board board = boardRepository.findByBoardNoAndIsDeletedFalse(boardNo)
				.orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

		board.increaseViewCnt(); // Dirty Checking으로 조회수 증가 반영

		return convertToDetailResponse(board);
	}

	@Override
	@Transactional
	public void updateBoard(BoardRequest.Update updateDto) {
		Board board = boardRepository.findByBoardNoAndIsDeletedFalse(updateDto.getBoardNo())
				.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

		board.update(updateDto.getBoardTitle(), updateDto.getBoardContent());
	}

	@Override
	@Transactional
	public boolean deleteBoard(Long boardNo) {
		Board board = boardRepository.findByBoardNoAndIsDeletedFalse(boardNo).orElse(null);
		if (board == null) return false;

		// 파일 물리 삭제
		board.getImages().forEach(img -> fileStore.deleteFile(img.getBoardImgName()));

		board.delete(); // Soft Delete
		return true;
	}

	private BoardResponse.List convertToListResponse(Board board) {
		BoardResponse.List res = new BoardResponse.List();
		res.setBoardNo(board.getBoardNo());
		res.setBoardTitle(board.getBoardTitle());
		res.setBoardWriter(board.getBoardWriter());
		res.setViewCnt(board.getViewCnt());
		res.setCreatedAt(board.getCreatedAt());

		// 썸네일 경로 추출
		board.getImages().stream()
				.filter(BoardImage::isThumbnail)
				.findFirst()
				.ifPresent(img -> res.setThumbnailPath(img.getImgPath()));

		return res;
	}

	private BoardResponse.Detail convertToDetailResponse(Board board) {
		BoardResponse.Detail res = new BoardResponse.Detail();
		res.setBoardNo(board.getBoardNo());
		res.setBoardTitle(board.getBoardTitle());
		res.setBoardWriter(board.getBoardWriter());
		res.setBoardContent(board.getBoardContent());
		res.setViewCnt(board.getViewCnt());
		res.setCreatedAt(board.getCreatedAt());
		res.setUserNo(board.getUser().getUserNo());

		// 기존 DTO 구조(com.ssafy.sm.domain.board.dto.BoardImage) 호환을 위한 변환 작업 필요 시 수행
		return res;
	}
}