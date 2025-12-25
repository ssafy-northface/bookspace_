<template>
  <main class="max-w-6xl px-4 py-6 mx-auto" :key="resolvedIsbn">
    <div
      v-if="loadingDetail && !book"
      class="py-10 text-sm text-muted-foreground"
    >
      상세 정보를 불러오는 중...
    </div>

    <!-- URL상에서 없는 ISBN 번호로 입력해서 화면이동했을 때 -->
    <div v-else-if="detailError === 'NOT_FOUND'" class="py-16 text-center">
      <p class="text-2xl font-medium font-semibold">도서를 찾을 수 없어요</p>
      <p class="mt-2 text-base text-muted-foreground">
        ISBN이 잘못되었거나 삭제된 도서일 수 있어요.
      </p>
      <div class="flex justify-center gap-2 mt-6">
        <button
          class="h-9 rounded-md border px-4 py-1.5"
          @click="$router.back()"
        >
          이전으로
        </button>
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
        <ReviewPostTabs
          v-model="activeTab"
          :key="book.bookId"
          :review-count="reviewCount"
          :post-count="postCount"
        >
          <!-- ReviewSection -->
          <template #review>
            <ReviewSection
              :key="book.bookId"
              :book-id="book.bookId"
              :isbn="book.isbn"
              @review-updated="load"
            />
          </template>

          <!-- PostSection -->
          <template #post>
            <PostSection 
              :isbn="book.isbn" 
              :book-id="book.bookId"
              @post-updated="load"
            />
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
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute } from "vue-router";
import { useBookStore } from "@/stores/bookStore";
import { useWishStore } from "@/stores/wishStore";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "@/composables/useToast";
import { useRequireAuth } from "@/composables/useRequireAuth";

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
const { toast } = useToast();
const { requireAuth } = useRequireAuth();

const book = computed(() => bookStore.bookDetail);

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
const averageRating = computed(() => book.value?.averageRating ?? 0.0);
const reviewCount = computed(() => book.value?.reviewCount ?? 0);
const postCount = computed(() => book.value?.postCount ?? 0);

const loadingDetail = computed(() => bookStore.loadingDetail);
const detailError = computed(() => bookStore.detailError);

const props = defineProps({
  isbn: {
    type: [String, Number],
    default: null,
  },
});

const resolvedIsbn = computed(() => props.isbn ?? route.params.isbn);

const load = async (isbn) => {
  if (!isbn) return;
  // 라우트가 변경될 때 이전 데이터를 즉시 초기화하여 이전 페이지 내용이 보이지 않도록 함
  bookStore.clearBookDetail();
  await bookStore.loadBookDetail(isbn);
};

watch(
  () => resolvedIsbn.value,
  (isbn, oldIsbn) => {
    // ISBN이 변경될 때만 로드
    if (isbn && isbn !== oldIsbn) {
      load(isbn);
    }
  },
  { immediate: true }
);

// 페이지 마운트 시 스크롤을 맨 위로 즉시 이동
onMounted(() => {
  window.scrollTo(0, 0);
});

// 찜 버튼 클릭 핸들러
// 비로그인 유저는 토스트 알림 후 로그인 페이지로 리다이렉트
const toggleWish = requireAuth(async () => {
  console.log("[toggleWish] clicked");
  console.log(
    "[toggleWish] isbn/bookId =",
    book.value?.isbn,
    book.value?.bookId
  );

  if (!book.value) return;

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
    wishStore.setIsWished(isbn, wasWished);
    toast({
      title: "오류",
      description: "찜 처리 중 오류가 발생했습니다",
      variant: "destructive",
    });
  }
}, {
  loginMessage: "찜하기는 로그인 후 이용 가능한 서비스입니다",
  redirect: route.fullPath, // 현재 책 상세 페이지로 리다이렉트
});
</script>
