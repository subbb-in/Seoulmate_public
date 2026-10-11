import { http, httpFile } from './http';

// 회원가입
export const signup = (payload) => {
  return http.post('/users/signup', payload);
};

// 로그인 (JWT)
export const login = (payload) => http.post('/auth/login', payload);

// 로그아웃 (JWT)
export const logout = () => http.post('/auth/logout');

// 토큰 재발급
export const reissue = (refreshToken) =>
  http.post('/auth/reissue', null, {
    headers: { Authorization: `Bearer ${refreshToken}` },
  });

// 로그인 상태 확인 (JWT - Authorization 헤더로 인증)
export const fetchMe = () => http.get('/users/me');

// 회원 정보 상세 조회
export const getUserInfo = async (userNo) => {
  const response = await http.get(`/users/${userNo}`);
  return response.data;
};

/////

export const updateUserInfo = async (userNo, userData, file) => {
  const formData = new FormData();
  formData.append('user', JSON.stringify(userData));
  if (file) {
    formData.append('file', file);
  }
  const response = await httpFile.patch(`/users/${userNo}`, formData);
  return response.data;
};

// 프로필 사진 수정 -> FormData 전송
export const updateUserProfileImage = async (userNo, file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    const response = await httpFile.patch(`/users/${userNo}/image`, formData);
    return response.data;
  } catch (error) {
    console.error('Failed to update profile image:', error);
    throw error;
  }
};

// 회원 탈퇴(soft delete)
export const deleteUser = async (userNo, password) => {
  const response = await http.post(`/users/${userNo}`, { password });
  return response.data;
};
