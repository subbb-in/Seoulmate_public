package com.ssafy.sm.domain.itineraryPlace.entity;

import com.ssafy.sm.domain.itinerary.entity.Itinerary;
import com.ssafy.sm.domain.place.entity.Place;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "itinerary_place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ItineraryPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itin_place_no")
    private Long itinPlaceNo;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itin_no")
    private Itinerary itinerary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_no")
    private Place place;

    @Column(name = "day_order")
    private int dayOrder;

    @Column(name = "order_in_day")
    private int orderInDay;

    @Column(name = "move_type")
    private String moveType;

    @Column(name = "move_minutes")
    private int moveMinutes;

    // 비즈니스 로직: 순서 변경
    public void updateOrder(int dayOrder, int orderInDay) {
        this.dayOrder = dayOrder;
        this.orderInDay = orderInDay;
    }
}
