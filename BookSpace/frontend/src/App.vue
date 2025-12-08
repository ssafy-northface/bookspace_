<script setup>
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";

</script>
<template>
  <div class="min-h-screen flex flex-col">
    <AppHeader />

    <main class="flex-1">
      <RouterView />
    </main>

    <AppFooter />
  </div>
</template>


<!-- src/App.vue -->
<template>
  <div>
    <!-- 공통 헤더 -->
    <header>
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/books">도서</RouterLink>
        <RouterLink to="/community">커뮤니티</RouterLink>
        <RouterLink to="/ai-recommend">AI 추천</RouterLink>
        <RouterLink to="/profile">마이페이지</RouterLink>
        <RouterLink to="/login">로그인</RouterLink>
        <RouterLink to="/signup">회원가입</RouterLink>
      </nav>
    </header>

    <!-- 여기 안에 현재 선택된 페이지가 렌더링됨 -->
    <main>
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style scoped>
/* 대충 스타일 */
nav {
  display: flex;
  gap: 12px;
}
</style>
