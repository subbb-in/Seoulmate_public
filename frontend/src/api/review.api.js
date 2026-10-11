import { http, httpFile } from "./http";

// 특정 장소의 리뷰 목록 조회
// GET /api/reviews/place/{placeNo}
export const fetchReviewsByPlace = async (placeNo) => {
  try {
    const response = await http.get(`/reviews/place/${placeNo}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch reviews for place ${placeNo}:`, error);
    throw error; // 에러를 상위로 전파하여 Vue 컴포넌트에서 잡을 수 있게 함
  }
};

// 리뷰 작성 (이미지 포함 가능)
// POST /api/reviews
export const createReview = async (reviewData) => {
  try {
    const response = await httpFile.post("/reviews", reviewData);
    return response.data;
  } catch (error) {
    console.error("Failed to create review:", error);
    throw error;
  }
};

// 리뷰 수정
// PATCH /api/reviews/{reviewNo}
export const updateReview = async (reviewNo, reviewData) => {
  try {
    // 백엔드 컨트롤러가 @RequestBody(JSON)를 받으므로 http 사용
    const response = await http.patch(`/reviews/${reviewNo}`, reviewData);
    return response.data;
  } catch (error) {
    console.error(`Failed to update review (${reviewNo}):`, error);
    throw error;
  }
};

// 리뷰 삭제
// DELETE /api/reviews/{reviewNo}
export const deleteReview = async (reviewNo) => {
  try {
    const response = await http.delete(`/reviews/${reviewNo}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to delete review (${reviewNo}):`, error);
    throw error;
  }
};