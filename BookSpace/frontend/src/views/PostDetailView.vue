<template>
  <section class="max-w-5xl mx-auto mt-6 space-y-6">
    <div
      v-if="showBackButton"
      class="flex items-center justify-between gap-4"
    >
      <Button variant="ghost" class="gap-2 px-3" @click="goBack">
        <ArrowLeft class="w-4 h-4" />
        목록으로
      </Button>
    </div>

    <div v-if="isLoading && !post" class="py-12 text-center">
      게시글을 불러오는 중입니다...
    </div>

    <div v-else-if="isError && !post" class="py-12 text-center">
      <p class="mb-3 text-destructive">게시글을 불러오지 못했습니다.</p>
      <Button variant="outline" @click="refetch">다시 시도</Button>
    </div>

    <template v-else-if="post">
      <article class="p-6 space-y-5 border shadow-sm rounded-xl bg-card">
        <header class="flex items-start justify-between gap-4">
          <div class="flex items-center gap-3">
            <UserProfileImg
              :src="post.userProfileImage"
              :name="post.userNickName"
              sizeClass="w-12 h-12"
            />
            <div>
              <p class="font-semibold text-foreground">
                {{ post.userNickName }}
              </p>
              <p class="text-sm text-muted-foreground">
                {{ formattedDate }}
              </p>
            </div>
          </div>
          <!-- 로그인한 유저가 게시글 작성 유저일 경우 three-dot 메뉴 -->
          <KebabMenu v-if="isOwner" @edit="editPost" @delete="deletePost" />
        </header>

        <div class="space-y-4">
          <!-- 게시글 제목 -->
          <h1 class="text-2xl font-semibold text-foreground">
            {{ post.postTitle }}
          </h1>

          <!-- 책 정보 -->
          <PostBookInfo
            :title="post.bookTitle"
            :author="post.bookAuthor"
            :isbn="post.isbn"
            :image-url="post.bookImageUrl"
          />
        </div>

        <!-- 게시글 내용 -->
        <p class="leading-relaxed whitespace-pre-line text-foreground">
          {{ content }}
        </p>

        <!-- 좋아요 & 댓굴 & 조회 -->
        <div
          class="flex items-center justify-end w-full gap-3 text-sm flex- text-muted-foreground"
        >
          <button
            type="button"
            class="flex items-center gap-1 px-3 py-1 transition rounded-full group"
            :class="
              isLiked ? 'text-destructive bg-destructive/10' : 'hover:bg-muted'
            "
            :disabled="toggleLikeMutation.isPending.value"
            @click="handleToggleLike"
          >
            <HeartIcon
              class="w-4 h-4 transition-colors"
              :class="
                isLiked
                  ? 'text-destructive'
                  : 'text-muted-foreground group-hover:text-destructive'
              "
            />
            <span :class="isLiked ? 'font-semibold text-foreground' : ''">
              {{ likeCount }}개의 좋아요
            </span>
          </button>
          <span class="text-muted-foreground">|</span>
          <div class="flex items-center gap-1">
            <MessageSquare class="w-4 h-4" />
            <span>{{ post.commentCount ?? 0 }}개의 댓글</span>
          </div>
          <span class="text-muted-foreground">|</span>
          <div class="flex items-center gap-1">
            <Eye class="w-4 h-4" />
            <span>{{ post.postViewCnt ?? 0 }}회 조회</span>
          </div>
        </div>
      </article>

      <!-- 댓글 섹션 -->
      <CommentsSection
        v-if="post"
        :post-id="post.postId"
        @comment-changed="handleCommentChanged"
      />

      <div v-if="isError" class="flex items-center gap-3 text-sm">
        <span class="text-destructive"
          >최신 정보를 불러오는 데 실패했습니다.</span
        >
        <Button variant="ghost" size="sm" @click="refetch">
          다시 불러오기
        </Button>
      </div>
    </template>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch, onActivated } from "vue";
import { useRouter, onBeforeRouteUpdate } from "vue-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import Button from "@/components/ui/Button.vue";
import { useToast } from "@/composables/useToast";
import {
  deleteLikes,
  deletePostApi,
  fetchPostDetail,
  postLikes,
} from "@/api/postApi";
import { MessageSquare, Eye, ArrowLeft } from "lucide-vue-next";
import { HeartIcon } from "@heroicons/vue/24/solid";
import { useAuthStore } from "@/stores/authStore";
import { useUserStore } from "@/stores/userStore";
import CommentsSection from "@/components/community/CommentsSection.vue";
import UserProfileImg from "@/components/common/UserProfileImg.vue";
import KebabMenu from "@/components/community/KebabMenu.vue";
import PostBookInfo from "@/components/community/PostBookInfo.vue";

const { toast } = useToast();

const props = defineProps({
  postId: {
    type: [String, Number],
    required: true,
  },
  showBackButton: {
    type: Boolean,
    default: true,
  },
});
const emit = defineEmits(["close"]);

