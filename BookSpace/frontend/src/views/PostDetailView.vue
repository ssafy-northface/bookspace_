<template>
  <section class="max-w-5xl mx-auto mt-6 space-y-6">
    <div class="flex items-center justify-between gap-4">
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
            <img
              :src="post.userProfileImage || defaultProfile"
              alt="작성자 프로필"
              class="object-cover w-12 h-12 rounded-full"
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
          <div class="flex items-center gap-2">
            <div v-if="isOwner" class="relative">
              <button
                type="button"
                class="flex items-center justify-center w-8 h-8 rounded-full hover:bg-muted"
                @click="toggleMenu"
              >
                <MoreVertical class="w-4 h-4 text-muted-foreground" />
              </button>
              <!-- ... 클릭 -> 메뉴 -->
              <div
                v-if="showMenu"
                class="absolute right-0 z-10 w-20 h-auto p-3 mt-1 overflow-hidden border rounded-md shadow-md bg-card border-border"
              >
                <!-- 수정 버튼 -->
                <button
                  type="button"
                  class="flex justify-center w-full px-3 py-3 mb-1 text-sm text-left hover:bg-muted"
                  @mousedown.prevent
                  @click="editPost"
                >
                  수정하기
                </button>
                <!-- 삭제버튼 -->
                <button
                  type="button"
                  class="flex justify-center w-full px-3 py-2 text-sm text-left text-destructive hover:bg-muted"
                  @mousedown.prevent
                  @click="deletePost"
                >
                  삭제하기
                </button>
              </div>
            </div>
          </div>
        </header>

        <div class="space-y-4">
          <h1 class="text-2xl font-semibold text-foreground">
            {{ post.postTitle }}
          </h1>

          <div class="space-y-3">
            <Separator />
            <div class="flex items-start gap-4 px-1">
              <div class="h-40 overflow-hidden rounded-md w-28 bg-muted">
                <img
                  :src="post.bookImageUrl || defaultBook"
                  alt="book cover"
                  class="object-cover w-full h-full"
                />
              </div>
              <div class="flex-1 space-y-1">
                <p class="text-lg font-semibold text-foreground">
                  {{ post.bookTitle || "도서 정보 없음" }}
                </p>
                <p class="text-sm text-muted-foreground">
                  {{
                    post.bookAuthor || "작성자가 도서 정보를 추가하지 않았어요."
                  }}
                </p>
                <p v-if="post.isbn" class="text-xs text-muted-foreground">
                  ISBN {{ post.isbn }}
                </p>
              </div>
            </div>
            <Separator />
          </div>
        </div>

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
import Separator from "@/components/ui/Separator.vue";
import {
  deleteLikes,
  deletePostApi,
  fetchPostDetail,
  postLikes,
} from "@/api/postApi";
import { MessageSquare, Eye, ArrowLeft, MoreVertical } from "lucide-vue-next";
import { HeartIcon } from "@heroicons/vue/24/solid";
import { useAuthStore } from "@/stores/authStore";
import { useUserStore } from "@/stores/userStore";

const { toast } = useToast();

const props = defineProps({
  postId: {
    type: [String, Number],
    required: true,
  },
});

const router = useRouter();
const queryClient = useQueryClient();
const authStore = useAuthStore();
const userStore = useUserStore();

const { data, isLoading, isError, refetch } = useQuery({
  queryKey: ["post", props.postId],
  queryFn: () => fetchPostDetail(props.postId),
  enabled: !!props.postId,
  staleTime: 0,
});

const post = computed(() => data.value);
const viewAdjusted = ref(false);
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

const defaultProfile = "/default-profile.png";
const defaultBook = "/default-book.png";

const goBack = () => {
  router.push({
    name: "community",
    query: router.currentRoute.value.query,
  });
};

const showMenu = ref(false);
const toggleMenu = () => {
  showMenu.value = !showMenu.value;
};
const closeMenu = () => {
  showMenu.value = false;
};

const syncPostToLists = (nextPost) => {
  if (!nextPost) return;

  const updateCache = (queryKey) => {
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

  updateCache(["posts"]);
  updateCache(["posts", "latest"]);
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

  const previousPosts = updateCache(["posts"]);
  const previousLatest = updateCache(["posts", "latest"]);

  return { previousPosts, previousLatest };
};

const rollbackPostInLists = (previousPosts, previousLatest) => {
  if (previousPosts) {
    queryClient.setQueryData(["posts"], previousPosts);
  }
  if (previousLatest) {
    queryClient.setQueryData(["posts", "latest"], previousLatest);
  }
};

const toggleLikeMutation = useMutation({
  mutationFn: ({ nextLiked }) =>
    nextLiked ? postLikes(props.postId) : deleteLikes(props.postId),
  onMutate: async ({ nextLiked }) => {
    await queryClient.cancelQueries({ queryKey: ["post", props.postId] });
    await queryClient.cancelQueries({ queryKey: ["posts"] });
    await queryClient.cancelQueries({ queryKey: ["posts", "latest"] });
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
    rollbackPostInLists(ctx?.previousPosts, ctx?.previousLatest);
  },
  onSettled: () => {
    queryClient.invalidateQueries({ queryKey: ["post", props.postId] });
    queryClient.invalidateQueries({ queryKey: ["posts"] });
    queryClient.invalidateQueries({ queryKey: ["posts", "latest"] });
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
  closeMenu();
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
    router.push({ name: "community" });
  },
});

const deletePost = async () => {
  closeMenu();
  if (!confirm("게시글을 삭제하시겠습니까?")) return;
  await deletePostMutation.mutateAsync();
};

onMounted(async () => {
  if (authStore.isLoggedIn && !me.value) {
    try {
      await userStore.fetchMyInfo();
    } catch (err) {
      console.error("[POST] failed to fetch my info", err);
    }
  }
  viewAdjusted.value = false;
});

onActivated(() => {
  viewAdjusted.value = false;
});

watch(
  () => post.value?.postViewCnt,
  (val) => {
    if (!post.value || viewAdjusted.value) return;
    const nextCount = (val ?? 0) + 1;
    const nextPost = { ...post.value, postViewCnt: nextCount };
    queryClient.setQueryData(["post", props.postId], nextPost);
    syncPostToLists(nextPost);
    viewAdjusted.value = true;
  },
  { immediate: true }
);

onBeforeRouteUpdate(() => {
  viewAdjusted.value = false;
});
</script>
