package com.ssafy.sm.domain.board.entity;

import com.ssafy.sm.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_no")
    private Long boardNo;

    @Column(name = "board_title", nullable = false)
    private String boardTitle;

    @Column(name = "board_writer")
    private String boardWriter;

    @Column(name = "board_content", columnDefinition = "TEXT")
    private String boardContent;

    @Column(name = "view_cnt")
    private int viewCnt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardImage> images = new ArrayList<>();

    // 비즈니스 로직: 조회수 증가
    public void increaseViewCnt() {
        this.viewCnt++;
    }

    // 비즈니스 로직: 게시글 수정
    public void update(String title, String content) {
        if (title != null) this.boardTitle = title;
        if (content != null) this.boardContent = content;
    }

    // 비즈니스 로직: 삭제 처리
    public void delete() {
        this.isDeleted = true;
    }

    // 연관 관계 편의 메서드
    public void addImage(BoardImage image) {
        this.images.add(image);
        image.setBoard(this);
    }
}