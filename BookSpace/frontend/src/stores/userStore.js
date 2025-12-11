// 회원가입 & 유저 정보 관리
// 여기서 에러 처리하지 않고 그대로 컴포넌트로 전달

// src/stores/userStore.js
import { defineStore } from "pinia";
import { ref } from "vue";
import {
  signupApi,
  getUserByIdApi,
  updateUserApi,
  softDeleteUserApi,
  hardDeleteUserApi,
  checkDuplicateApi,
} from "@/api/userApi";
import { getMyInfoApi } from "../api/authApi";

export const useUserStore = defineStore("user", () => {
  const profile = ref(null); // 현재 보고 있는 유저 정보
  const me = ref(null); // 로그인한 내 정보 (/auth/me)

  // 1. 회원가입
  const signup = async (payload) => {
    // payload: SignupRequestDto
    // 에러 발생 시 그대로 throw → 컴포넌트에서 처리
    const data = await signupApi(payload);
    return data; // 필요하면 응답 데이터 사용
  };

  // 2. 내 정보 조회
  const fetchMyInfo = async () => {
    const data = await getMyInfoApi();
    me.value = data;
    return data;
  };

  const clearMe = () => {
    me.value = null;
  };


  // 3. 회원 정보 조회
  const fetchUserById = async (userId) => {
    const data = await getUserByIdApi(userId);
    profile.value = data;
    return data;
  };

  // 4. 회원 정보 수정
  const updateUser = async (userId, payload) => {
    const data = await updateUserApi(userId, payload);
    profile.value = data;
    return data;
  };

  // 5. 회원 탈퇴 (soft delete)
  const softDeleteUser = async (userId) => {
    await softDeleteUserApi(userId);
  };

  // 6. 회원 영구 삭제 (hard delete)
  const hardDeleteUser = async (userId) => {
    await hardDeleteUserApi(userId);
  };

  // 7~9. 통합 중복 체크
  const checkDuplicate = async (type, value) => {
    const available = await checkDuplicateApi(type, value);
    // available: true = 사용 가능, false = 이미 존재
    return available;
  };

  return {
    profile,
    me,
    signup,
    fetchMyInfo,
    clearMe,
    fetchUserById,
    updateUser,
    softDeleteUser,
    hardDeleteUser,
    checkDuplicate,
  };
});
