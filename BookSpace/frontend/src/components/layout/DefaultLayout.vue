// 상단 헤더 + 메인 컨테이너 + 사이드바 + 푸터...
<script setup>
import { useRoute } from "vue-router";
import AppHeader from "@/components/common/AppHeader.vue";
import AppFooter from "@/components/common/AppFooter.vue";
import AiFloatingButton from "@/components/common/AiFloatingButton.vue";
import ScrollTopButton from "@/components/common/ScrollTopButton.vue";

const route = useRoute();
</script>
<template>
  <AppHeader />
  <main class="container flex-1 py-8">
    <RouterView />
  </main>
  <AppFooter v-if="!$route.meta.noFooter" />

  <!-- 플로팅 버튼들 -->
  <!-- 상단 이동 버튼 (모든 페이지) -->
  <ScrollTopButton />
  
  <!-- AI 추천 플로팅 버튼 (AI 추천 페이지에서는 숨김) -->
  <AiFloatingButton v-if="route.name !== 'aiRecommend'" />
</template>
