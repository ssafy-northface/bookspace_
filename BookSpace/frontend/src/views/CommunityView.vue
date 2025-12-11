<template>
  <ViewHeader
    title="커뮤니티"
    description="커뮤니티 게시판 목록을 확인하세요."
    align="left"
    size="lg"
  />

  <!-- 검색인풋 + 게시글 작성 버튼 -->
  <section class="flex items-center justify-between gap-4 mt-6 mb-4">
    <SearchInput placeholder="게시판 검색..." />

    <Button><PlusIcon class="w-5 h-5 text-white" /> 게시글 작성</Button>
  </section>
</template>

<script setup>
import ViewHeader from "../components/common/ViewHeader.vue";
import SearchInput from "../components/common/SearchInput.vue";
import Button from "../components/ui/Button.vue";
import { PlusIcon } from "lucide-vue-next";
</script>

<style scoped></style>
