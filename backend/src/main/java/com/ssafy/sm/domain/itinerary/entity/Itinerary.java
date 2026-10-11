package com.ssafy.sm.domain.itinerary.entity;

import com.ssafy.sm.domain.itineraryPlace.entity.ItineraryPlace;
import com.ssafy.sm.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itinerary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itin_no")
    private Long itinNo;

    @Column(name = "itin_title", nullable = false)
    private String itinTitle;

    @Column(name = "day_cnt")
    private int dayCnt;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItineraryPlace> places = new ArrayList<>();

    // 비즈니스 로직: 삭제 (Soft Delete)
    public void delete() {
        this.isDeleted = true;
    }

    // 연관 관계 편의 메서드
    public void addPlace(ItineraryPlace place) {
        this.places.add(place);
        place.setItinerary(this);
    }
}
