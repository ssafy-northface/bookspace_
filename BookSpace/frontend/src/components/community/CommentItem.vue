<template>
  <div class="space-y-3">
    <div class="flex-col items-start gap-3">
      <div class="flex items-start justify-between gap-3 mb-2">
        <div class="flex items-center gap-4">
          <UserProfileImg :name="comment.userNickname" />
          <p class="text-sm font-semibold text-foreground">
            {{ comment.userNickname }}
          </p>
        </div>
        <div class="flex items-center gap-2">
          <KebabMenu
            v-if="isOwner && !isEditing && !isDeleting"
            @edit="startEdit"
            @delete="handleDelete"
          />
          <Spinner v-else-if="isOwner && isDeleting" size="sm" />
        </div>
      </div>

      <div class="flex justify-between gap-3">
        <!-- 댓글 내용 & 날짜 -->
        <div class="flex-1 space-y-2">
          <template v-if="isEditing">
            <Textarea v-model="draft" rows="3" :disabled="isUpdating" />
            <div class="flex justify-end gap-2 text-xs">
              <button
                type="button"
                class="text-muted-foreground hover:underline"
                :disabled="isUpdating"
                @click="cancelEdit"
              >
                취소
              </button>
              <button
                type="button"
                class="text-primary hover:underline"
                :disabled="isUpdating"
                @click="submitEdit"
              >
                <span v-if="isUpdating" class="inline-flex items-center gap-2">
                  <Spinner size="sm" />
                  처리중
                </span>
                <span v-else>수정</span>
              </button>
            </div>
          </template>
          <template v-else>
            <p
              class="text-sm leading-relaxed whitespace-pre-line text-foreground"
            >
              {{ comment.commentContent }}
            </p>
            <p class="text-xs text-muted-foreground">{{ formattedDate }}</p>
          </template>
        </div>
        <!-- 답글 작성 버튼 -->
        <button
          v-if="isParent && !isEditing && isLoggedIn"
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

    <!-- 대댓글 -->
    <div
      v-if="comment.replies?.length"
      class="pl-12 space-y-3 border-l border-border"
    >
      <CommentItem
        v-for="reply in comment.replies"
        :key="reply.commentId"
        :comment="reply"
        :post-id="postId"
        @reply="$emit('reply', $event)"
        @comment-updated="$emit('comment-updated', $event)"
        @comment-deleted="$emit('comment-deleted', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useMutation, useQueryClient } from "@tanstack/vue-query";
import { updateCommentApi, deleteCommentApi } from "@/api/postApi";
import { useAuthStore } from "@/stores/authStore";
import { useUserStore } from "@/stores/userStore";
import { useToast } from "@/composables/useToast";
import UserProfileImg from "@/components/common/UserProfileImg.vue";
import KebabMenu from "@/components/community/KebabMenu.vue";
import Textarea from "@/components/ui/Textarea.vue";
import Spinner from "@/components/ui/Spinner.vue";

defineOptions({ name: "CommentItem" });

// ------------ props & emits  ------------
const props = defineProps({
  comment: {
    type: Object,
    required: true,
  },
  postId: {
    type: [String, Number],
    required: true,
  },
});

const emit = defineEmits(["reply", "comment-updated", "comment-deleted"]);

// ------------ 기본 사용 설정 ------------
const authStore = useAuthStore();
const userStore = useUserStore();
const queryClient = useQueryClient();
const { toast } = useToast();
import { useFormattedDate } from "@/composables/useFormattedDate";
const isLoggedIn = computed(() => authStore.isLoggedIn);

// ------------ owner? ------------
const me = computed(() => userStore.me);
const commentUserId = computed(
  () =>
    props.comment.userId ??
    props.comment.userID ??
    props.comment.user_id ??
    props.comment.authorId
);
const isOwner = computed(() => {
  if (!authStore.isLoggedIn || !me.value) return false;
  if (!commentUserId.value) return false;
  return String(me.value.userId) === String(commentUserId.value);
});

// ------------ edit state ------------
const isEditing = ref(false);
const draft = ref(props.comment.commentContent ?? "");

watch(
  () => props.comment.commentContent,
  (next) => {
    if (!isEditing.value) {
      draft.value = next ?? "";
    }
  }
);

const formattedDate = useFormattedDate(props.comment.commentDate);

const isParent = computed(() => props.comment.parentCommentId === null);

// ------------ Update Mutation ------------
const updateCommentMutation = useMutation({
  mutationFn: (payload) => updateCommentApi(props.comment.commentId, payload),
  onSuccess: () => {
    queryClient.invalidateQueries(["comments", props.postId]);
    isEditing.value = false;
    toast({
      title: "수정 완료",
      description: "댓글이 수정되었습니다.",
    });
    emit("comment-updated", props.comment.commentId);
  },
  onError: (err) => {
    toast({
      title: "댓글 수정 실패",
      description:
        err?.response?.data?.message ||
        "댓글 수정에 실패했습니다. 잠시 후 다시 시도해주세요.",
      variant: "destructive",
    });
  },
});

const isUpdating = computed(() => updateCommentMutation.isPending.value);

// ------------ Delete Mutation ------------
const deleteCommentMutation = useMutation({
  mutationFn: () => deleteCommentApi(props.comment.commentId),
  onSuccess: () => {
    queryClient.invalidateQueries(["comments", props.postId]);
    toast({
      title: "삭제 완료",
      description: "댓글이 삭제되었습니다.",
    });
    emit("comment-deleted", props.comment.commentId);
  },
  onError: (err) => {
    toast({
      title: "댓글 삭제 실패",
      description:
        err?.response?.data?.message ||
        "댓글 삭제에 실패했습니다. 잠시 후 다시 시도해주세요.",
      variant: "destructive",
    });
  },
});

const isDeleting = computed(() => deleteCommentMutation.isPending.value);

// ------------ Handlers ------------
const startEdit = () => {
  draft.value = props.comment.commentContent ?? "";
  isEditing.value = true;
};

const cancelEdit = () => {
  isEditing.value = false;
  draft.value = props.comment.commentContent ?? "";
};

const submitEdit = () => {
  const content = draft.value.trim();
  if (!content) {
    toast({
      title: "댓글 내용을 입력해주세요.",
      description: "수정할 내용을 입력해야 저장할 수 있습니다.",
    });
    return;
  }
  if (content === props.comment.commentContent) {
    isEditing.value = false;
    return;
  }
  updateCommentMutation.mutateAsync({ commentContent: content });
};

const handleDelete = () => {
  toast({
    title: "댓글 삭제",
    description: "댓글을 삭제하고 있습니다...",
    variant: "destructive",
  });
  deleteCommentMutation.mutateAsync();
};
</script>
