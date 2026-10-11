import { http } from "@/api/http";

// AI 일정 추천 요청
// POST /api/ai/recommend
export const generateAiItinerary = async (payload) => {
  // payload: { destination, days, theme, companions }
  // 예: { destination: "Seoul", days: "2박 3일", theme: "맛집 탐방", companions: "친구" }
  try {
    const response = await http.post("/ai/recommend", payload);
    return response.data; // ApiResponse 객체 반환
  } catch (error) {
    console.error("AI Generation Failed:", error);
    throw error;
  }
};