package com.ssafy.sm.domain.ai.controller;

import com.ssafy.sm.common.dto.ApiResponse;
import com.ssafy.sm.domain.ai.dto.AiDto;
import com.ssafy.sm.domain.ai.dto.AiMappedDto; // [중요] 매핑된 DTO import
import com.ssafy.sm.domain.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    // AI 추천 요청
    // POST /api/ai/recommend
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<AiMappedDto>> recommendItinerary(@RequestBody AiDto.Request request) {
        try {
            AiMappedDto response = aiService.getRecommendAndMap(request);

            return ResponseEntity.ok(ApiResponse.success("AI 추천 및 장소 매핑 성공", response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage()));
        }
    }
}