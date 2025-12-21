<template>
  <div class="space-y-4">
    <!-- 로딩(초기) -->
    <div
      v-if="loading && !posts.length"
      class="rounded-xl border border-border bg-card p-6 text-sm text-muted-foreground"
    >
      게시글을 불러오는 중...
    </div>

    <!-- 에러 -->
    <div
      v-else-if="error"
      class="rounded-xl border border-destructive/30 bg-card p-6 text-sm"
    >
      <p class="font-medium text-foreground">게시글을 불러오지 못했어요.</p>
      <p class="mt-1 text-muted-foreground">잠시 후 다시 시도해주세요.</p>

      <button
        v-if="showRetry"
        type="button"
        class="mt-4 h-9 rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground hover:bg-primary/90"
        @click="$emit('retry')"
      >
        다시 시도
      </button>
    </div>

    <!-- 게시글이 없을 때 -->
    <div
      v-else-if="!posts.length"
      class="flex min-h-[160px] flex-col items-center justify-center rounded-xl border border-dashed border-border bg-card p-6 text-sm text-muted-foreground"
    >
      아직 게시글이 없어요.
      <span class="mt-1">첫 번째 게시글을 작성해보세요!</span>
    </div>

    <!-- list -->
    <div v-else class="space-y-4">
      <PostCardInBookDetail
        v-for="post in posts"
        :key="post.postId ?? post.id"
        :post="post"
      />
    </div>

    <!-- 더보기 -->
    <div v-if="posts.length" class="pt-2">
      <button
        v-if="hasNext"
        type="button"
        class="w-full h-11 rounded-md border border-border bg-background text-sm font-semibold hover:bg-muted transition disabled:opacity-50 disabled:cursor-not-allowed"
        :disabled="isFetchingNext"
        @click="$emit('load-more')"
      >
        {{ isFetchingNext ? "불러오는 중..." : "더 보기" }}
      </button>

      <p v-else class="py-3 text-center text-xs text-muted-foreground">
        마지막 게시글까지 확인했어요.
      </p>
    </div>
  </div>
</template>

<script setup>
import PostCardInBookDetail from "./PostCardInBookDetail.vue";

defineProps({
  posts: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: Boolean, default: false },
  showRetry: { type: Boolean, default: true },

  // 더보기 관련
  hasNext: { type: Boolean, default: false },
  isFetchingNext: { type: Boolean, default: false },
});

defineEmits(["retry", "load-more"]);
</script>
