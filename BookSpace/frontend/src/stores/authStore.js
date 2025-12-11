// 인증 / 유저 관련 전역 상태
import { defineStore } from "pinia";
import { loginApi } from "@/api/authApi";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null, // 로그인한 사용자 정보 (나중에 받아오기)
    token: localStorage.getItem("accessToken") || null, // 새로고침해도 토큰 유지
    isLoggedIn: !!localStorage.getItem("accessToken"), // 토큰 존재 여부로 로그인 상태 판별
  }),

  actions: {
    async login(payload) {
      // payload: {userLoginId, userPw}
      const data = await loginApi(payload); // 에러 시 그대로 throw

      // 백엔드 응답 : {accessToken, refreshToken}
      localStorage.setItem("accessToken", data.accessToken);

      this.token = data.accessToken;
      this.isLoggedIn = true;

      // 나중에 여기서 userStore.fetchMyInfo() 호출해서 this.user 채워도 됨
    },

    logout() {
      localStorage.removeItem("accessToken");
      this.user = null;
      this.token = null;
      this.isLoggedIn = false;
    },
  }
});
