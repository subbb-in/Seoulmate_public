package com.ssafy.sm.domain.itineraryPlace.service;

import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import com.ssafy.sm.domain.itinerary.entity.Itinerary;
import com.ssafy.sm.domain.itinerary.repository.ItineraryRepository;
import com.ssafy.sm.domain.itineraryPlace.dto.ItineraryPlaceRequest;
import com.ssafy.sm.domain.itineraryPlace.entity.ItineraryPlace;
import com.ssafy.sm.domain.itineraryPlace.repository.ItineraryPlaceRepository;
import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItineraryPlaceServiceImpl implements ItineraryPlaceService {

    private final ItineraryPlaceRepository itineraryPlaceRepository;
    private final ItineraryRepository itineraryRepository;
    private final PlaceRepository placeRepository;

    @Override
    @Transactional
    public List<ItineraryResponse.ItinPlaceDetail> addItineraryPlace(Long userNo, ItineraryPlaceRequest.Create createDto) {
        Itinerary itinerary = itineraryRepository.findById(createDto.getItinNo())
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        Place place = placeRepository.findById(createDto.getPlaceNo())
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        ItineraryPlace itineraryPlace = ItineraryPlace.builder()
                .itinerary(itinerary)
                .place(place)
                .dayOrder(createDto.getDayOrder())
                .orderInDay(createDto.getOrderInDay())
                .moveType(createDto.getMoveType())
                .moveMinutes(createDto.getMoveMinutes())
                .build();

        itineraryPlaceRepository.save(itineraryPlace);

        return getPlaceListByItinNo(createDto.getItinNo());
    }

    @Override
    @Transactional
    public List<ItineraryResponse.ItinPlaceDetail> removeItineraryPlace(Long userNo, Long itinNo, Long itinPlaceNo) {
        ItineraryPlace itineraryPlace = itineraryPlaceRepository
                .findByItinPlaceNoAndItinerary_ItinNo(itinPlaceNo, itinNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

        itineraryPlaceRepository.delete(itineraryPlace);

        return getPlaceListByItinNo(itinNo);
    }

    @Override
    @Transactional
    public List<ItineraryResponse.ItinPlaceDetail> updateItineraryPlaceOrder(Long userNo, Long itinNo, List<ItineraryPlaceRequest.UpdateOrder> orderList) {
        for (ItineraryPlaceRequest.UpdateOrder item : orderList) {
            ItineraryPlace itineraryPlace = itineraryPlaceRepository
                    .findByItinPlaceNoAndItinerary_ItinNo(item.getItinPlaceNo(), itinNo)
                    .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

            itineraryPlace.updateOrder(item.getDayOrder(), item.getOrderInDay());
        }

        return getPlaceListByItinNo(itinNo);
    }

    // 특정 일정의 장소 목록 조회 (DTO 변환)
    private List<ItineraryResponse.ItinPlaceDetail> getPlaceListByItinNo(Long itinNo) {
        return itineraryPlaceRepository
                .findAllByItinerary_ItinNoOrderByDayOrderAscOrderInDayAsc(itinNo).stream()
                .map(this::convertToItinPlaceDetail)
                .collect(Collectors.toList());
    }

    // ItineraryPlace Entity -> ItinPlaceDetail DTO 변환
    private ItineraryResponse.ItinPlaceDetail convertToItinPlaceDetail(ItineraryPlace ip) {
        Place p = ip.getPlace();
        return new ItineraryResponse.ItinPlaceDetail(
                ip.getItinPlaceNo(),
                ip.getDayOrder(),
                ip.getOrderInDay(),
                ip.getMoveType(),
                ip.getMoveMinutes(),
                p.getPlaceNo(),
                ip.getItinerary().getItinNo(),
                p.getContentId(),
                p.getContentTypeId() != null ? p.getContentTypeId().longValue() : null,
                p.getTitle(),
                p.getAddr1(),
                p.getAddr2(),
                null, // description
                p.getTitleKo(),
                p.getAddr1Ko(),
                p.getZipcode(),
                null, // opening
                null, // closing
                p.getRating(),
                p.getMapx() != null ? p.getMapx().floatValue() : null,
                p.getMapy() != null ? p.getMapy().floatValue() : null,
                p.getFirstImage(),
                p.getFirstImage2(),
                p.getTel(),
                null  // totalMinutes
        );
    }
}
