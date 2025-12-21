<!-- 
# 게시글 카드 컴포넌트 
-->
// TODO 유저 이미지 추가 (DB, S3 연동 후) or 새싹, 책 기본 이미지로 수정
<template>
  <div
    class="flex justify-between w-full p-5 transition border shadow-sm cursor-pointer border-border rounded-xl bg-card hover:shadow-md"
    :data-post-id="post.postId"
    @click="goToPostDetail"
  >
    <div class="flex flex-col justify-between">
      <!-- 작성자 정보 -->
      <div class="flex items-center gap-3 mb-4">
        <UserProfileImg
          :src="post.userProfileImage"
          :name="post.userNickName"
          sizeClass="w-10 h-10"
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
import { useRoute, useRouter } from "vue-router";
import { useFormattedDate } from "@/composables/useFormattedDate";
import UserProfileImg from "@/components/common/UserProfileImg.vue";
const { toast } = useToast();

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
  disableNavigation: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["open"]);

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const queryClient = useQueryClient();

// TODO 기본 이미지
const defaultBook = "/default-book.png";

// 날짜 포맷
const formattedDate = useFormattedDate(props.post.postDate, {
  dateStyle: "medium", // 게시글 카드에서는 날짜만
});

// 좋아요
const likeCount = ref(props.post.likeCount ?? 0);
const isLiked = ref(!!props.post.liked);

// 좋아요 토글 (낙관적 업데이트)
const toggleLikeMutation = useMutation({
  mutationFn: (nextLiked) =>
    nextLiked ? postLikes(props.post.postId) : deleteLikes(props.post.postId),
  onMutate: async (nextLiked) => {
    // 현재 목록 쿼리 취소
    await queryClient.cancelQueries({ queryKey: ["posts"] });
    await queryClient.cancelQueries({ queryKey: ["posts", "latest"] });
    await queryClient.cancelQueries({ queryKey: ["my-posts"] });
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
    await queryClient.invalidateQueries({ queryKey: ["my-posts"] });
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
  await toggleLikeMutation.mutateAsync(nextLiked);
};

const goToPostDetail = () => {
  if (props.disableNavigation) {
    emit("open", props.post);
    return;
  }
  // 현재 스크롤 위치를 저장해서 돌아올 때 위치 복원하기
  sessionStorage.setItem(
    "communityScroll",
    JSON.stringify({
      scrollY: window.scrollY,
      postId: props.post.postId,
    })
  );
  router.push({
    name: "postDetail",
    params: { postId: props.post.postId },
    query: {
      ...route.query,
    },
  });
};
</script>
