<template>
  <ViewHeader
    :title="headerTitle"
    :description="headerDescription"
    align="left"
    size="lg"
  />

  <section
    class="max-w-4xl p-6 mt-6 space-y-6 border shadow-sm rounded-xl bg-card border-border"
  >
    <form class="space-y-6" @submit.prevent="handleSubmit">
      <!-- 책 선택 -->
      <div class="space-y-3">
        <div class="flex items-center justify-end">
          <!-- 작성 모드에서만 다시 선택 가능 -->
          <Button
            v-if="
              !isEditMode &&
              selectedBook &&
              selectedBook.isbn &&
              !showBookSelector
            "
            type="button"
            variant="ghost"
            size="sm"
            @click="showBookSelector = true"
          >
            다시 선택
          </Button>
        </div>

        <!-- 수정 모드: 책 변경 불가 -->
        <template v-if="isEditMode">
          <PostBookInfo
            v-if="selectedBook"
            :title="selectedBook.title"
            :author="selectedBook.author"
            :isbn="selectedBook.isbn"
            :image-url="selectedBook.imageUrl"
          />
        </template>

        <!-- 작성 모드 -->
        <template v-else>
          <PostBookSelector
            v-if="showBookSelector"
            v-model:isbn="isbn"
            @select="onBookSelect"
          />

          <div v-if="selectedBook && !showBookSelector" class="pt-1">
            <PostBookInfo
              :title="selectedBook.title"
              :author="selectedBook.author"
              :isbn="selectedBook.isbn"
              :image-url="selectedBook.imageUrl"
            />
          </div>
        </template>
      </div>

      <!-- 제목 -->
      <div class="space-y-2">
        <label class="text-sm font-semibold text-foreground">제목</label>
        <Input
          v-model="postTitle"
          type="text"
          placeholder="게시글 제목을 입력하세요"
          required
        />
      </div>

      <!-- 내용 -->
      <div class="space-y-2">
        <label class="text-sm font-semibold text-foreground">내용</label>
        <Textarea
          v-model="postContent"
          rows="10"
          placeholder="책에 대한 생각이나 질문을 자유롭게 작성하세요"
          required
        />
      </div>

      <!-- 버튼 -->
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
            :disabled="!isFormValid || isSubmitting || isEditLoading"
            :loading="isSubmitting"
          >
            {{ submitLabel }}
          </Button>
        </div>
      </div>
    </form>
  </section>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import ViewHeader from "@/components/common/ViewHeader.vue";
import Button from "@/components/ui/Button.vue";
import Input from "@/components/ui/Input.vue";
import Textarea from "@/components/ui/Textarea.vue";
import PostBookSelector from "@/components/community/PostBookSelector.vue";
import PostBookInfo from "@/components/community/PostBookInfo.vue";
import { createPostApi, fetchPostDetail, updatePostApi } from "@/api/postApi";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import { useToast } from "@/composables/useToast";

const router = useRouter();
const route = useRoute();
const queryClient = useQueryClient();
const { toast } = useToast();

const isEditMode = computed(() => route.query.mode === "edit");
const postId = computed(() => route.query.postId);
const preloadedPost =
  route.state?.post &&
  postId.value &&
  String(route.state.post.postId) === String(postId.value)
    ? route.state.post
    : null;

const isbn = ref("");
const postTitle = ref("");
const postContent = ref("");
const errorMessage = ref("");
const isSubmitting = ref(false);

// 수정 모드 - 수정 여부 체크
const originalTitle = ref("");
const originalContent = ref("");

const selectedBook = ref(null);
const showBookSelector = ref(true);

/**
 * 수정 모드
 * - 게시물 제목, 내용이 바뀌었는지 체크
 */
const isPostChanged = computed(() => {
  if (!isEditMode.value) return true;

  const trimmedTitle = postTitle.value.trim();
  const trimmedContent = postContent.value.trim();

  return (
    trimmedTitle !== (originalTitle.value || "").trim() ||
    trimmedContent !== (originalContent.value || "").trim()
  );
});

/**
 * 작성 모드
 * - 필수 값 체크
 */
const hasRequiredFieldsForCreate = computed(() => {
  return (
    !!isbn.value.trim() &&
    !!postTitle.value.trim() &&
    !!postContent.value.trim()
  );
});