const router = useRouter();
const queryClient = useQueryClient();
const authStore = useAuthStore();
const userStore = useUserStore();

// 최초 로드 여부를 추적 (postId당 한 번만 조회수 증가)
const hasInitiallyLoaded = ref(false);

const { data, isLoading, isError, refetch } = useQuery({
  queryKey: ["post", props.postId],
  queryFn: () => {
    // 최초 로드가 아니면 조회수 증가 스킵
    const skipViewCount = hasInitiallyLoaded.value;
    if (!hasInitiallyLoaded.value) {
      hasInitiallyLoaded.value = true;
    }
    return fetchPostDetail(props.postId, skipViewCount);
  },
  enabled: !!props.postId,
  staleTime: 1000 * 60 * 5, // 5분간 fresh 상태 유지
  refetchOnWindowFocus: false, // 윈도우 포커스 시 자동 refetch 방지
  refetchOnMount: false, // 컴포넌트 마운트 시 자동 refetch 방지 (캐시 있으면 사용)
  refetchOnReconnect: false, // 네트워크 재연결 시 자동 refetch 방지
});

const post = computed(() => data.value);
const likeCount = computed(() => post.value?.likeCount ?? 0);
const isLiked = computed(() => !!post.value?.liked);
const me = computed(() => userStore.me);
const isOwner = computed(() => {
  if (!authStore.isLoggedIn || !post.value || !me.value) return false;
  return String(me.value.userId) === String(post.value.userId);
});

const formattedDate = computed(() => {
  const dateString = post.value?.postDate;
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString("ko-KR", {
    dateStyle: "medium",
    timeStyle: "short",
  });
});

const content = computed(() => post.value?.postContent ?? "");

const goBack = () => {
  if (!props.showBackButton) {
    emit("close");
    return;
  }
  router.push({
    name: "community",
    query: router.currentRoute.value.query,
  });
};

const syncPostToLists = (nextPost) => {
  if (!nextPost) return;

  const updatePaged = (queryKey) => {
    const cache = queryClient.getQueryData(queryKey);
    if (!cache?.pages) return;

    const updatedPages = cache.pages.map((page) => {
      const updatedPosts = page.posts?.map((p) =>
        String(p.postId) === String(nextPost.postId) ? { ...p, ...nextPost } : p
      );
      return { ...page, posts: updatedPosts };
    });

    queryClient.setQueryData(queryKey, { ...cache, pages: updatedPages });
  };

  // 커뮤니티 목록
  updatePaged(["posts"]);
  updatePaged(["posts", "latest"]);

  // 내 게시글 목록 (배열 형태)
  const myPosts = queryClient.getQueryData(["my-posts"]);
  if (Array.isArray(myPosts)) {
    const updated = myPosts.map((p) =>
      String(p.postId) === String(nextPost.postId) ? { ...p, ...nextPost } : p
    );
    queryClient.setQueryData(["my-posts"], updated);
  }
};

const updatePostInLists = (nextLiked) => {
  const updateCache = (queryKey) => {
    const cache = queryClient.getQueryData(queryKey);
    if (!cache?.pages) return cache;

    const updatedPages = cache.pages.map((page) => {
      const updatedPosts = page.posts?.map((p) => {
        if (String(p.postId) !== String(props.postId)) return p;
        const nextCount = Math.max(
          0,
          (p.likeCount ?? 0) + (nextLiked ? 1 : -1)
        );
        return { ...p, liked: nextLiked, likeCount: nextCount };
      });
      return { ...page, posts: updatedPosts };
    });

    queryClient.setQueryData(queryKey, { ...cache, pages: updatedPages });
    return cache;
  };

  const updateMyPosts = () => {
    const cache = queryClient.getQueryData(["my-posts"]);
    if (!Array.isArray(cache)) return cache;
    const updated = cache.map((p) => {
      if (String(p.postId) !== String(props.postId)) return p;
      const nextCount = Math.max(
        0,
        (p.likeCount ?? 0) + (nextLiked ? 1 : -1)
      );
      return { ...p, liked: nextLiked, likeCount: nextCount };
    });
    queryClient.setQueryData(["my-posts"], updated);
    return cache;
  };

  const previousPosts = updateCache(["posts"]);
  const previousLatest = updateCache(["posts", "latest"]);
  const previousMyPosts = updateMyPosts();

  return { previousPosts, previousLatest, previousMyPosts };
};

const rollbackPostInLists = (previousPosts, previousLatest, previousMyPosts) => {
  if (previousPosts) {
    queryClient.setQueryData(["posts"], previousPosts);
  }
  if (previousLatest) {
    queryClient.setQueryData(["posts", "latest"], previousLatest);
  }
  if (previousMyPosts) {
    queryClient.setQueryData(["my-posts"], previousMyPosts);
  }
};

