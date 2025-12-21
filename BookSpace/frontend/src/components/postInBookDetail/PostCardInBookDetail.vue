<template>
  <div
    class="w-full rounded-xl border border-border bg-card p-5 shadow-sm transition hover:shadow-md cursor-pointer"
    :data-post-id="post.postId"
    @click="goToPostDetail"
  >
    <!-- 제목 -->
    <h2 class="text-lg font-semibold text-foreground">
      {{ post.postTitle }}
    </h2>

    <!-- 작성자 + 작성일 -->
    <p class="mt-1 text-sm text-muted-foreground">
      {{ post.userNickName }}
      <span class="mx-1">·</span>
      {{ formattedDate }}
    </p>

    <!-- 내용 -->
    <p class="mt-4 text-sm text-foreground/90 line-clamp-2 whitespace-pre-line">
      {{ post.postContent }}
    </p>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
});

const router = useRouter();

// 작성일자 포맷
const formattedDate = computed(() => {
  if (!props.post.postDate) return "";
  const date = new Date(props.post.postDate);
  return date.toLocaleDateString("ko-KR");
});

const goToPostDetail = () => {
  router.push({
    name: "postDetail",
    params: { postId: props.post.postId },
  });
};
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
