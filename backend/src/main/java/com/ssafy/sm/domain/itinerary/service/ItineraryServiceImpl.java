package com.ssafy.sm.domain.itinerary.service;

import com.ssafy.sm.common.exception.ResourceNotFoundException;
import com.ssafy.sm.domain.itinerary.dto.ItineraryRequest;
import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import com.ssafy.sm.domain.itinerary.entity.Itinerary;
import com.ssafy.sm.domain.itinerary.repository.ItineraryRepository;
import com.ssafy.sm.domain.itineraryPlace.entity.ItineraryPlace;
import com.ssafy.sm.domain.itineraryPlace.repository.ItineraryPlaceRepository;
import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.place.repository.PlaceRepository;
import com.ssafy.sm.domain.user.entity.User;
import com.ssafy.sm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItineraryServiceImpl implements ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final ItineraryPlaceRepository itineraryPlaceRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItineraryResponse.Page createItinerary(ItineraryRequest.Create createDto, int page, int size) {
        User user = userRepository.findById(createDto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String thumbnail = createDto.getThumbnail();
        if (thumbnail == null) {
            thumbnail = "http://tong.visitkorea.or.kr/cms/resource/94/3487594_image2_1.jpg";
        }

        Itinerary itinerary = Itinerary.builder()
                .itinTitle(createDto.getItinTitle())
                .dayCnt(createDto.getDayCnt())
                .thumbnail(thumbnail)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        itineraryRepository.save(itinerary);

        return getPagedItineraries(createDto.getUserNo(), page, size);
    }

    @Override
    @Transactional
    public Long saveAiItinerary(ItineraryRequest.SaveAiItinerary saveDto) {
        User user = userRepository.findById(saveDto.getUserNo())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String thumbnail = "https://your-default-image-url.com/default.jpg";

        if (saveDto.getItinerary() != null && !saveDto.getItinerary().isEmpty()) {
            List<ItineraryRequest.SaveAiItinerary.PlaceInfo> firstDayPlaces = saveDto.getItinerary().get(0).getPlaces();
            if (firstDayPlaces != null && !firstDayPlaces.isEmpty()) {
                Long firstPlaceNo = firstDayPlaces.get(0).getPlaceNo();
                Place firstPlace = placeRepository.findById(firstPlaceNo).orElse(null);
                if (firstPlace != null && firstPlace.getFirstImage() != null && !firstPlace.getFirstImage().isEmpty()) {
                    thumbnail = firstPlace.getFirstImage();
                }
            }
        }

        Itinerary itinerary = Itinerary.builder()
                .itinTitle(saveDto.getTitle())
                .dayCnt(saveDto.getDayCnt())
                .thumbnail(thumbnail)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        itineraryRepository.save(itinerary);

        if (saveDto.getItinerary() != null) {
            for (ItineraryRequest.SaveAiItinerary.DayPlan dayPlan : saveDto.getItinerary()) {
                int day = dayPlan.getDay();
                int order = 1;

                for (ItineraryRequest.SaveAiItinerary.PlaceInfo placeInfo : dayPlan.getPlaces()) {
                    Place place = placeRepository.findById(placeInfo.getPlaceNo())
                            .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

                    ItineraryPlace itineraryPlace = ItineraryPlace.builder()
                            .itinerary(itinerary)
                            .place(place)
                            .dayOrder(day)
                            .orderInDay(order++)
                            .moveType(placeInfo.getMoveType() != null ? placeInfo.getMoveType() : "BUS")
                            .moveMinutes(placeInfo.getMoveMinutes())
                            .build();

                    itineraryPlaceRepository.save(itineraryPlace);
                }
            }
        }
        return itinerary.getItinNo();
    }

    @Override
    public ItineraryResponse.Page getPagedItineraries(Long userNo, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Itinerary> pageResult = itineraryRepository
                .findAllByUser_UserNoAndIsDeletedFalseOrderByCreatedAtDesc(userNo, pageRequest);

        List<ItineraryResponse.GetAll> list = pageResult.getContent().stream()
                .map(itin -> new ItineraryResponse.GetAll(
                        itin.getItinNo(),
                        itin.getItinTitle(),
                        itin.getDayCnt(),
                        itin.getThumbnail()))
                .collect(Collectors.toList());

        int totalCount = (int) pageResult.getTotalElements();

        return new ItineraryResponse.Page(list, totalCount, page, size);
    }

    @Override
    public ItineraryResponse.DetailWithPlaces getItinerary(Long userNo, Long itinNo) {
        Itinerary itinerary = itineraryRepository
                .findByItinNoAndUser_UserNoAndIsDeletedFalse(itinNo, userNo)
                .orElse(null);

        if (itinerary == null) return null;

        ItineraryResponse.Basic basic = new ItineraryResponse.Basic(
                itinerary.getItinNo(),
                itinerary.getItinTitle(),
                itinerary.getDayCnt(),
                itinerary.getThumbnail());

        List<ItineraryPlace> itinPlaces = itineraryPlaceRepository
                .findAllByItinerary_ItinNoOrderByDayOrderAscOrderInDayAsc(itinNo);

        List<ItineraryResponse.ItinPlaceDetail> places = itinPlaces.stream()
                .map(this::convertToItinPlaceDetail)
                .collect(Collectors.toList());

        return new ItineraryResponse.DetailWithPlaces(basic, places);
    }

    @Override
    @Transactional
    public void deleteItinerary(Long itinNo, Long userNo) {
        Itinerary itinerary = itineraryRepository
                .findByItinNoAndUser_UserNoAndIsDeletedFalse(itinNo, userNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ITINERARY_NOT_FOUND",
                        "삭제할 일정이 없거나, 본인 일정이 아닙니다. itinNo=" + itinNo));

        itinerary.delete();
    }

    // ItineraryPlace Entity -> ItinPlaceDetail DTO 변환
    ItineraryResponse.ItinPlaceDetail convertToItinPlaceDetail(ItineraryPlace ip) {
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
