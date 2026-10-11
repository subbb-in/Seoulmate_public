package com.ssafy.sm.domain.place.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.domain.place.dto.PlaceDto;
import com.ssafy.sm.domain.place.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    // [관리자용] 영문 저장 + 한국어 매핑
    // POST /api/places/fetch
    @PostMapping("/fetch")
    public ResponseEntity<ApiResponse<Integer>> fetchPlaces() {
        int savedCount = placeService.fetchAndSavePlaces();
        return ResponseEntity.ok(ApiResponse.success("장소 데이터 동기화 완료 (저장된 영문 장소 수: " + savedCount + ")", savedCount));
    }

    // 전체 장소 조회
    // GET /api/places
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaceDto.Detail>>> getAllPlaces() {
        List<PlaceDto.Detail> list = placeService.getAllPlaces();
        return ResponseEntity.ok(ApiResponse.success("장소 목록 조회 성공", list));
    }

    // 장소 상세 조회
    // GET /api/places/{placeNo}
    @GetMapping("/{placeNo}")
    public ResponseEntity<ApiResponse<PlaceDto.Detail>> getPlaceDetail(@PathVariable Long placeNo) {
        try {
            PlaceDto.Detail detail = placeService.getPlaceDetail(placeNo);
            return ResponseEntity.ok(ApiResponse.success("장소 상세 조회 성공", detail));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}