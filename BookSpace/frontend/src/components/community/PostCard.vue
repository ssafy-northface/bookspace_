// TODO 유저 이미지 추가 (DB, S3 연동 후) or 새싹, 책 기본 이미지로 수정
<template>
  <div
    class="flex justify-between w-full p-5 transition border shadow-sm cursor-pointer border-border rounded-xl bg-card hover:shadow-md"
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
            <!-- 좋아요 -->
            <div class="flex items-center">
              <ThumbsUp class="w-4 h-4 mr-1" />
              <span>{{ post.likeCount }}</span>
            </div>

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
import { computed } from "vue";
import { ThumbsUp, MessageSquare, Eye } from "lucide-vue-next";

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
});

// 기본 이미지
const defaultProfile = "/default-profile.png";
const defaultBook = "/default-book.png";

// 날짜 포맷
const formattedDate = computed(() => {
  if (!props.post.postDate) return "";
  const date = new Date(props.post.postDate);
  return date.toLocaleDateString("ko-KR");
});
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
