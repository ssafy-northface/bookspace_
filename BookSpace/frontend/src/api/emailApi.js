// 이메일 인증 관련 API
import httpClient from "./httpClient";

// 인증 코드 발송
export const sendVerificationCodeApi = (email) => {
  return httpClient.post("/email/send-code", { email }).then((res) => res.data);
};

// 인증 코드 검증
export const verifyCodeApi = (email, code) => {
  return httpClient.post("/email/verify-code", { email, code }).then((res) => res.data);
};

// 이메일 인증 상태 확인
export const checkEmailVerifiedApi = (email) => {
  return httpClient.get("/email/check", { params: { email } }).then((res) => res.data);
};

