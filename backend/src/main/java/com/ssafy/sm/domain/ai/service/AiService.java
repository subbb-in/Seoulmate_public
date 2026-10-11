package com.ssafy.sm.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sm.domain.ai.dto.AiDto;
import com.ssafy.sm.domain.ai.dto.AiMappedDto;
import com.ssafy.sm.domain.place.dto.PlaceDto;
import com.ssafy.sm.domain.place.mapper.PlaceMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ObjectMapper objectMapper;
    private final PlaceMapper placeMapper;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    //아래의 Value들은 properties 파일에서 관리 필요
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @PostConstruct
    public void init() {
        if (this.apiKey != null) {
            this.apiKey = this.apiKey.replace("\"", "").trim();
        }
    }

    public AiMappedDto getRecommendAndMap(AiDto.Request userRequest) {
        AiDto.Response aiResponse = getRecommendation(userRequest);
        return mapToPlaceData(aiResponse);
    }

    // AI에게 보낼 프롬프트 작성 및 전송, 답변 처리 메서드
    public AiDto.Response getRecommendation(AiDto.Request userRequest) {
        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("model", model);
            requestBodyMap.put("temperature", 0.3);

            // 프롬프트 텍스트 생성 및 역할 부여
            String prompt = createSystemPrompt(userRequest);
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content",
                    "You are an expert local tour guide in Seoul. Provide optimized routes with transport info."
            ));
            messages.add(Map.of("role", "user", "content", prompt));

            requestBodyMap.put("messages", messages);

            String jsonBody = objectMapper.writeValueAsString(requestBodyMap);
            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseString = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    throw new RuntimeException("AI Error: " + response.code());
                }

                com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseString);
                String answerContent = rootNode.path("choices").get(0).path("message").path("content").asText();
                answerContent = answerContent.replaceAll("```json|```", "").trim();

                return objectMapper.readValue(answerContent, AiDto.Response.class);
            }

        } catch (Exception e) {
            log.error("AI Service Error", e);
            throw new RuntimeException("AI 추천 서비스 오류: " + e.getMessage());
        }
    }

    // AI의 대답(JSON 형태)을, 우리 서비스의 DB에 있는 place와 연결
    private AiMappedDto mapToPlaceData(AiDto.Response aiResponse) {
        AiMappedDto mappedDto = new AiMappedDto();
        mappedDto.setTitle(aiResponse.getTitle());
        List<AiMappedDto.MappedDayPlan> mappedItinerary = new ArrayList<>();

        if (aiResponse.getItinerary() != null) {
            for (AiDto.Response.DayPlan dayPlan : aiResponse.getItinerary()) {
                AiMappedDto.MappedDayPlan mappedDay = new AiMappedDto.MappedDayPlan();
                mappedDay.setDay(dayPlan.getDay());

                List<AiMappedDto.MappedPlace> mappedPlaces = new ArrayList<>();
                if (dayPlan.getPlaces() != null) {
                    for (AiDto.Response.PlaceInfo placeInfo : dayPlan.getPlaces()) {
                        List<PlaceDto.Detail> searchResults = placeMapper.findPlaceByTitle(placeInfo.getPlaceName());

                        if (!searchResults.isEmpty()) {
                            AiMappedDto.MappedPlace mappedPlace = new AiMappedDto.MappedPlace();
                            mappedPlace.setPlaceDetail(searchResults.get(0));
                            mappedPlace.setMoveType(placeInfo.getMoveType());
                            mappedPlace.setMoveMinutes(placeInfo.getMoveMinutes());
                            mappedPlaces.add(mappedPlace);
                        } else {
                            log.warn("Mapping failed for: {}", placeInfo.getPlaceName());
                        }
                    }
                }
                mappedDay.setPlaces(mappedPlaces);
                mappedItinerary.add(mappedDay);
            }
        }
        mappedDto.setItinerary(mappedItinerary);
        return mappedDto;
    }

    // AI에게 보낼 프롬프트 메시지 내용
    private String createSystemPrompt(AiDto.Request request) {
        return """
               Plan a trip to Seoul.
               - Period: %s
               - Theme: %s
               - Companions: %s
               
               [Requirements]
               1. 3-4 places per day, optimized for proximity.
               2. Use OFFICIAL English names from KTO (e.g., 'Gyeongbokgung Palace').
               3. **Estimate moveType (BUS, SUBWAY, WALK, TAXI) and moveMinutes (integer) from the PREVIOUS place.** For the first place of the day, set moveMinutes to 0.
               4. Also recommend 2 restaurant per day to visit: in the first, and in the last. Restaurants should be popular, and offers with reasonable price, and not very far from the "places".
                4-1. restaurants should follow the JSON output rule. { "placeName": "Myeongdong Kyoja", "moveType": "WALK", "moveMinutes": 20}
               5. Output JSON ONLY:
               {
                 "title": "Trip Title",
                 "itinerary": [
                   {
                     "day": 1,
                     "places": [
                       { "placeName": "Gyeongbokgung Palace", "moveType": "WALK", "moveMinutes": 0 },
                       { "placeName": "Bukchon Hanok Village", "moveType": "WALK", "moveMinutes": 15 },
                       { "placeName": "Myeongdong Kyoja", "moveType": "WALK", "moveMinutes": 20}
                     ]
                   }
                 ]
               }
               """.formatted(request.getDays(), request.getTheme(), request.getCompanions());
    }
}