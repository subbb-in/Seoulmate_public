package com.ssafy.sm.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_img_no")
    private Long boardImgNo;
    // 연관 관계 설정을 위한 Setter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no")
    private Board board;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "board_img_name")
    private String boardImgName;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_thumbnail")
    private boolean isThumbnail;
}
