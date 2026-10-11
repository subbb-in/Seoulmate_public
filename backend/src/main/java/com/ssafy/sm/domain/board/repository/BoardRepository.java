package com.ssafy.sm.domain.board.repository;

import com.ssafy.sm.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 삭제되지 않은 게시글 목록을 최신순으로 조회
    List<Board> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    // 단일 게시글 조회 (Soft Delete 상태 확인)
    Optional<Board> findByBoardNoAndIsDeletedFalse(Long boardNo);
}
