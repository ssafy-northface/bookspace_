<template>
  <footer class="border-t border-border bg-muted/50">
    <div class="container py-12">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <h3 class="font-semibold mb-4">Bookspace</h3>
          <p class="text-sm text-muted-foreground">
            AI 기반 감성 도서 추천 플랫폼
          </p>
        </div>

        <div>
          <h3 class="font-semibold mb-4">서비스</h3>
          <ul class="space-y-2 text-sm">
            <li>
              <RouterLink
                to="/books"
                class="text-muted-foreground hover:text-foreground"
                >도서 목록</RouterLink
              >
            </li>
            <li>
              <RouterLink
                to="/ai-recommend"
                class="text-muted-foreground hover:text-foreground"
                >AI 추천</RouterLink
              >
            </li>
            <li>
              <RouterLink
                to="/community"
                class="text-muted-foreground hover:text-foreground"
                >커뮤니티</RouterLink
              >
            </li>
          </ul>
        </div>

        <div>
          <h3 class="font-semibold mb-4">고객지원</h3>
          <ul class="space-y-2 text-sm">
            <li>
              <RouterLink
                to="#"
                class="text-muted-foreground hover:text-foreground"
                >공지사항</RouterLink
              >
            </li>
            <li>
              <RouterLink
                to="#"
                class="text-muted-foreground hover:text-foreground"
                >자주 묻는 질문</RouterLink
              >
            </li>
            <li>
              <RouterLink
                to="#"
                class="text-muted-foreground hover:text-foreground"
                >문의하기</RouterLink
              >
            </li>
          </ul>
        </div>

        <div>
          <h3 class="font-semibold mb-4">정보</h3>
          <ul class="space-y-2 text-sm">
            <li>
              <RouterLink
                to="#"
                class="text-muted-foreground hover:text-foreground"
                >회사 소개</RouterLink
              >
            </li>
            <li>
              <RouterLink
                to="#"
                class="text-muted-foreground hover:text-foreground"
                >이용약관</RouterLink
              >
            </li>
            <li>
              <RouterLink
                to="#"
                class="text-muted-foreground hover:text-foreground"
                >개인정보처리방침</RouterLink
              >
            </li>
          </ul>
        </div>
      </div>

      <div
        class="mt-8 pt-8 border-t border-border text-center text-sm text-muted-foreground"
      >
        © 2025 Bookspace. All rights reserved.
      </div>
    </div>
  </footer>
</template>

<script setup>
import { RouterLink } from "vue-router";
</script>
