<template>
  <section class="space-y-6">
    <!-- 헤더 -->
    <div class="flex items-center justify-between">
      <h2 class="text-lg font-semibold text-foreground">토론 게시글</h2>

      <!-- 스샷 느낌: + 아이콘 + 게시글 작성 -->
      <Button class="h-9 px-4 text-white font-extrabold inline-flex items-center gap-2" @click="openCreateModal">
        <PlusIcon class="w-4 h-4" />
        게시글 작성
      </Button>
    </div>

    <!-- 리스트 -->
    <PostList />

    <!-- 작성 모달 -->
    <PostCreateModal v-if="isModalOpen" @close="isModalOpen = false" @created="handleCreated" />
  </section>
</template>

<script setup>
import { ref } from "vue";
import Button from "@/components/ui/Button.vue";
import { PlusIcon } from "lucide-vue-next";

import PostCreateModal from "./PostCreateModal.vue";

const isModalOpen = ref(false);

const openCreateModal = () => {
  isCreateOpen.value = true;
};

// (선택) 작성 완료 후 모달 닫기 + 리스트 갱신 트리거 연결용
const handleCreated = () => {
  isCreateOpen.value = false;
  // 여기서 refetch() 하거나, 상위에 emit해서 다시 불러오게 연결 가능
};
</script>
