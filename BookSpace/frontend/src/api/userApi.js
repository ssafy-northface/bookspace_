// src/api/userApi.js
import httpClient from "./httpClient";

// 1. 회원가입
export const signupApi = (payload) => {
  // payload: SignupRequestDto 구조 그대로
  return httpClient.post("/users/signup", payload).then((res) => res.data);
};

// 3. 회원 정보 조회 (by userId)
export const getUserByIdApi = (userId) => {
  return httpClient.get(`/users/${userId}`).then((res) => res.data);
};

// 4. 회원 정보 수정
export const updateUserApi = (userId, payload) => {
  return httpClient.put(`/users/${userId}`, payload).then((res) => res.data);
};

// 5. 회원 탈퇴 (soft delete)
export const softDeleteUserApi = (userId) => {
  return httpClient.delete(`/users/${userId}`).then((res) => res.data);
};

// 6. 회원 영구 삭제 (hard delete)
export const hardDeleteUserApi = (userId) => {
  return httpClient.delete(`/users/${userId}/hard`).then((res) => res.data);
};

// 7~9. 통합 중복 체크 API
// /users/check?type=loginId&value=xxx
export const checkDuplicateApi = (type, value) => {
  return httpClient
    .get("/users/check", {
      params: { type, value },
    })
    .then((res) => res.data); // true = 사용 가능, false = 이미 존재
};
