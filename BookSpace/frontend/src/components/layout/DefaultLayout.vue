// 상단 헤더 + 메인 컨테이너 + 사이드바 + 푸터...
<script setup>
import AppHeader from "@/components/common/AppHeader.vue";
import AppFooter from "@/components/common/AppFooter.vue";
</script>
<template>
  <AppHeader />
  <main class="min-h-screen">
    <RouterView></RouterView>
  </main>
  <AppFooter />
</template>
