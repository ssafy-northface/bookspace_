<script setup>
import { BookOpen, Search, User, Menu } from "lucide-vue-next";
import { RouterLink } from "vue-router";
// Heroicons import
import {
  MagnifyingGlassIcon,
  UserIcon,
  Bars3Icon,
} from "@heroicons/vue/24/outline";
</script>
<template>
  <header
    class="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60"
  >
    <div class="container flex h-16 items-center justify-between">
      <!-- 왼쪽 로고 + 내비게이션 -->
      <div class="flex items-center gap-8">
        <RouterLink to="/" class="flex items-center gap-2">
          <!-- 아이콘 제거 → Bookspace 텍스트만 -->
          <span class="text-xl font-semibold">Bookspace</span>
        </RouterLink>

        <!--네브바 -->
        <nav class="hidden md:flex items-center gap-6">
          <RouterLink
            to="/books"
            class="text-sm font-medium hover:text-primary transition-colors"
          >
            도서 목록
          </RouterLink>
          <RouterLink
            to="/community"
            class="text-sm font-medium hover:text-primary transition-colors"
          >
            커뮤니티
          </RouterLink>
          <RouterLink
            to="/ai-recommend"
            class="text-sm font-medium hover:text-primary transition-colors"
          >
            AI 추천
          </RouterLink>
        </nav>
      </div>
      <!-- 오른쪽 버튼들 -->
      <div class="flex items-center gap-4">
        <!-- 검색 아이콘 -->
        <RouterLink
          to="/search"
          class="hidden md:flex items-center hover:text-blue-600 transition"
        >
          <MagnifyingGlassIcon class="w-5 h-5" />
        </RouterLink>

        <!-- 프로필 텍스트 -->
        <RouterLink
          to="/profile"
          class="text-sm font-medium hover:text-blue-600 transition-colors"
        >
          <UserIcon class="w-5 h-5" />
        </RouterLink>

        <!-- 로그인 버튼 -->
        <RouterLink
          to="/login"
          class="hidden md:flex text-sm font-medium border px-3 py-1.5 rounded-md hover:bg-accent transition"
        >
          로그인
        </RouterLink>

        <!-- 모바일 메뉴 버튼 -->
        <button class="md:hidden text-sm font-medium hover:text-primary">
          <Bars3Icon class="w-5 h-5" />
        </button>
      </div>
    </div>
  </header>
</template>
