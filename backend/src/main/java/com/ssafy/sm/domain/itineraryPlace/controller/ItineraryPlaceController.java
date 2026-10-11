package com.ssafy.sm.domain.itineraryPlace.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.common.util.SecurityUtil;
import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import com.ssafy.sm.domain.itineraryPlace.dto.ItineraryPlaceRequest;
import com.ssafy.sm.domain.itineraryPlace.service.ItineraryPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary-places")
@RequiredArgsConstructor
public class ItineraryPlaceController {

    private final ItineraryPlaceService itineraryPlaceService;

    // 일정에 장소 추가 (POST /api/itinerary-places)
    @PostMapping
    public ResponseEntity<ApiResponse<List<ItineraryResponse.ItinPlaceDetail>>> addPlace(
            @RequestBody ItineraryPlaceRequest.Create createDto) {

        Long userNo = SecurityUtil.getCurrentUserNo();
        List<ItineraryResponse.ItinPlaceDetail> updatedPlaces =
                itineraryPlaceService.addItineraryPlace(userNo, createDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("일정에 장소 추가 성공", updatedPlaces));
    }

    // 일정 속 장소 삭제
    @DeleteMapping("/{itinNo}/{itinPlaceNo}")
    public ResponseEntity<ApiResponse<List<ItineraryResponse.ItinPlaceDetail>>> deleteplace(
            @PathVariable("itinNo") Long itinNo,
            @PathVariable("itinPlaceNo") Long itinPlaceNo) {

        Long userNo = SecurityUtil.getCurrentUserNo();
        List<ItineraryResponse.ItinPlaceDetail> updatedPlaces =
                itineraryPlaceService.removeItineraryPlace(userNo, itinNo, itinPlaceNo);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("일정에 장소 삭제 성공", updatedPlaces));
    }

    // 일정 장소 순서 변경 (여러 개 일괄 수정)
    @PatchMapping("/{itinNo}/order")
    public ResponseEntity<ApiResponse<List<ItineraryResponse.ItinPlaceDetail>>> updatePlaceOrder(
            @PathVariable Long itinNo,
            @RequestBody List<ItineraryPlaceRequest.UpdateOrder> orderList) {

        Long userNo = SecurityUtil.getCurrentUserNo();
        List<ItineraryResponse.ItinPlaceDetail> updatedPlaces =
                itineraryPlaceService.updateItineraryPlaceOrder(userNo, itinNo, orderList);

        return ResponseEntity.ok(ApiResponse.success("순서 변경 성공", updatedPlaces));
    }
}
