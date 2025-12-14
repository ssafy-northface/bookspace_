// TODO 유저 이미지 추가 (DB, S3 연동 후) or 새싹, 책 기본 이미지로 수정
<template>
  <div
    class="flex justify-between w-full p-5 transition border shadow-sm cursor-pointer border-border rounded-xl bg-card hover:shadow-md"
    @click="goToPostDetail"
  >
    <div class="flex flex-col justify-between">
      <!-- 작성자 정보 -->
      <div class="flex items-center gap-3 mb-4">
        <img
          :src="post.userProfileImage || defaultProfile"
          class="object-cover w-10 h-10 rounded-full"
          alt="profile"
        />
        <div>
          <p class="font-medium text-foreground">{{ post.userNickName }}</p>
          <p class="text-sm text-muted-foreground">
            {{ formattedDate }}
          </p>
        </div>
      </div>

      <!-- 게시글 내용  -->
      <div class="flex gap-4">
        <!-- 왼쪽: 게시글 정보 -->
        <div class="flex-1">
          <!-- 제목 -->
          <h2 class="mb-2 text-lg font-semibold text-foreground">
            {{ post.postTitle }}
          </h2>

          <!-- 내용 미리보기 -->
          <p class="mb-4 text-sm text-muted-foreground line-clamp-2">
            {{ post.postContent }}
          </p>

          <!-- 메타 정보 -->
          <div class="flex items-center gap-4 text-sm text-muted-foreground">
            <!-- 좋아요 (하트 버튼) -->
            <button
              type="button"
              class="flex items-center gap-1 px-3 py-1 transition rounded-full"
              :disabled="toggleLikeMutation.isPending.value"
              @click.stop="toggleLike"
            >
              <component
                :is="isLiked ? HeartSolid : HeartOutline"
                class="w-5 h-5 transition"
                :class="
                  isLiked
                    ? 'text-red-500'
                    : 'text-muted-foreground hover:text-red-400'
                "
              />
              <span :class="isLiked ? 'text-foreground font-medium' : ''">
                {{ likeCount }}
              </span>
            </button>

            <!-- 댓글 -->
            <div class="flex items-center">
              <MessageSquare class="w-4 h-4 mr-1" />
              <span>{{ post.commentCount }}</span>
            </div>

            <!-- 조회수 -->
            <div class="flex items-center">
              <Eye class="w-4 h-4 mr-1" />
              <span>{{ post.postViewCnt }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 오른쪽: 책 정보 -->
    <div class="flex flex-col items-center text-center w-28">
      <img
        :src="post.bookImageUrl || defaultBook"
        class="object-cover w-24 h-32 rounded"
        alt="book"
      />

      <p class="mt-2 text-sm font-medium text-foreground line-clamp-1">
        {{ post.bookTitle }}
      </p>
      <p class="text-xs text-muted-foreground line-clamp-1">
        {{ post.bookAuthor }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { MessageSquare, Eye } from "lucide-vue-next";
import { HeartIcon as HeartSolid } from "@heroicons/vue/24/solid";
import { HeartIcon as HeartOutline } from "@heroicons/vue/24/outline";
import { useToast } from "@/composables/useToast";
import { postLikes, deleteLikes } from "@/api/postApi";
import { useMutation, useQueryClient } from "@tanstack/vue-query";
import { useAuthStore } from "@/stores/authStore";
import { useRouter } from "vue-router";
const { toast } = useToast();

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
});

const router = useRouter();
const authStore = useAuthStore();
const queryClient = useQueryClient();

// TODO 기본 이미지
const defaultProfile = "/default-profile.png";
const defaultBook = "/default-book.png";

// 날짜 포맷
const formattedDate = computed(() => {
  if (!props.post.postDate) return "";
  const date = new Date(props.post.postDate);
  return date.toLocaleDateString("ko-KR");
});

// 좋아요
const likeCount = ref(props.post.likeCount ?? 0);
const isLiked = ref(!!props.post.liked);

// 좋아요 토글 (낙관적 업데이트)
const toggleLikeMutation = useMutation({
  mutationFn: ({ postId, nextLiked }) =>
    nextLiked ? postLikes(postId) : deleteLikes(postId),
  onMutate: async ({ nextLiked }) => {
    // 현재 목록 쿼리 취소
    await queryClient.cancelQueries({ queryKey: ["posts"] });
    await queryClient.cancelQueries({ queryKey: ["posts", "latest"] });
    const previous = { liked: isLiked.value, likeCount: likeCount.value };

    // 낙관적 업데이트
    isLiked.value = nextLiked;
    likeCount.value = Math.max(0, likeCount.value + (nextLiked ? 1 : -1));
    return previous;
  },
  onError: (_err, _vars, ctx) => {
    console.error("[LIKE] Mutation failed. Rolling back.", _err);
    // 실패 시 롤백
    if (ctx) {
      isLiked.value = ctx.liked;
      likeCount.value = ctx.likeCount;
    }
  },
  onSettled: async () => {
    console.log("[LIKE] Mutation settled. Invalidating queries.");
    await queryClient.invalidateQueries({ queryKey: ["posts"] });
    await queryClient.invalidateQueries({ queryKey: ["posts", "latest"] });
  },
});

const isToggling = computed(() => toggleLikeMutation.isPending.value);
watch(
  () => [props.post.likeCount, props.post.liked],
  ([count, liked]) => {
    likeCount.value = count ?? 0;
    isLiked.value = !!liked;
  },
  { immediate: true }
);

const toggleLike = async () => {
  if (!authStore.isLoggedIn) {
    toast({
      title: "로그인이 필요한 서비스입니다.",
      description: "좋아요를 누르시려면 먼저 로그인해주세요.",
    });
    const redirectPath =
      router.currentRoute.value?.fullPath ||
      router.resolve({ name: "community" }).path;

    router.push({
      name: "signin",
      query: { redirect: redirectPath },
    });
    return;
  }

  if (isToggling.value) {
    return;
  }

  const nextLiked = !isLiked.value;
  await toggleLikeMutation.mutateAsync({
    postId: props.post.postId,
    nextLiked,
  });
};

const goToPostDetail = () => {
  // TODO 게시글 상세 페이지 조회
};
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
