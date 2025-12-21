<template>
  <div class="space-y-6">
    <!-- 리뷰 작성 폼 (내 리뷰가 없거나, 수정 모드일 때만 보임) -->
    <ReviewCreate
      v-if="!myReview || isEditing"
      :book-id="bookId" 
      :isbn="isbn"
      :mode="isEditing ? 'edit' : 'create'"
      :initial-rating="editInitial.rating"
      :initial-content="editInitial.content"
      :review-id="editInitial.reviewId"
      @cancel-edit="handleCancelEdit"
      @saved="handleSaved"
      ></ReviewCreate>

    <!-- 내 리뷰가 있으면 상단에 리뷰 카드 고정 -->
    <MyReviewCard
      v-else
      :review="myReview"
      @edit="handleEdit"
      @deleted="handleDeleted"
    />

    <!-- 리뷰 목록 (로그인 유저가 아닌 다른 사람들이 작성한 리뷰 목록) -->
    <ReviewList 
      :reviews="otherReviews" 
      :has-book-id="!!bookId"
      :has-my-review="!!myReview"
    ></ReviewList>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from "vue";
import { useReviewStore } from "@/stores/reviewStore";
import { useUserStore } from "@/stores/userStore";
import { useAuthStore } from "@/stores/authStore";
import { useBookStore } from "@/stores/bookStore";

import ReviewCreate from "./ReviewCreate.vue";
import ReviewList from "./ReviewList.vue";
import MyReviewCard from "./MyReviewCard.vue";

const props = defineProps({
  bookId: { type: Number, required: true },
  isbn: { type: String, required: true },
});

const reviewStore = useReviewStore();
const userStore = useUserStore();
const authStore = useAuthStore();
const bookStore = useBookStore();

const emit = defineEmits(["review-updated"]);

const meLoading = ref(false);

// 로그인 유저 id
const me = computed(()=>userStore.me);
const loginUserId = computed(() => me.value?.userId ?? null);

// 전체 리뷰
const reviews = computed(() => reviewStore.reviewsByBook);

// 내 리뷰 찾기
const myReview = computed(() => {
  if (!loginUserId.value) return null;
  return reviews.value.find((r) => String(r.userId) === String(loginUserId.value)) ?? null;
});

// 내 리뷰를 제외한 나머지 리뷰
const otherReviews = computed(() => {
  if (!myReview.value) return reviews.value;
  return reviews.value.filter((r) => r.reviewId !== myReview.value.reviewId);
});

// 수정모드 상태
const isEditing = ref(false);

// 수정 시작 시 ReviewCreate에 주입할 초기값
const editInitial = ref({
  reviewId: null,
  rating: 0,
  content: "",
});

// 로그인 상태면 me를 먼저 확보
const ensureMe = async () => {
  if (!authStore.isLoggedIn) return;
  if (me.value) return;

  meLoading.value = true;
  try {
    await userStore.fetchMyInfo();
  } catch (err) {
    console.error("[REVIEW] failed to fetch my info", err);
  } finally {
    meLoading.value = false;
  }
};


// 목록 로드
const fetch = async () => {
  // bookId 없으면 잔상 제거하고 종료
  if (!props.bookId) {
    reviewStore.reviewsByBook = [];
    return;
  }
  reviewStore.reviewsByBook = [];
  // 일단 sort는 latest로 고정
  await reviewStore.fetchReviewsByBookId(props.bookId, "latest");
};

onMounted(async () => {
  // me를 먼저 확보한 뒤 리뷰 목록 로드
  await ensureMe();
  await fetch();
});

// bookId가 바뀌는 경우(다른 상세로 이동) 대비 + 수정모드 초깃화
watch(
  () => props.bookId,
  async (newVal, oldVal) => {
    if (!newVal || newVal === oldVal) return;

    reviewStore.reviewsByBook = [];

    isEditing.value = false;
    editInitial.value = { reviewId: null, rating: 0, content: "" };
    
    await ensureMe();
    await fetch();
  }
);

// 로그인 직후 상세 진입 대비
watch(
  () => authStore.isLoggedIn,
  async (loggedIn) => {
    if (!loggedIn) return;
    await ensureMe();
  }
);

// 수정 버튼 클릭 -> ReviewCreate를 edit 모드로 띄우기
// 기존에 로그인 유저가 작성해둔 리뷰 평점 & 내용을 그대로
function handleEdit() {
  if (!myReview.value) return;

  isEditing.value = true;
  editInitial.value = {
    reviewId: myReview.value.reviewId,
    rating: myReview.value.reviewRating,
    content: myReview.value.reviewContent ?? "",
  };
}

// 수정 취소
function handleCancelEdit() {
  isEditing.value = false;
  editInitial.value = { reviewId: null, rating: 0, content: "" };
}

// 작성/수정 완료 후
async function handleSaved() {
  isEditing.value = false;
  editInitial.value = { reviewId: null, rating: 0, content: "" };
  
  await fetch(); // 리뷰 목록 갱신
  await bookStore.loadBookDetail(props.isbn, { force: true }); // 평점/리뷰 개수 갱신
  
  emit("review-updated");
}

// 삭제 완료 후
async function handleDeleted() {
  await fetch(); // 리뷰 목록 갱신
  await bookStore.loadBookDetail(props.isbn); // 평점/리뷰 개수 갱신

  emit("review-updated");

  isEditing.value = false;
  editInitial.value = { reviewId: null, rating: 0, content: "" };
}

</script>

<style scoped></style>
