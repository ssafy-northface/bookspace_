<template>
  <section class="space-y-6">
    <!-- 헤더 -->
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-semibold text-foreground">게시글</h2>

      <button
        type="button"
        class="h-9 px-4 rounded-md bg-primary text-primary-foreground font-semibold inline-flex items-center gap-2 hover:bg-primary/90"
        @click.stop="openCreateModal"
      >
        <PlusIcon class="w-4 h-4" />
        게시글 작성
      </button>
    </div>

    <!-- 리스트 -->
    <PostList
      :posts="flatPosts"
      :loading="postsQuery.isLoading.value"
      :error="postsQuery.isError.value"
      :has-next="postsQuery.hasNextPage.value"
      :is-fetching-next="postsQuery.isFetchingNextPage.value"
      @retry="refetch"
      @load-more="fetchNextPage"
    />

    <!-- 작성 모달 -->
    <PostCreateModal
      v-if="isModalOpen"
      @close="isModalOpen = false"
      @submit="handleSubmit"
    />
  </section>
</template>

<script setup>
import { computed, ref } from "vue";
import { PlusIcon } from "lucide-vue-next";
import { useInfiniteQuery, useQueryClient } from "@tanstack/vue-query";

import PostList from "./PostList.vue";
import PostCreateModal from "./PostCreateModal.vue";

import { fetchPosts, createPostApi } from "@/api/postApi";
import { useAuthStore } from "@/stores/authStore";
import { useBookStore } from "@/stores/bookStore";
import { useToast } from "@/composables/useToast";

const emit = defineEmits(["post-updated"]);

const props = defineProps({
  isbn: { type: String, required: true },
  bookId: { type: Number, required: false },
});

const bookStore = useBookStore();
const authStore = useAuthStore();
const queryClient = useQueryClient();
const { toast } = useToast();

const isModalOpen = ref(false);
const sort = ref("latest");

// 도서별 캐시 분리(섞임 방지)
const queryKey = computed(() => ["posts", props.isbn, sort.value]);

const postsQuery = useInfiniteQuery({
  queryKey,
  queryFn: ({ pageParam = 0 }) =>
    fetchPosts({ pageParam, isbn: props.isbn, sort: sort.value }),
  getNextPageParam: (lastPage) =>
    lastPage?.hasNext ? lastPage.nextPage : undefined,
  initialPageParam: 0,
  enabled: computed(() => !!props.isbn),
});

const flatPosts = computed(() => {
  const pages = postsQuery.data.value?.pages ?? [];
  return pages.flatMap((p) => p.posts ?? []);
});

const fetchNextPage = async () => {
  if (!postsQuery.hasNextPage.value) return;
  await postsQuery.fetchNextPage();
};

const refetch = async () => {
  await postsQuery.refetch();
};

const openCreateModal = () => {
  if (!authStore.isLoggedIn) {
    toast({
      title: "로그인 필요",
      description: "로그인 후 이용 가능한 서비스입니다",
      variant: "destructive",
    });
    return;
  }
  isModalOpen.value = true;
};

const handleSubmit = async ({ title, content }) => {
  if (!authStore.isLoggedIn) {
    toast({
      title: "로그인 필요",
      description: "로그인 후 이용 가능한 서비스입니다",
      variant: "destructive",
    });
    return;
  }

  try {
    // 모달에서 게시글 등록 시 isbn/bookId 함께 전달
    await createPostApi({
      isbn: props.isbn,
      bookId: props.bookId,
      postTitle: title,
      postContent: content,
    });
    
    toast({
      title: "등록 완료",
      description: "게시글이 등록되었습니다.",
    });
    isModalOpen.value = false;
  } catch (e) {
    const status = e?.response?.status;
    if (status === 401) {
      toast({
        title: "로그인 필요",
        description: "로그인 후 이용 가능한 서비스입니다",
        variant: "destructive",
      });
    } else {
      toast({
        title: "등록 실패",
        description: e?.response?.data?.message ?? "게시글 등록에 실패했습니다",
        variant: "destructive",
      });
    }
    return;
  }

  // 게시글 목록 갱신
  try {
    await queryClient.invalidateQueries({ queryKey: queryKey.value });
  } catch (e) {
    console.warn("[invalidateQueries failed]", e);
  }

  // BookSideCard postCount 갱신
  try {
    await bookStore.loadBookDetail(props.isbn, { force: true });
  } catch (e) {
    console.warn("[loadBookDetail failed]", e);
  }

  // 부모에게 전달
  emit("post-updated");
};
</script>
