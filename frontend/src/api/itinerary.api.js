import { http } from "./http";

// 일정 생성
// POST /api/itineraries
export const createItinerary = async (data) => {
  // data: { itinTitle, dayCnt, startDate, endDate, ... }
  const response = await http.post("/itineraries", data);
  return response; 
};

// 일정 목록 조회
// GET /api/itineraries
export const getItineraries = async (params) => {
  // params: { page, size }
  const response = await http.get("/itineraries", { params });
  return response;
};

// 일정 삭제
// DELETE /api/itinerary/{itinNo}
export const deleteItinerary = async (itinNo) => {
  const response = await http.delete(`/itineraries/${itinNo}`);
  return response;
};

// AI 일정 저장
// POST /api/itineraries/ai
export const saveAiItinerary = async (payload) => {
  // payload 구조: { title, dayCnt, itinerary: [ { day, places: [ { placeNo, moveType, moveMinutes } ] } ] }
  const response = await http.post("/itineraries/ai", payload);
  return response;
};

// 일정 상세 조회
// GET /api/itineraries/{itinNo}
export const getItineraryDetail = async (itinNo) => {
  try {
    const response = await http.get(`/itineraries/${itinNo}`);
    return response.data; // ApiResponse.success -> { message, data: DetailWithPlaces }
  } catch (error) {
    console.error("Failed to fetch itinerary detail:", error);
    throw error;
  }
};

// 일정에 장소 추가
// POST /api/itinerary-places
export const addItineraryPlace = async (placeData) => {
    // placeData: { itinNo, placeNo, dayOrder, moveType, moveMinutes, ... }
    try {
        const response = await http.post(`/itinerary-places`, placeData);
        return response.data;
    } catch (error) {
        console.error("Failed to add place:", error);
        throw error;
    }
};

// 일정 장소 삭제
// DELETE /api/itinerary-places/{itinNo}/{itinPlaceNo}
export const deleteItineraryPlace = async (itinNo, itinPlaceNo) => {
    try {
        const response = await http.delete(`/itinerary-places/${itinNo}/${itinPlaceNo}`);
        return response.data;
    } catch (error) {
        console.error("Failed to delete place:", error);
        throw error;
    }
};