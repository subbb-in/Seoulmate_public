package com.ssafy.sm.domain.itinerary.controller;

import com.ssafy.sm.common.util.SecurityUtil;
import com.ssafy.sm.domain.itinerary.dto.ItineraryResponse;
import com.ssafy.sm.domain.itinerary.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.domain.itinerary.dto.ItineraryRequest;

@RestController
@RequestMapping("/api/itineraries")
@RequiredArgsConstructor
public class ItineraryController {

	private final ItineraryService itineraryService;

	// 여행 일정 생성 (POST /api/itineraries)
	@PostMapping
	public ResponseEntity<ApiResponse<ItineraryResponse.Page>> createItenerary(
			@RequestBody ItineraryRequest.Create createDto,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {

		Long userNo = SecurityUtil.getCurrentUserNo();
		createDto.setUserNo(userNo);

		ItineraryResponse.Page pageResponse = itineraryService.createItinerary(createDto, page, size);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("일정 생성 성공", pageResponse));
	}

	// 여행 일정 목록 (GET /api/itineraries)
	@GetMapping
	public ResponseEntity<ApiResponse<ItineraryResponse.Page>> getAllItinerary(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {

		Long userNo = SecurityUtil.getCurrentUserNo();
		ItineraryResponse.Page pageResponse = itineraryService.getPagedItineraries(userNo, page, size);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("일정 리스트", pageResponse));
	}

	// 여행 일정 상세 조회 (GET /api/itineraries/{itinNo})
	@GetMapping("/{itinNo}")
	public ResponseEntity<ApiResponse<ItineraryResponse.DetailWithPlaces>> getItinerary(@PathVariable("itinNo") Long itinNo) {

		Long userNo = SecurityUtil.getCurrentUserNo();
		ItineraryResponse.DetailWithPlaces detail = itineraryService.getItinerary(userNo, itinNo);

		if (detail == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error("해당 일정을 찾을 수 없습니다."));
		}

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("상세 조회 성공", detail));
	}

	// 여행 일정 삭제 (DELETE /api/itineraries/{itinNo})
	@DeleteMapping("/{itinNo}")
	public ResponseEntity<ApiResponse<ItineraryResponse.Page>> deleteItinerary(
			@PathVariable("itinNo") Long itinNo,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {

		Long userNo = SecurityUtil.getCurrentUserNo();
		itineraryService.deleteItinerary(itinNo, userNo);

		ItineraryResponse.Page pageResponse = itineraryService.getPagedItineraries(userNo, page, size);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("일정 삭제 완료", pageResponse));
	}

    // AI 추천 일정 저장 (POST /api/itineraries/ai)
    @PostMapping("/ai")
    public ResponseEntity<ApiResponse<Long>> saveAiItinerary(@RequestBody ItineraryRequest.SaveAiItinerary saveDto) {

        Long userNo = SecurityUtil.getCurrentUserNo();
        saveDto.setUserNo(userNo);
        Long itinNo = itineraryService.saveAiItinerary(saveDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("AI 일정이 저장되었습니다.", itinNo));
    }
}
