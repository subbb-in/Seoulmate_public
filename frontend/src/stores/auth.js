import { defineStore } from 'pinia';
import {
  login as loginApi,
  signup as signupApi,
  fetchMe as fetchMeApi,
  logout as logoutApi,
} from '@/api/user.api.js';
import { setTokens, clearTokens, getAccessToken } from '@/api/http';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    checked: false,
    loading: false,
  }),

  getters: {
    isAuthenticated: (state) => !!state.user,
    isAdmin: (state) => state.user && state.user.admin === true,
  },

  actions: {
    // 앱 시작 시 로그인 상태 확인
    async fetchMe() {
      // 토큰이 없으면 API 호출 불필요
      if (!getAccessToken()) {
        this.user = null;
        this.checked = true;
        return;
      }

      try {
        const res = await fetchMeApi();
        this.user = res.data.data;
      } catch (e) {
        this.user = null;
      } finally {
        this.checked = true;
      }
    },

    // 로그인
    async login(payload) {
      this.loading = true;
      try {
        const res = await loginApi(payload);
        const { accessToken, refreshToken, user } = res.data.data;

        // 토큰 저장
        setTokens(accessToken, refreshToken);

        // 유저 정보 저장
        this.user = user;
        this.checked = true;
      } finally {
        this.loading = false;
      }
    },

    // 회원가입
    async signup(payload) {
      this.loading = true;
      try {
        await signupApi(payload);
        await this.login(payload);
      } finally {
        this.loading = false;
      }
    },

    // 로그아웃
    async logout() {
      this.loading = true;
      try {
        await logoutApi();
      } catch (e) {
        // 로그아웃 실패해도 프론트 상태는 정리
      } finally {
        clearTokens();
        this.user = null;
        this.checked = true;
        this.loading = false;
      }
    },
  },
});
