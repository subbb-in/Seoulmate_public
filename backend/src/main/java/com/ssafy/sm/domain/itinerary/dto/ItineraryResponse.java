package com.ssafy.sm.domain.itinerary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class ItineraryResponse {

	@Getter
	@AllArgsConstructor
	public static class GetAll {
		private Long itinNo;
		private String itinTitle;
		private int dayCnt;
		private String thumbnail;
	}

	@Getter
	@AllArgsConstructor
	public static class Basic {
		private Long itinNo;
		private String itinTitle;
		private int dayCnt;
		private String thumbnail;
	}

	@Getter
	@AllArgsConstructor
	public static class Page {
		private List<GetAll> content;
		private int totalCount;
		private int page;
		private int size;
	}

	// 상세 장소 조회
	@Getter
	@AllArgsConstructor
	public static class DetailWithPlaces {
		private Basic basic; // 여행 제목, 날짜 수(dayCnt) 등 기본 정보
		private List<ItinPlaceDetail> places; // 장소 목록
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ItinPlaceDetail {
		private Long itinPlaceNo;
		private int dayOrder;
		private int orderInDay;
		private String moveType;
		private int moveMinutes;

		// FK
		private Long placeNo;
		private Long itinNo;

		// place 테이블
		private Long contentId;
		private Long contentTypeId;
		private String title;
		private String addr1;
		private String addr2;
		private String description;
		private String titleKo;
		private String addr1Ko;
		private String zipcode;
		private String opening;
		private String closing;
		private Float rating;
		private Float mapx;
		private Float mapy;
		private String firstImage;
		private String firstImage2;
		private String tel;
		private Integer totalMinutes;
	}
}
