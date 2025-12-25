<template>
  <!-- 도서목록 클릭하면 초기화되면서 메인 도서 목록으로 돌아오도록 설정 -->
  <RouterLink :to="{ path: '/books' }">
    <ViewHeader
      title="도서 목록"
      description="다양한 책들을 탐색해보세요"
      align="left"
      size="lg"
    />
  </RouterLink>

  <div>
    <!-- 검색창  -->
    <section class="mb-8">
      <BookSearchInput
        :initialQuery="query"
        :initialType="type"
        @search="search"
      ></BookSearchInput>
    </section>

    <!-- 정렬기준 선택 (검색모드일때만 보임)-->
    <section v-if="isSearchMode" class="mb-8">
      <BookSortBar :sort="sort" @sort-change="changeSort"></BookSortBar>
    </section>
    <!-- 로딩 -->
    <!-- <div v-if="loading" class="mt-6 text-sm text-muted-foreground">로딩중...</div> -->

    <!-- 에러 -->
    <!-- <div v-else-if="error" class="mt-6 text-sm text-red-500">
      {{ error }}
    </div> -->

    <!-- 도서 목록 -->
    <section>
      <!-- 베스트셀러 제목 (검색 아닐 때만) -->
      <h2 v-if="!isSearchMode" class="mb-8 text-2xl font-semibold">
        인기 도서
      </h2>
      <BookList
        v-if="!loading && !error && books.length"
        :books="books"
      ></BookList>

      <!-- 결과 없음 -->
      <div
        v-else
        class="flex min-h-[160px] flex-col items-center justify-center rounded-xl border border-dashed text-sm text-muted-foreground mt-6"
      >
        {{ isSearchMode ? "검색 결과가 없습니다." : "표시할 도서가 없습니다." }}
        <span class="mt-1"
          >검색어를 바꿔보거나 다른 조건으로 시도해보세요.</span
        >
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, onUnmounted, nextTick, watch } from "vue";
import { useRouter } from "vue-router";
import ViewHeader from "../components/common/ViewHeader.vue";
import BookList from "../components/book/BookList.vue";
import BookSearchInput from "../components/book/BookSearchInput.vue";
import BookSortBar from "../components/book/BookSortBar.vue";

import { useBookSearch } from "@/composables/useBookSearch";
import { RouterLink } from "vue-router";

const router = useRouter();

const SCROLL_POSITION_KEY = "booksViewScrollPosition";
const IS_DIRECT_NAVIGATION_KEY = "booksViewIsDirectNavigation";
const SORT_CHANGE_SCROLL_KEY = "booksViewSortChangeScroll";
const IS_FROM_DETAIL_KEY = "booksViewIsFromDetail";

const {
  query,
  type,
  sort,
  isSearchMode,
  books,
  loading,
  error,
  search,
  changeSort,
} = useBookSearch();

// 정렬 옵션이 변경될 때 스크롤 위치 저장
watch(sort, () => {
  if (isSearchMode.value) {
    // 검색 모드에서 정렬 변경 시 현재 스크롤 위치 저장
    sessionStorage.setItem(SORT_CHANGE_SCROLL_KEY, window.scrollY.toString());
  }
});

// 로딩이 완료되면 저장된 스크롤 위치 복원
watch(loading, async (isLoading) => {
  if (!isLoading && isSearchMode.value) {
    const savedScrollPosition = sessionStorage.getItem(SORT_CHANGE_SCROLL_KEY);
    if (savedScrollPosition) {
      // DOM이 완전히 렌더링될 때까지 대기
      await nextTick();
      setTimeout(() => {
        window.scrollTo({ top: parseInt(savedScrollPosition, 10), behavior: 'auto' });
        sessionStorage.removeItem(SORT_CHANGE_SCROLL_KEY);
      }, 100);
    }
  }
});

// 스크롤 위치를 실시간으로 저장하는 함수 (throttle 적용)
let scrollTimeout;
const saveScrollPosition = () => {
  clearTimeout(scrollTimeout);
  scrollTimeout = setTimeout(() => {
    sessionStorage.setItem(SCROLL_POSITION_KEY, window.scrollY.toString());
  }, 100);
};

// 페이지를 떠날 때 스크롤 위치 저장 (여러 방법으로 확실히 저장)
const saveScrollBeforeLeave = () => {
  sessionStorage.setItem(SCROLL_POSITION_KEY, window.scrollY.toString());
};

