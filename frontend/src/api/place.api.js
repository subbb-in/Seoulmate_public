import { http } from "./http";

// 장소 검색 (목록 조회)
// GET /api/places
export const searchPlaces = async (params) => {
  // params: { pageNo, numOfRows, contentTypeId, keyword, sidoCode }
  try {
    const response = await http.get("/places", { params });
    return response.data; // ApiResponse 구조에 따라 .data.data 등이 될 수 있음
  } catch (error) {
    console.error("Failed to search places:", error);
    throw error;
  }
};

// 장소 상세 조회
// GET /api/places/{placeNo}
export const getPlaceDetail = async (contentId) => {
  try {
    const response = await http.get(`/places/${contentId}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to get place detail (${contentId}):`, error);
    throw error;
  }
};