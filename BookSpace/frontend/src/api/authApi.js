// authentication / authorization 관련 api
// src/api/authApi.js

import httpClient from "./httpClient";

// 로그인 (JWT 발급)
export const loginApi = (payload) => {
  // payload: { userLoginId: string, userPw: string }
  return httpClient.post("/auth/login", payload).then((res) => res.data);
};
