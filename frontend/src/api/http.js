import axios from 'axios';
import router from '@/router';

// Axios 인스턴스 생성
const http = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
});

// 파일 업로드 전용 인스턴스 (Multipart/form-data)
const httpFile = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'multipart/form-data',
  },
});

// 토큰 관리
const getAccessToken = () => localStorage.getItem('accessToken');
const getRefreshToken = () => localStorage.getItem('refreshToken');
const setTokens = (accessToken, refreshToken) => {
  localStorage.setItem('accessToken', accessToken);
  if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
};
const clearTokens = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
};

// Request 인터셉터: Authorization 헤더 자동 추가
const requestInterceptor = (config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

http.interceptors.request.use(requestInterceptor);
httpFile.interceptors.request.use(requestInterceptor);

// Response 인터셉터: 401 시 토큰 재발급 시도
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve(token);
    }
  });
  failedQueue = [];
};

const responseErrorInterceptor = async (error) => {
  const originalRequest = error.config;

  // 401이고 재시도가 아닌 경우에만 토큰 재발급 시도
  if (error.response?.status === 401 && !originalRequest._retry) {
    // 로그인/재발급 요청 자체가 401이면 로그인 페이지로
    if (originalRequest.url?.includes('/auth/login') || originalRequest.url?.includes('/auth/reissue')) {
      clearTokens();
      router.push('/login');
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      }).then((token) => {
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return http(originalRequest);
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      clearTokens();
      router.push('/login');
      return Promise.reject(error);
    }

    try {
      const { data } = await axios.post('/api/auth/reissue', null, {
        headers: { Authorization: `Bearer ${refreshToken}` },
      });

      const newAccessToken = data.data.accessToken;
      const newRefreshToken = data.data.refreshToken;
      setTokens(newAccessToken, newRefreshToken);

      processQueue(null, newAccessToken);

      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
      return http(originalRequest);
    } catch (refreshError) {
      processQueue(refreshError, null);
      clearTokens();
      router.push('/login');
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }

  return Promise.reject(error);
};

http.interceptors.response.use((res) => res, responseErrorInterceptor);
httpFile.interceptors.response.use((res) => res, responseErrorInterceptor);

export { http, httpFile, setTokens, clearTokens, getAccessToken };
