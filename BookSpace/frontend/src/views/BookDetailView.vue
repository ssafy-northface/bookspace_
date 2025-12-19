<template>
  <main class="mx-auto max-w-6xl px-4 py-6">

    <div v-if="loadingDetail" class="py-10 text-sm text-muted-foreground">
      상세 정보를 불러오는 중...
    </div>

    <!-- URL상에서 없는 ISBN 번호로 입력해서 화면이동했을 때 -->
    <div v-else-if="detailError === 'NOT_FOUND'" class="py-16 text-center">
      <p class="text-2xl font-medium font-semibold">도서를 찾을 수 없어요</p>
      <p class="mt-2 text-base text-muted-foreground">
        ISBN이 잘못되었거나 삭제된 도서일 수 있어요.
      </p>
      <div class="mt-6 flex justify-center gap-2">
        <button class="h-9 rounded-md border px-4 py-1.5" @click="$router.back()">이전으로</button>
        <button
          class="h-9 rounded-md bg-primary px-4 py-1.5 text-primary-foreground"
          @click="$router.push('/books')"
        >
        도서 목록으로
        </button>
      </div>
    </div>

    <div v-else-if="detailError" class="py-10 text-sm text-destructive">
      {{ detailError }}
    </div>

    <div v-else-if="book" class="grid gap-6 md:grid-cols-[240px_1fr]">
      <!-- LEFT -->
      <aside class="lg:sticky lg:top-6 h-fit">
        <BookSideCard 
          :book="book" 
          :is-wished="isWished"
          :is-wish-loading="false"
          :average-rating="averageRating" 
          :review-count="reviewCount" 
          :post-count="postCount" 
          @toggle-wish="toggleWish" 
        />
      </aside>

      <!-- RIGHT -->
      <section class="space-y-10">
        <!-- 책 상세정보 -->
        <BookDetailInfo :book="book" />

        <!-- 리뷰 / 게시글 탭 -->
        <ReviewPostTabs v-model="activeTab" :review-count="reviewCount" :post-count="postCount">
          <!-- ReviewSection -->
          <template #review>
            <ReviewSection :book-id="book.bookId" :isbn="book.isbn"/>
          </template>

          <!-- PostSection -->
          <template #post>
            <PostSection />
          </template>
        </ReviewPostTabs>
      </section>
    </div>

    <div v-else class="py-10 text-sm text-muted-foreground">
      도서 정보를 찾을 수 없습니다.
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { useBookStore } from "@/stores/bookStore";
import { useWishStore } from "@/stores/wishStore";
import { useAuthStore } from "@/stores/authStore";

import BookDetailInfo from "../components/bookDetail/BookDetailInfo.vue";
import BookSideCard from "@/components/bookDetail/BookSideCard.vue";
import ReviewPostTabs from "../components/bookDetail/ReviewPostTabs.vue";

import ReviewSection from "@/components/review/ReviewSection.vue";
import PostSection from "@/components/postInBookDetail/PostSection.vue";


const activeTab = ref("review");

const route = useRoute();
const bookStore = useBookStore();
const wishStore = useWishStore();
const authStore = useAuthStore();

const book = computed(()=> bookStore.bookDetail);

// 도서 상세 로딩 후 wishStore 초기 세팅 추가
watch(
  book,
  (b) => {
    if (!b) return;
    // 백엔드에서 내려준 isWished를 store에 초기 세팅
    wishStore.setIsWished(b.isbn, b.isWished);
  },
  { immediate: true }
);

// BookSideCard에 전달
const isWished = computed(() => {
  if (!book.value) return false;
  return wishStore.isWished(book.value.isbn);
});
const averageRating = computed(() => bookStore.bookDetail?.averageRating ?? 0.0);
const reviewCount = computed(() => bookStore.bookDetail?.reviewCount ?? 0);
const postCount = computed(() => bookStore.bookDetail?.postCount ?? 0);

const loadingDetail = computed(() => bookStore.loadingDetail);
const detailError = computed(() => bookStore.detailError);

const load = () => {
  const isbn = route.params.isbn;
  if (!isbn) return;
  bookStore.loadBookDetail(isbn);
};

onMounted(load);
// 같은 화면에서 isbn만 바뀌는 경우 대비
watch(() => route.params.isbn, load);

// 찜 버튼 클릭 핸들러
// 비로그인 유저는 차단 후 안내 메시지
async function toggleWish() {

  console.log("[toggleWish] clicked");
  console.log("[toggleWish] isbn/bookId =", book.value?.isbn, book.value?.bookId);


  if (!book.value) return;

  // 1) 비로그인 처리
  if (!authStore.isLoggedIn) {
    alert("로그인 후 이용 가능한 서비스입니다");
    // toast("로그인 후 이용 가능한 서비스입니다");
    return;
  }

  const isbn = book.value.isbn;
  // 현재 찜 상태 저장
  const wasWished = wishStore.isWished(isbn);
  
  
  try {
    await wishStore.toggleWish({
      isbn,
      bookId: book.value.bookId, // 찜 해제 시 필요
    });

    // 토글 성공하면 UI 즉시 반영
    wishStore.setIsWished(isbn, !wasWished);

    // 찜 추가했을 때만 상세 정보 재조회 (bookId 받아오기)
    if (!wasWished) {
      await bookStore.loadBookDetail(isbn);
      // 재조회 후 wishStore 상태 다시 동기화
       wishStore.setIsWished(isbn, book.value.isWished);
    }

  

  } catch (e) {
    console.error(e);
    //  실패하면 원래 상태로 복구
    wishStore.setIsWished(isbn,wasWished);
    alert("찜 처리 중 오류가 발생했습니다");
    // toast("찜 처리 중 오류가 발생했습니다");
  }
};
</script>