const toggleLikeMutation = useMutation({
  mutationFn: ({ nextLiked }) =>
    nextLiked ? postLikes(props.postId) : deleteLikes(props.postId),
  onMutate: async ({ nextLiked }) => {
    await queryClient.cancelQueries({ queryKey: ["post", props.postId] });
    await queryClient.cancelQueries({ queryKey: ["posts"] });
    await queryClient.cancelQueries({ queryKey: ["posts", "latest"] });
    await queryClient.cancelQueries({ queryKey: ["my-posts"] });
    const previous = queryClient.getQueryData(["post", props.postId]);

    queryClient.setQueryData(["post", props.postId], (old) => {
      if (!old) return old;
      const updatedCount = Math.max(
        0,
        (old.likeCount ?? 0) + (nextLiked ? 1 : -1)
      );
      return { ...old, liked: nextLiked, likeCount: updatedCount };
    });

    const listSnapshots = updatePostInLists(nextLiked);

    return { previous, ...listSnapshots };
  },
  onError: (_err, _vars, ctx) => {
    if (ctx?.previous) {
      queryClient.setQueryData(["post", props.postId], ctx.previous);
    }
    rollbackPostInLists(
      ctx?.previousPosts,
      ctx?.previousLatest,
      ctx?.previousMyPosts
    );
  },
  onSettled: async () => {
    // 좋아요 토글 후 상세 쿼리 refetch (skipViewCount=true이므로 조회수는 증가 안 함)
    await refetch();
    
    // 목록 쿼리들도 invalidate
    queryClient.invalidateQueries({ queryKey: ["posts"] });
    queryClient.invalidateQueries({ queryKey: ["posts", "latest"] });
    queryClient.invalidateQueries({ queryKey: ["my-posts"] });
  },
});

const handleToggleLike = async () => {
  if (!authStore.isLoggedIn) {
    toast({
      title: "로그인이 필요한 서비스입니다.",
      description: "좋아요를 누르려면 먼저 로그인해주세요.",
    });
    const redirectPath = router.resolve({
      name: "postDetail",
      params: { postId: props.postId },
    }).path;
    router.push({ name: "signin", query: { redirect: redirectPath } });
    return;
  }

  if (toggleLikeMutation.isPending.value) return;

  const nextLiked = !isLiked.value;
  await toggleLikeMutation.mutateAsync({ nextLiked });
};

const editPost = () => {
  router.push({
    name: "postCreate",
    query: { mode: "edit", postId: props.postId },
    state: { post: post.value },
  });
};

const deletePostMutation = useMutation({
  mutationFn: () => deletePostApi(props.postId),
  onSuccess: async () => {
    await queryClient.invalidateQueries({ queryKey: ["posts"] });
    await queryClient.invalidateQueries({ queryKey: ["posts", "latest"] });
    await queryClient.invalidateQueries({ queryKey: ["my-posts"] });
    router.push({ name: "community" });
  },
});

const deletePost = async () => {
  if (!confirm("게시글을 삭제하시겠습니까?")) return;
  await deletePostMutation.mutateAsync();
};

// postId가 변경될 때 플래그 초기화 및 캐시 무효화 (새로운 게시글로 이동 시)
watch(
  () => props.postId,
  async () => {
    hasInitiallyLoaded.value = false;
    // 캐시 무효화하여 최신 데이터 강제 refetch (skipViewCount=false로 조회수 증가)
    await queryClient.invalidateQueries({ queryKey: ["post", props.postId] });
  }
);

onMounted(async () => {
  // 마운트 시 캐시 무효화하여 최신 데이터 강제 refetch (skipViewCount=false로 조회수 증가)
  await queryClient.invalidateQueries({ queryKey: ["post", props.postId] });
  
  if (authStore.isLoggedIn && !me.value) {
    try {
      await userStore.fetchMyInfo();
    } catch (err) {
      console.error("[POST] failed to fetch my info", err);
    }
  }
});

onActivated(async () => {
  // Keep Alive로 인한 재활성화 시 플래그 초기화 및 캐시 무효화
  hasInitiallyLoaded.value = false;
  await queryClient.invalidateQueries({ queryKey: ["post", props.postId] });
});

onBeforeRouteUpdate(async () => {
  // 라우트 변경 전 플래그 초기화
  hasInitiallyLoaded.value = false;
  // 캐시 무효화는 watch에서 처리됨
});

/**
 * 댓글 변경 이벤트 핸들러
 */
const handleCommentChanged = ({ type }) => {
  if (!post.value) return;

  // 댓글 수 변화
  const delta = type === "create" ? 1 : type === "delete" ? -1 : 0; // update는 변화 없음
  if (delta == 0) return;

  const nextCount = Math.max(0, (post.value.commentCount ?? 0) + delta);

  const nextPost = { ...post.value, commentCount: nextCount };

  // 상세 조회 캐시 즉시 반영
  queryClient.setQueryData(["post", props.postId], nextPost);

  // 목록 캐시도 동기화
  syncPostToLists(nextPost);
};
</script>
