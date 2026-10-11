package com.ssafy.sm.domain.place.entity;

import com.ssafy.sm.domain.place.dto.PlaceDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "place")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_no")
    private Long placeNo;

    @Column(name = "content_id", unique = true, nullable = false)
    private Long contentId;

    @Column(name = "content_type_id")
    private Integer contentTypeId;

    @Column(name = "title")
    private String title;

    @Column(name = "addr1")
    private String addr1;

    @Column(name = "addr2")
    private String addr2;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "mapx")
    private Double mapx;

    @Column(name = "mapy")
    private Double mapy;

    @Column(name = "first_image")
    private String firstImage;

    @Column(name = "first_image2")
    private String firstImage2;

    @Column(name = "tel")
    private String tel;

    // DTO에 존재하는 theme 필드 추가
    @Column(name = "theme")
    private String theme;

    // DTO에 존재하는 rating 필드 추가
    @Column(name = "rating")
    @ColumnDefault("0.0")
    private Float rating;

    // 한국어 정보 (nullable)
    @Column(name = "title_ko")
    private String titleKo;

    @Column(name = "addr1_ko")
    private String addr1Ko;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // DTO -> Entity 변환을 위한 생성자
    public Place(PlaceDto.Detail dto) {
        this.contentId = dto.getContentId();
        this.contentTypeId = dto.getContentTypeId();
        this.title = dto.getTitle();
        this.addr1 = dto.getAddr1();
        this.addr2 = dto.getAddr2();
        this.zipcode = dto.getZipcode();
        this.mapx = dto.getMapx();
        this.mapy = dto.getMapy();
        this.firstImage = dto.getFirstImage();
        this.firstImage2 = dto.getFirstImage2();
        this.tel = dto.getTel();

        // 추가된 필드 매핑
        this.theme = dto.getTheme();
        this.rating = dto.getRating();

        // DTO에 한국어 정보가 있다면 설정
        this.titleKo = dto.getTitleKo();
        this.addr1Ko = dto.getAddr1Ko();
    }

    // 데이터 업데이트 메서드 (기존 MyBatis 로직 유지: title, addr, image, tel만 업데이트)
    public void updateEnglishInfo(PlaceDto.Detail dto) {
        this.title = dto.getTitle();
        this.addr1 = dto.getAddr1();
        this.addr2 = dto.getAddr2();
        this.firstImage = dto.getFirstImage();
        this.tel = dto.getTel();
        // rating이나 theme은 API 응답에 따라 업데이트할 필요가 있다면 여기에 추가
    }

    // 평점 업데이트 메서드
    public void updateRating(Float rating) {
        this.rating = rating;
    }

    // 한국어 데이터 업데이트 메서드
    public void updateKoreanInfo(String titleKo, String addr1Ko) {
        this.titleKo = titleKo;
        this.addr1Ko = addr1Ko;
    }

    // Entity -> DTO 변환 메서드
    public PlaceDto.Detail toDetailDto() {
        PlaceDto.Detail dto = new PlaceDto.Detail();
        dto.setPlaceNo(this.placeNo);
        dto.setContentId(this.contentId);
        dto.setContentTypeId(this.contentTypeId);
        dto.setTitle(this.title);
        dto.setAddr1(this.addr1);
        dto.setAddr2(this.addr2);
        dto.setTitleKo(this.titleKo);
        dto.setAddr1Ko(this.addr1Ko);
        dto.setZipcode(this.zipcode);
        dto.setMapx(this.mapx);
        dto.setMapy(this.mapy);
        dto.setFirstImage(this.firstImage);
        dto.setFirstImage2(this.firstImage2);
        dto.setTel(this.tel);

        // 추가된 필드 매핑
        dto.setTheme(this.theme);
        dto.setRating(this.rating);

        return dto;
    }
}