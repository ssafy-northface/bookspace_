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

          <div
            class="flex flex-wrap items-center gap-3 text-sm text-muted-foreground"
          >
            <button
              type="button"
              class="flex items-center gap-1 px-3 py-1 transition rounded-full group"
              :class="
                isLiked
                  ? 'text-destructive bg-destructive/10'
                  : 'hover:bg-muted'
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
        </div>

        <p class="leading-relaxed whitespace-pre-line text-foreground">
          {{ content }}
        </p>
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
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import ViewHeader from "@/components/common/ViewHeader.vue";
import Button from "@/components/ui/Button.vue";
import { useToast } from "@/composables/useToast";
import Separator from "@/components/ui/Separator.vue";
import { deleteLikes, fetchPostDetail, postLikes } from "@/api/postApi";
import { MessageSquare, Eye, ArrowLeft } from "lucide-vue-next";
import { HeartIcon } from "@heroicons/vue/24/solid";
import { useAuthStore } from "@/stores/authStore";

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

const { data, isLoading, isError, refetch } = useQuery({
  queryKey: ["post", props.postId],
  queryFn: () => fetchPostDetail(props.postId),
  enabled: !!props.postId,
  staleTime: 0,
});

const post = computed(() => data.value);
const likeCount = computed(() => post.value?.likeCount ?? 0);
const isLiked = computed(() => !!post.value?.liked);

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
  router.back();
};

const toggleLikeMutation = useMutation({
  mutationFn: ({ nextLiked }) =>
    nextLiked ? postLikes(props.postId) : deleteLikes(props.postId),
  onMutate: async ({ nextLiked }) => {
    await queryClient.cancelQueries({ queryKey: ["post", props.postId] });
    const previous = queryClient.getQueryData(["post", props.postId]);

    queryClient.setQueryData(["post", props.postId], (old) => {
      if (!old) return old;
      const updatedCount = Math.max(
        0,
        (old.likeCount ?? 0) + (nextLiked ? 1 : -1)
      );
      return { ...old, liked: nextLiked, likeCount: updatedCount };
    });

    return { previous };
  },
  onError: (_err, _vars, ctx) => {
    if (ctx?.previous) {
      queryClient.setQueryData(["post", props.postId], ctx.previous);
    }
  },
  onSettled: () => {
    queryClient.invalidateQueries({ queryKey: ["post", props.postId] });
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
</script>
