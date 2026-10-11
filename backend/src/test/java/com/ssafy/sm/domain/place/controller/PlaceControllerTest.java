package com.ssafy.sm.domain.place.controller;

import com.ssafy.sm.domain.place.dto.PlaceDto;
import com.ssafy.sm.domain.place.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaceController.class)
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @Test
    @DisplayName("전체 장소 조회 성공 테스트")
    void getAllPlacesTest() throws Exception {
        // given
        PlaceDto.Detail place1 = createPlaceDetail(1L, "Lotte World", 127.1, 37.5);
        PlaceDto.Detail place2 = createPlaceDetail(2L, "N Seoul Tower", 126.9, 37.5);
        List<PlaceDto.Detail> places = Arrays.asList(place1, place2);

        given(placeService.getAllPlaces()).willReturn(places);

        // when & then
        mockMvc.perform(get("/api/places")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 응답이 { "message": "...", "data": [...] } 형태이므로 경로 수정
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Lotte World"))
                .andExpect(jsonPath("$.data[1].title").value("N Seoul Tower"))
                .andDo(print());
    }

    @Test
    @DisplayName("장소 상세 조회 성공 테스트")
    void getPlaceDetailTest() throws Exception {
        // given
        Long placeNo = 1L;
        PlaceDto.Detail place = createPlaceDetail(placeNo, "Lotte World", 127.1, 37.5);

        given(placeService.getPlaceDetail(placeNo)).willReturn(place);

        // when & then
        mockMvc.perform(get("/api/places/{placeNo}", placeNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 상세 조회 응답 구조에 따라 경로 수정 (예: { "message": "...", "data": { ... } })
                // 만약 상세 조회도 ApiResponse로 감싸져 있다면 $.data.placeNo, 아니면 $.placeNo
                // 로그를 볼 때 전체 조회와 동일한 ApiResponse 패턴을 쓸 것으로 추정됨.
                .andExpect(jsonPath("$.data.placeNo").value(placeNo))
                .andExpect(jsonPath("$.data.title").value("Lotte World"))
                .andDo(print());
    }

    // 헬퍼 메서드
    private PlaceDto.Detail createPlaceDetail(Long placeNo, String title, Double mapx, Double mapy) {
        PlaceDto.Detail detail = new PlaceDto.Detail();
        detail.setPlaceNo(placeNo);
        detail.setTitle(title);
        detail.setMapx(mapx);
        detail.setMapy(mapy);
        detail.setContentId(10000L + placeNo);
        detail.setContentTypeId(12);
        return detail;
    }
}