/*
 * 버튼 활성화 조건
 * - 작성 모드: 필수값 필요
 * - 수정 모드: 제목 or 내용 변경 시에만 활성화
 *  */
const isFormValid = computed(() => {
  if (isEditMode.value) {
    return isPostChanged.value;
  }
  return hasRequiredFieldsForCreate.value;
});

const headerTitle = computed(() =>
  isEditMode.value ? "게시글 수정" : "게시글 작성"
);
const headerDescription = computed(() =>
  isEditMode.value
    ? "게시글을 수정할 수 있습니다."
    : "책에 대한 소감을 남겨보세요."
);
const submitLabel = computed(() =>
  isEditMode.value ? "수정 완료" : "게시글 등록"
);

// 수정 데이터 로드
const { data: detailData, isLoading: isEditLoading } = useQuery({
  queryKey: ["post", postId.value],
  queryFn: () => fetchPostDetail(postId.value),
  enabled: computed(() => isEditMode.value && !!postId.value),
  initialData: () => (isEditMode.value ? preloadedPost : undefined),
  staleTime: 0,
});

watch(
  () => detailData.value,
  (val) => {
    if (!isEditMode.value || !val) return;

    isbn.value = val.isbn || "";
    postTitle.value = val.postTitle || "";
    postContent.value = val.postContent || "";

    originalTitle.value = val.postTitle || "";
    originalContent.value = val.postContent || "";

    selectedBook.value = {
      title: val.bookTitle || "",
      author: val.bookAuthor || "",
      isbn: val.isbn || "",
      imageUrl: val.bookImageUrl || "",
    };

    showBookSelector.value = false;
  },
  { immediate: true }
);

/**
 * 게시글 생성
 */
const createPostMutation = useMutation({
  mutationFn: (payload) => createPostApi(payload),
  onSuccess: async () => {
    await queryClient.invalidateQueries({ queryKey: ["posts"] });
    await queryClient.invalidateQueries({ queryKey: ["posts", "latest"] });
    toast({
      title: "등록 완료",
      description: "게시글이 등록되었습니다.",
    });
    router.push({ name: "community" });
  },
  onError: (error) => {
    const msg =
      error?.response?.data?.message ||
      "게시글 등록에 실패했습니다. 잠시 후 다시 시도해주세요.";
    toast({
      title: "등록 실패",
      description: msg,
      variant: "destructive",
    });
  },
});

/**
 * 게시글 수정
 */
const updatePostMutation = useMutation({
  mutationFn: (payload) => updatePostApi(postId.value, payload),
  onSuccess: async () => {
    await queryClient.invalidateQueries({ queryKey: ["posts"] });
    await queryClient.invalidateQueries({ queryKey: ["posts", "latest"] });
    await queryClient.invalidateQueries({ queryKey: ["post", postId.value] });
    toast({
      title: "수정 완료",
      description: "게시글이 수정되었습니다.",
    });
    router.push({ name: "postDetail", params: { postId: postId.value } });
  },
  onError: (error) => {
    const msg =
      error?.response?.data?.message ||
      "게시글 수정에 실패했습니다. 잠시 후 다시 시도해주세요.";
    toast({
      title: "수정 실패",
      description: msg,
      variant: "destructive",
    });
  },
});

/**
 * 버튼 클릭
 */
const handleSubmit = async () => {
  errorMessage.value = "";

  const trimmedTitle = postTitle.value.trim();
  const trimmedContent = postContent.value.trim();

  isSubmitting.value = true;
  try {
    if (isEditMode.value) {
      await updatePostMutation.mutateAsync({
        isbn: isbn.value, // 수정 시 기존 isbn 유지
        postTitle: trimmedTitle,
        postContent: trimmedContent,
      });
    } else {
      await createPostMutation.mutateAsync({
        isbn: isbn.value.trim(),
        postTitle: trimmedTitle,
        postContent: trimmedContent,
      });
    }
  } finally {
    isSubmitting.value = false;
  }
};

/**
 * 책 선택
 */
const onBookSelect = (book) => {
  isbn.value = book?.isbn13 ?? "";
  selectedBook.value = {
    title: book?.title || "",
    author: book?.author || "",
    isbn: isbn.value,
    imageUrl: book?.cover || "",
  };
  showBookSelector.value = false;
};

watch(
  isEditMode,
  (val) => {
    if (val) showBookSelector.value = false;
  },
  { immediate: true }
);
</script>
