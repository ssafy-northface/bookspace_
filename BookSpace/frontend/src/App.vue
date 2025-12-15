<!-- 
App.vue
로그인 상태 복구 + 내 정보 불러오기 
-->
<template>
  <div class="flex flex-col min-h-screen">
    <RouterView />
    <Toaster />
  </div>
</template>

<script setup>
import Toaster from "./components/ui/Toaster.vue";
import { onMounted } from "vue";
import { useAuthStore } from "./stores/authStore";
import { useUserStore } from "./stores/userStore";

// pinina 스토어 사용
const authStore = useAuthStore();
const userStore = useUserStore();

// 앱이 처음 로드되거나 새로고침됐을 떄 한 번 실행
onMounted(async () => {
  // 인증 상태 복구 (localStorage -> pinia)
  authStore.restoreAuth();

  // 로그인 상태가 아니면 아무것도 안 함
  if (!authStore.isLoggedIn) {
    return;
  }

  // 이미 me가 채워져 있으면 다시 호출할 필요 없음
  if (userStore.me && userStore.me.value) {
    return;
  }

  // 3) 로그인 상태 + me 비어 있으면 -> /auth/me 호출해서 유저 정보 채우기
  try {
    await userStore.fetchMyInfo();
  } catch (e) {
    // 토큰 만료 등으로 /auth/me 실패하면 강제 로그아웃
    authStore.logout();
  }
});
</script>
