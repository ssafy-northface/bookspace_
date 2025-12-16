<template>
  <div class="space-y-3">
    <div class="flex-col items-start gap-3">
      <div class="flex items-center gap-4 mb-2">
        <UserProfileImg :name="comment.userNickname" />
        <p class="text-sm font-semibold text-foreground">
          {{ comment.userNickname }}
        </p>
      </div>

      <div class="flex justify-between space-y-2">
        <!-- 댓글 내용 & 날짜 -->
        <div>
          <p
            class="text-sm leading-relaxed whitespace-pre-line text-foreground"
          >
            {{ comment.commentContent }}
          </p>
          <p class="text-xs text-muted-foreground">{{ formattedDate }}</p>
        </div>
        <!-- 답글 작성 버튼 -->
        <button
          v-if="isParent"
          class="text-xs text-primary hover:underline"
          type="button"
          @click="$emit('reply', comment.commentId)"
        >
          답글
        </button>
      </div>
      <!-- 답글 작성 Textarea -->
      <slot name="replyArea" />
    </div>

    <div
      v-if="comment.replies?.length"
      class="pl-12 space-y-3 border-l border-border"
    >
      <CommentItem
        v-for="reply in comment.replies"
        :key="reply.commentId"
        :comment="reply"
        @reply="$emit('reply', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import UserProfileImg from "@/components/common/UserProfileImg.vue";

defineOptions({ name: "CommentItem" });

const props = defineProps({
  comment: {
    type: Object,
    required: true,
  },
});

defineEmits(["reply"]);

const formattedDate = computed(() => {
  if (!props.comment.commentDate) return "";
  const date = new Date(props.comment.commentDate);
  return date.toLocaleString("ko-KR", {
    dateStyle: "medium",
    timeStyle: "short",
  });
});

const isParent = computed(() => props.comment.parentCommentId === null);
</script>
