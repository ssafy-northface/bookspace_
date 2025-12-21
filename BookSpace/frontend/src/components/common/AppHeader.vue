<template>
  <header
    class="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur"
  >
    <div class="container flex items-center justify-between h-16">
      <!-- 왼쪽 로고 + 내비게이션 -->
      <div class="flex items-center gap-8">
        <RouterLink to="/" class="flex items-center gap-2">
          <!-- 아이콘 제거 → Bookspace 텍스트만 -->
          <span class="text-xl font-semibold">Bookspace</span>
        </RouterLink>

        <!--네브바 -->
        <nav class="items-center hidden gap-6 md:flex">
          <RouterLink
            to="/books"
            class="text-sm font-medium transition-colors hover:text-primary"
          >
            도서 목록
          </RouterLink>
          <RouterLink
            to="/community"
            class="text-sm font-medium transition-colors hover:text-primary"
          >
            커뮤니티
          </RouterLink>
          <RouterLink
            to="/ai-recommend"
            class="text-sm font-medium transition-colors hover:text-primary"
          >
            AI 추천
          </RouterLink>
        </nav>
      </div>
      <!-- 오른쪽 버튼들 -->
      <div class="flex items-center gap-4">
        <!-- 로그인 상태 -->
        <template v-if="isLoggedIn">
          <!--닉네임 -->
          <span>{{ displayName }}님</span>

          <!-- profile (mypage) -->
          <RouterLink
            to="/profile"
            class="items-center hidden transition md:flex hover:text-blue-600"
          >
            <UserCircleIcon class="w-5 h-5" />
          </RouterLink>

          <!-- 로그아웃 버튼 -->
          <button
            type="button"
            class="items-center hidden transition md:flex hover:text-blue-600"
            @click="handleLogout"
          >
            <PowerIcon class="w-5 h-5" />
          </button>
        </template>

        <!-- 비로그인 상태 -->
        <template v-else>
          <!-- 프로필 아이콘: 로그인 페이지로 이동 -->
          <RouterLink
            to="/signup"
            class="hidden md:flex text-sm font-medium border px-4 py-1.5 rounded-md hover:bg-accent transition"
          >
            회원가입
          </RouterLink>

          <!-- 로그인 버튼 -->
          <RouterLink
            to="/signin"
            class="hidden md:flex text-sm font-medium border px-4 py-1.5 rounded-md hover:bg-accent transition"
          >
            로그인
          </RouterLink>
        </template>

        <!-- 모바일 메뉴 버튼 (햄버거) -->
        <button class="text-sm font-medium md:hidden hover:text-primary">
          <Bars3Icon class="w-5 h-5" />
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter, RouterLink } from "vue-router";
import {
  UserCircleIcon,
  Bars3Icon, // 햄버거메뉴
  PowerIcon, //로그아웃
} from "@heroicons/vue/24/outline";
import { computed } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const userStore = useUserStore();
const router = useRouter();

const { me } = storeToRefs(userStore);

const isLoggedIn = computed(() => authStore.isLoggedIn);

const displayName = computed(() => {
  if (!me.value) return "";
  return me.value.userNickname || me.Value.userName || "";
});

const handleLogout = () => {
  authStore.logout();
  router.push("/");
};
</script>
