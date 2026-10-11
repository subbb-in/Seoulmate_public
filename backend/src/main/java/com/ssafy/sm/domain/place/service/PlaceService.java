package com.ssafy.sm.domain.place.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sm.domain.place.dto.PlaceDto;
import com.ssafy.sm.domain.place.entity.Place;
import com.ssafy.sm.domain.place.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlaceService {

    private final PlaceRepository placeRepository; // Mapper 대신 Repository 사용
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${tour-api.key}")
    private String serviceKey;

    public PlaceService(PlaceRepository placeRepository, RestTemplate restTemplate) {
        this.placeRepository = placeRepository;
        this.restTemplate = restTemplate;
        this.restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 영문 데이터만 수집 (AI 매칭도 영문명으로 진행)
     */
    @Transactional
    public int fetchAndSavePlaces() {
        int totalSaved = 0;
        int fetchCount = 2000;

        log.info("=== 영문 인기 장소 데이터 수집 시작 (Popularity Sort) ===");
        int[] engTypes = {76, 78, 85, 75, 79, 82, 77, 80};
        totalSaved += fetchEnglishData(engTypes, fetchCount);

        log.info("=== 데이터 수집 완료 (Total: {}) ===", totalSaved);
        return totalSaved;
    }

    private int fetchEnglishData(int[] contentTypes, int numOfRows) {
        String baseUrl = "http://apis.data.go.kr/B551011/EngService2/areaBasedList2";
        int savedCount = 0;

        for (int contentTypeId : contentTypes) {
            log.info("Fetching Eng Type: {}", contentTypeId);
            try {
                // ... (URI 생성 부분은 기존과 동일) ...
                URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("pageNo", 1)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "SeoulMate")
                        .queryParam("areaCode", 1)
                        .queryParam("contentTypeId", contentTypeId)
                        .queryParam("_type", "json")
                        .queryParam("arrange", "P")
                        .build(true)
                        .toUri();

                String response = restTemplate.getForObject(uri, String.class);

                if (response != null && response.trim().startsWith("{")) {
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode items = root.path("response").path("body").path("items").path("item");

                    if (items.isArray()) {
                        for (JsonNode node : items) {
                            savePlaceNode(node);
                            savedCount++;
                        }
                    } else if (items.isObject()) {
                        savePlaceNode(items);
                        savedCount++;
                    }
                }
            } catch (Exception e) {
                log.warn("Error fetching type {}: {}", contentTypeId, e.getMessage());
            }
        }
        return savedCount;
    }

    // JPA를 이용한 Upsert 로직 구현
    private void savePlaceNode(JsonNode node) {
        try {
            PlaceDto.TourApiItem apiItem = objectMapper.treeToValue(node, PlaceDto.TourApiItem.class);
            PlaceDto.Detail detailDto = new PlaceDto.Detail(apiItem);

            // contentId로 기존 데이터 조회
            placeRepository.findByContentId(detailDto.getContentId())
                    .ifPresentOrElse(
                            // 존재하면 업데이트 (Dirty Checking)
                            existingPlace -> existingPlace.updateEnglishInfo(detailDto),
                            // 없으면 새로 저장
                            () -> placeRepository.save(new Place(detailDto))
                    );

        } catch (Exception e) {
            log.error("Save Error", e);
        }
    }

    // 한국어 정보 업데이트 로직 (MyBatis updateKoreanInfo 대체)
    @Transactional
    public void updateKoreanInfo(Long contentId, String titleKo, String addr1Ko) {
        placeRepository.findByContentId(contentId)
                .ifPresent(place -> place.updateKoreanInfo(titleKo, addr1Ko));
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<PlaceDto.Detail> getAllPlaces() {
        return placeRepository.findAllByOrderByTitleAsc().stream()
                .map(Place::toDetailDto)
                .collect(Collectors.toList());
    }

    // 상세 조회
    @Transactional(readOnly = true)
    public PlaceDto.Detail getPlaceDetail(Long placeNo) {
        return placeRepository.findById(placeNo)
                .map(Place::toDetailDto)
                .orElse(null); // 또는 예외 던지기
    }

    // 검색 로직 (MyBatis findPlaceByTitle 대체)
    @Transactional(readOnly = true)
    public List<PlaceDto.Detail> searchPlacesByTitle(String keyword) {
        // Limit 5를 위해 PageRequest 사용
        return placeRepository.findByTitleContainingOrderByLength(keyword, PageRequest.of(0, 5))
                .stream()
                .map(Place::toDetailDto)
                .collect(Collectors.toList());
    }
}