<!-- <script setup>
import HelloWorld from "./components/HelloWorld.vue";
</script>

<template>
  <div>
    <a href="https://vite.dev" target="_blank">
      <img src="/vite.svg" class="logo" alt="Vite logo" />
    </a>
    <a href="https://vuejs.org/" target="_blank">
      <img src="./assets/vue.svg" class="logo vue" alt="Vue logo" />
    </a>
  </div>
  <HelloWorld msg="Vite + Vue" />
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style> -->

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
