<template>
  <section class="p-6 mt-8 space-y-6 border rounded-xl border-border bg-card">
    <!-- 댓글 & 댓글 개수 -->
    <div class="flex items-center justify-between">
      <h3 class="text-lg font-semibold text-foreground">
        댓글 {{ commentCount }}
      </h3>
    </div>

    <!-- 로그인 유저 댓글 작성 TextArea -->
    <div v-if="authStore.isLoggedIn" class="space-y-3">
      <label class="text-sm font-semibold text-foreground">댓글 작성</label>
      <Textarea
        v-model="commentText"
        rows="3"
        placeholder="댓글을 입력하세요"
      />
      <div class="flex justify-end gap-2">
        <Button
          size="lg"
          :disabled="isSubmitting || !commentText.trim()"
          @click="submitComment"
        >
          등록
        </Button>
      </div>
      <!-- mutation 에러 -->
      <p v-if="errorMessage" class="text-sm text-destructive">
        {{ errorMessage }}
      </p>
    </div>

    <!-- 비로그인 유저 UI (로그인 페이지 로그인 후 -> 해당 게시물 상세 조회 페이지로 이동) -->
    <div
      v-else
      class="p-4 text-sm border rounded-lg bg-muted/40 border-border text-muted-foreground"
    >
      <p class="mb-2">댓글 작성은 로그인 후 이용할 수 있습니다.</p>

      <!-- 로그인 페이지 이동 링크 -->
      <button
        type="button"
        class="font-medium text-primary hover:underline"
        @click="ensureAuth"
      >
        로그인 하러 가기 →
      </button>
    </div>

    <!-- 댓글 조회 -->
    <div v-if="isLoading" class="py-4 text-sm text-muted-foreground">
      댓글을 불러오는 중입니다...
    </div>
    <div v-else-if="isError" class="py-4 text-sm text-destructive">
      댓글을 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요.
    </div>
    <div v-else-if="comments?.length" class="space-y-5">
      <CommentItem
        v-for="comment in comments"
        :key="comment.commentId"
        :comment="comment"
        :post-id="postId"
        @reply="activateReply"
        @comment-updated="handleCommentUpdated"
        @comment-deleted="handleCommentDeleted"
      >
        <!-- 답글 -->
        <template
          #replyArea
          v-if="authStore.isLoggedIn && replyTargetId === comment.commentId"
        >
          <div
            class="p-4 mt-2 space-y-3 border rounded-lg bg-muted/40 border-border"
          >
            <div
              class="flex items-center justify-between text-sm text-muted-foreground"
            >
              <span>답글 작성</span>
            </div>
            <Textarea
              v-model="replyText"
              rows="3"
              placeholder="답글을 입력하세요"
            />
            <div class="flex justify-end gap-3">
              <button
                class="text-xs text-primary hover:underline"
                variant="outline"
                size="sm"
                @click="cancelReply"
                :disabled="isSubmitting"
              >
                취소
              </button>
              <button
                class="text-xs text-primary hover:underline"
                size="sm"
                :disabled="isSubmitting || !replyText.trim()"
                @click="submitReply"
              >
                등록
              </button>
            </div>
          </div>
        </template>
      </CommentItem>
    </div>
    <div v-else class="py-4 text-sm text-muted-foreground">
      아직 댓글이 없습니다. 첫 댓글을 남겨보세요.
    </div>
  </section>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import Button from "@/components/ui/Button.vue";
import Textarea from "@/components/ui/Textarea.vue";
import CommentItem from "./CommentItem.vue";
import { createCommentApi, fetchCommentsApi } from "@/api/postApi";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "@/composables/useToast";

// ------------ props & emits ------------
const props = defineProps({
  postId: {
    type: [String, Number],
    required: true,
  },
});

// comment Section -> post detail view : 게시글 상세 조회 조회 수 없데이트 emit
const emit = defineEmits(["comment-changed"]);

// ------------ basic setup ------------
const authStore = useAuthStore();
const router = useRouter();
const queryClient = useQueryClient();
const { toast } = useToast();

// ------------ local ui state ------------
const commentText = ref("");
const replyText = ref("");
const replyTargetId = ref(null);

// ------------ comment 조회 ------------
const {
  data: commentData,
  isLoading,
  isError,
  refetch,
} = useQuery({
  queryKey: ["comments", props.postId],
  queryFn: () => fetchCommentsApi(props.postId),
});

const comments = computed(() => commentData.value || []);

// ------------ comment 개수 계산 ------------
const commentCount = computed(() => {
  const countReplies = (items = []) =>
    items.reduce((acc, cur) => acc + 1 + countReplies(cur.replies || []), 0);
  return countReplies(comments.value);
});

// ------------ auth helper ------------
// 비로그인 유저 로그인 뷰 이동 & 로그인 -> 해당 포스트 게시물 상세보기 부로 이동
const ensureAuth = () => {
  if (authStore.isLoggedIn) return true;
  const redirectPath = router.resolve({
    name: "postDetail",
    params: { postId: props.postId },
  }).path;
  router.push({
    name: "signin",
    query: { redirect: redirectPath },
  });
  toast({
    title: "로그인이 필요한 서비스입니다.",
    description: "댓글을 작성하려면 로그인해주세요.",
  });
  return false;
};

// ------------ 댓글 작성 Mutation ------------
const createCommentMutation = useMutation({
  mutationFn: (payload) => createCommentApi(props.postId, payload),
  onSuccess: () => {
    queryClient.invalidateQueries(["comments", props.postId]);
    emit("comment-changed", { type: "create" });
    // ui 초기화
    commentText.value = "";
    replyText.value = "";
    replyTargetId.value = null;
    errorMessage.value = "";
  },
  onError: (err) => {
    errorMessage.value =
      err?.response?.data?.message ||
      "댓글 등록에 실패했습니다. 잠시 후 다시 시도해주세요.";
  },
});

// ------------ mutation 관련 상태 ------------
const isSubmitting = computed(() => createCommentMutation.isPending.value);

const errorMessage = computed(
  () => createCommentMutation.error?.response?.data?.message
);

// ------------ handlers ------------

const submitComment = async () => {
  if (!ensureAuth()) return;
  const content = commentText.value.trim();
  if (!content) return;
  createCommentMutation.mutate({
    commentContent: content,
  });
};

const submitReply = async () => {
  if (!ensureAuth()) return;
  const content = replyText.value.trim();
  if (!content || !replyTargetId.value) return;
  createCommentMutation.mutate({
    commentContent: content,
    parentCommentId: replyTargetId.value,
  });
};

const activateReply = (commentId) => {
  if (!ensureAuth()) return;
  replyTargetId.value = commentId;
  replyText.value = "";
  errorMessage.value = "";
};

const cancelReply = () => {
  replyTargetId.value = null;
  replyText.value = "";
};

const handleCommentDeleted = async () => {
  emit("comment-changed", { type: "delete" });
  queryClient.invalidateQueries(["comments", props.postId]);
};
</script>