// 라우터를 감시하여 도서 상세로 이동할 때 플래그 설정
watch(() => router.currentRoute.value.path, (newPath, oldPath) => {
  // 도서 목록(/books 또는 /books?...)에서 도서 상세(/books/...)로 이동하는 경우
  if (oldPath && oldPath.startsWith('/books') && !oldPath.match(/^\/books\/[^/]+$/)) {
    if (newPath && newPath.startsWith('/books/') && newPath !== '/books' && newPath.match(/^\/books\/[^/]+$/)) {
      sessionStorage.setItem(IS_FROM_DETAIL_KEY, 'true');
    }
  }
});

// visibilitychange 이벤트로도 저장 (페이지를 떠날 때)
const handleVisibilityChange = () => {
  if (document.hidden) {
    saveScrollBeforeLeave();
  }
};

onMounted(async () => {
  // 브라우저의 기본 스크롤 복원 비활성화
  if ('scrollRestoration' in history) {
    history.scrollRestoration = 'manual';
  }
  
  // visibilitychange 이벤트 리스너 추가
  document.addEventListener('visibilitychange', handleVisibilityChange);
  
  // 직접 링크로 들어온 경우인지 확인
  const isDirectNavigation = sessionStorage.getItem(IS_DIRECT_NAVIGATION_KEY) === "true";
  const savedScrollPosition = sessionStorage.getItem(SCROLL_POSITION_KEY);
  
  // navigation type 확인
  const navigationEntries = performance.getEntriesByType('navigation');
  const navigationType = navigationEntries[0]?.type;
  const isBackForward = navigationType === 'back_forward';
  
  // document.referrer 확인
  const referrer = document.referrer;
  const isFromBookDetail = referrer && referrer.includes('/books/') && !referrer.endsWith('/books');
  // BookDetailView에서 설정한 플래그 확인
  const isFromDetailFlag = sessionStorage.getItem(IS_FROM_DETAIL_KEY) === "true";
  
  // 스크롤 이벤트 리스너 추가 (실시간 저장)
  window.addEventListener('scroll', saveScrollPosition, { passive: true });
  
  // 스크롤 복원 함수
  const restoreScroll = (scrollPos) => {
    if (scrollPos && scrollPos > 0) {
      // 여러 번 시도하여 확실히 복원
      const tryRestore = (attempts = 0) => {
        if (attempts < 5) {
          window.scrollTo({ top: scrollPos, behavior: 'auto' });
          // 스크롤이 제대로 복원되었는지 확인
          setTimeout(() => {
            if (Math.abs(window.scrollY - scrollPos) > 10) {
              tryRestore(attempts + 1);
            }
          }, 50);
        }
      };
      tryRestore();
    }
  };
  
  // 뒤로가기로 도서 상세에서 돌아온 경우만 스크롤 복원
  // 그 외의 경우(홈에서 온 경우, 직접 링크, 다른 페이지에서 온 경우)는 모두 맨 위로
  if ((isBackForward || isFromDetailFlag) && savedScrollPosition) {
    // 뒤로가기로 도서 상세에서 돌아온 경우만 저장된 스크롤 위치로 복원
    const scrollPos = parseInt(savedScrollPosition, 10);
    // DOM이 완전히 렌더링될 때까지 대기
    await nextTick();
    // 데이터 로딩 완료 후에도 복원 시도
    if (!loading.value) {
      setTimeout(() => restoreScroll(scrollPos), 200);
    } else {
      // 로딩 중이면 로딩 완료 후 복원
      watch(loading, (isLoading) => {
        if (!isLoading) {
          setTimeout(() => restoreScroll(scrollPos), 200);
        }
      }, { once: true });
    }
  } else {
    // 그 외의 모든 경우 맨 위로 스크롤
    window.scrollTo(0, 0);
    sessionStorage.removeItem(SCROLL_POSITION_KEY);
    sessionStorage.removeItem(IS_DIRECT_NAVIGATION_KEY);
    sessionStorage.removeItem(IS_FROM_DETAIL_KEY);
  }
  
  // 스크롤 복원 후 플래그 제거
  if (isFromDetailFlag) {
    sessionStorage.removeItem(IS_FROM_DETAIL_KEY);
  }
});

// 페이지를 떠날 때 스크롤 위치 저장
onBeforeUnmount(() => {
  saveScrollBeforeLeave();
});

onUnmounted(() => {
  // 스크롤 이벤트 리스너 제거
  window.removeEventListener('scroll', saveScrollPosition);
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  clearTimeout(scrollTimeout);
});
</script>

<style scoped></style>
