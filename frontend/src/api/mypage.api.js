import { http, httpFile } from "./http";

// 내 정보 조회
// GET /api/mypage/profile
export const fetchMyProfile = async () => {
  try {
    const response = await http.get("/mypage/profile");
    console.log(response.data)
    return response.data;
  } catch (error) {
    console.error("Failed to fetch profile:", error);
    throw error;
  }
};

// 내 정보 수정 (닉네임, 프로필 사진 등)
export const updateMyProfile = async (profileData) => {
  try {
    // 파일 업로드가 포함될 수 있으므로 httpFile 사용
    const response = await httpFile.put("/mypage/profile", profileData);
    return response.data;
  } catch (error) {
    console.error("Failed to update profile:", error);
    throw error;
  }
};

// 내 일정 목록 조회
// GET /api/mypage/itineraries
export const fetchMyItineraries = async () => {
  try {
    const response = await http.get("/mypage/itineraries");
    return response.data;
  } catch (error) {
    console.error("Failed to fetch my itineraries:", error);
    throw error;
  }
};

// 내 리뷰 목록 조회
// GET /api/mypage/reviews
export const fetchMyReviews = async () => {
  try {
    const response = await http.get("/mypage/reviews");
    return response.data;
  } catch (error) {
    console.error("Failed to fetch my reviews:", error);
    throw error;
  }
};

// 내가 찜한 장소 목록 조회
// GET /api/mypage/places
export const fetchMyBookmarks = async () => {
  try {
    const response = await http.get("/mypage/places");
    return response.data;
  } catch (error) {
    console.error("Failed to fetch my bookmarks:", error);
    throw error;
  }
};