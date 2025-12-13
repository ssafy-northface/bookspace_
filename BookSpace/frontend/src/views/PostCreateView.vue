<!-- TODO 검색 기능 -->
<!--TODO 스타일 수정  -->
<template>
  <ViewHeader
    title="게시글 작성"
    description="책에 대한 소감을 남겨보세요."
    align="left"
    size="lg"
  />

  <section
    class="max-w-4xl p-6 mt-6 space-y-6 border shadow-sm rounded-xl bg-card border-border"
  >
    <form class="space-y-6" @submit.prevent="handleSubmit">
      <div class="space-y-2">
        <div class="flex items-center justify-between">
          <label class="text-sm font-semibold text-foreground"> 책 선택 </label>
          <!-- <span class="text-xs text-muted-foreground"> BookSpace </span> -->
        </div>
        <SearchInput
          v-model="isbn"
          placeholder="책 제목 또는 ISBN을 검색하세요"
          @search="onSearch"
        />
      </div>

      <div class="space-y-2">
        <label class="text-sm font-semibold text-foreground">제목</label>
        <Input
          v-model="postTitle"
          type="text"
          placeholder="게시글 제목을 입력하세요"
          required
        />
      </div>

      <div class="space-y-2">
        <label class="text-sm font-semibold text-foreground">내용</label>
        <Textarea
          v-model="postContent"
          rows="10"
          :class="textareaClasses"
          placeholder="책에 대한 생각이나 질문을 자유롭게 작성하세요"
          required
        />
      </div>

      <div class="flex items-center justify-between gap-3 pt-2">
        <p v-if="errorMessage" class="text-sm text-destructive">
          {{ errorMessage }}
        </p>
        <div class="flex items-center gap-2 ml-auto">
          <Button
            type="button"
            variant="outline"
            @click="router.back()"
            :disabled="isSubmitting"
          >
            취소
          </Button>
          <Button
            type="submit"
            :disabled="!isFormValid || isSubmitting"
            :loading="isSubmitting"
          >
            게시글 등록
          </Button>
        </div>
      </div>
    </form>
  </section>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import ViewHeader from "@/components/common/ViewHeader.vue";
import Button from "@/components/ui/Button.vue";
import SearchInput from "../components/common/SearchInput.vue";
import Input from "@/components/ui/Input.vue";
import Textarea from "@/components/ui/Textarea.vue";
import { createPostApi } from "@/api/postApi";
import { useMutation, useQueryClient } from "@tanstack/vue-query";

const router = useRouter();
const queryClient = useQueryClient();

const isbn = ref("");
const postTitle = ref("");
const postContent = ref("");
const errorMessage = ref("");
const isSubmitting = ref(false);

const textareaClasses =
  "w-full rounded-md border border-[color:var(--border)] bg-[color:var(--background)] " +
  "text-[color:var(--foreground)] px-3 py-2 text-sm shadow-xs transition outline-none " +
  "placeholder:text-[color:var(--muted-foreground)] " +
  "focus-visible:ring-[3px] focus-visible:ring-[color:var(--ring)]/50 focus-visible:border-[color:var(--ring)] min-h-[220px] resize-y leading-relaxed";

const isFormValid = computed(() => {
  return (
    !!postTitle.value.trim() &&
    !!postContent.value.trim() &&
    !!isbn.value.trim()
  );
});

const createPostMutation = useMutation({
  mutationFn: (payload) => createPostApi(payload),
  onSuccess: async () => {
    await queryClient.invalidateQueries({ queryKey: ["posts"] });
    await queryClient.invalidateQueries({ queryKey: ["posts", "latest"] });
    router.push({ name: "community" });
  },
  onError: (error) => {
    errorMessage.value =
      error?.response?.data?.message ||
      "게시글 등록에 실패했습니다. 잠시 후 다시 시도해주세요.";
  },
});

const handleSubmit = async () => {
  errorMessage.value = "";

  const trimmedIsbn = isbn.value.trim();
  const trimmedTitle = postTitle.value.trim();
  const trimmedContent = postContent.value.trim();

  if (!trimmedIsbn) {
    errorMessage.value = "ISBN을 입력해주세요.";
    return;
  }
  if (!trimmedTitle || !trimmedContent) {
    errorMessage.value = "제목과 내용을 입력해주세요.";
    return;
  }

  isSubmitting.value = true;
  try {
    await createPostMutation.mutateAsync({
      isbn: trimmedIsbn,
      postTitle: trimmedTitle,
      postContent: trimmedContent,
    });
  } finally {
    isSubmitting.value = false;
  }
};
</script>
