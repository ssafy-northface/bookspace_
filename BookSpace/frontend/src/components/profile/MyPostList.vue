<template>
  <div class="space-y-4">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="py-8 text-center text-muted-foreground">
      내가 쓴 게시글을 불러오는 중입니다...
    </div>

    <!-- 작성 게시글 없음 -->
    <div
      v-else-if="!posts.length"
      class="flex flex-col items-center justify-center gap-3 px-6 py-10 text-center border border-dashed rounded-xl border-border bg-muted/30 text-muted-foreground"
    >
      <p class="text-base font-semibold text-foreground">
        작성한 게시글이 없어요
      </p>
      <p class="text-sm">관심 있는 책으로 토론을 시작해보세요.</p>
    </div>

    <!-- 작성 게시글 목록 -->
    <div v-else class="grid grid-cols-1 gap-4">
      <PostCard
        v-for="post in posts"
        :key="post.postId"
        :post="post"
        disable-navigation
        @open="emit('open-post', post)"
      />
    </div>
  </div>
</template>

<script setup>
import PostCard from "@/components/community/PostCard.vue";

const emit = defineEmits(["open-post"]);
defineProps({
  posts: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const formatDate = (value) => {
  if (!value) return "날짜 정보 없음";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "날짜 정보 없음";
  return date.toLocaleDateString("ko-KR");
};
</script>
