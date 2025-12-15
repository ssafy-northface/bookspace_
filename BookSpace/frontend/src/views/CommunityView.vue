<!-- TODO SearchInput 검색 기능 -->
<!-- TODO 스타일 수정 -->
<template>
  <ViewHeader
    title="커뮤니티"
    description="커뮤니티 게시판 목록을 확인하세요."
    align="left"
    size="lg"
  />

  <!-- 검색인풋 + 게시글 작성 버튼 -->
  <section
    ref="searchAreaRef"
    class="flex items-center justify-between gap-4 mt-6 mb-4"
  >
    <SearchInput placeholder="게시판 검색..." />

    <Button @click="goToCreatePost">
      <PlusIcon class="w-5 h-5 text-white" /> 게시글 작성
    </Button>
  </section>

  <!-- 새 게시글 알림 -->
  <section
    v-if="newPostsCount > 0 && showNewButton"
    class="fixed z-50 -translate-x-1/2 top-20 left-1/2"
  >
    <Button
      variant="outline"
      class="rounded-full px-5 py-2.5 text-sm font-semibold border border-primary/30 bg-background/70 backdrop-blur shadow-lg shadow-black/10 ring-1 ring-primary/20 hover:bg-background/90 hover:border-primary/50 hover:ring-primary/35 hover:shadow-xl active:scale-[0.98] transition-all"
      @click="showLatestPosts"
    >
      새 게시글 {{ newPostsCount }}개 보기
    </Button>
  </section>

  <!-- 포스트 리스트 -->
  <section>
    <!--  로딩 중 -->
    <div v-if="status === 'loading'" class="py-10 text-center">
      불러오는 중...
    </div>

    <!-- 게시글 리스트 -->
    <div
      v-for="(page, pageIndex) in data?.pages"
      :key="pageIndex"
      class="grid grid-cols-1 gap-4"
    >
      <!-- posts 배열 렌더링 -->
      <PostCard v-for="post in page.posts" :key="post.postId" :post="post" />
    </div>

    <!-- 무한 스크롤 트리거 -->
    <div ref="loadMoreObserverTarget" class="h-10"></div>

    <!-- 마지막 페이지 표시 -->
    <div
      v-if="status === 'success' && !hasNextPage"
      class="py-6 mt-4 text-center text-muted-foreground"
    >
      더 이상 게시글이 없습니다.
    </div>

    <!-- 상단 이동 플로팅 버튼 -->
    <button
      v-if="showScrollTopButton"
      type="button"
      aria-label="상단으로 이동"
      @click="scrollToTop"
      class="fixed z-40 flex items-center justify-center w-12 h-12 text-white rounded-full bottom-24 right-6 sm:right-8 sm:bottom-28 bg-primary"
    >
      <ArrowUpIcon class="w-6 h-6" />
    </button>
  </section>
</template>

<script setup>
import ViewHeader from "../components/common/ViewHeader.vue";
import SearchInput from "../components/common/SearchInput.vue";
import Button from "../components/ui/Button.vue";
import { PlusIcon } from "lucide-vue-next";
import { ArrowUpIcon } from "@heroicons/vue/24/solid";
import PostCard from "@/components/community/PostCard";
import { useInfiniteQuery, useQuery } from "@tanstack/vue-query";
import { fetchPosts } from "../api/postApi.js";
import { computed, onMounted, ref, onUnmounted, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "@/composables/useToast";
import { useAuthStore } from "@/stores/authStore";

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const { toast } = useToast();

// 1. 쿼리에서 목록 상태 복원 (스크롤 복원 위치)
const page = computed(() => Number(route.query.page ?? 0));
const keyword = computed(() => route.query.keyword ?? "");

// 게시글 목록 경로: data.pages[0].content.posts
// 2. 게시글 목록 조회
const {
  data,
  fetchNextPage,
  hasNextPage,
  isFetchingNextPage,
  status,
  refetch,
} = useInfiniteQuery({
  queryKey: ["posts"],
  queryFn: fetchPosts,
  staleTime: 0, // 항상 stale 처리  -> 최신 데이터 가져오기
  refetchOnWindowFocus: true, // 탭 전환할 때 자동 리패치
  cacheTime: 30000, // 30초 후 캐시 제거
  getNextPageParam: (lastPage) => {
    return lastPage.hasNext ? lastPage.nextPage : undefined;
  },
  // onSuccess: (data) => {
  //   console.log(JSON.parse(JSON.stringify(data)));
  // },
});

// 스크롤 복원용 상태
const pendingScroll = ref(null);
const hasRestoredScroll = ref(false);
const isRestoringScroll = ref(false);

// 3. scroll 위치 복원
onMounted(() => {
  const stored = sessionStorage.getItem("communityScroll");
  if (stored) {
    try {
      pendingScroll.value = JSON.parse(stored);
    } catch (err) {
      console.error("Failed to parse scroll state", err);
    }
  }
});

// 최신 게시글 감지용 쿼리 (첫 페이지만 주기적으로 조회)
const { data: latestPageData } = useQuery({
  queryKey: ["posts", "latest"],
  queryFn: () => fetchPosts({ pageParam: 0 }),
  refetchInterval: 10000, // 10초마다 새 게시글 여부 확인
  refetchOnWindowFocus: true,
  staleTime: 0,
});

// 현재 화면에서 맨 위 게시글 id
const visibleTopPostId = computed(
  () => data.value?.pages?.[0]?.posts?.[0]?.postId ?? null
);

// 최신 페이지 기준 새 게시글 개수 계산
const newPostsCount = computed(() => {
  const latestPosts = latestPageData.value?.posts || [];
  const currentTopId = visibleTopPostId.value;

  if (!currentTopId) return 0;
  let count = 0;

  for (const post of latestPosts) {
    if (post.postId === currentTopId) break;
    count += 1;
  }

  return count;
});

// 무한 스크롤 옵저버 대상
const loadMoreObserverTarget = ref(null);
let observer = null;

// 검색 영역 가시성 체크
const searchAreaRef = ref(null);
let searchAreaObserver = null;
const showNewButton = ref(false);

// 검색 박스 체크
const showScrollTopButton = ref(false);

// IntersectionObserver 설정
onMounted(() => {
  observer = new IntersectionObserver(
    (entries) => {
      const entry = entries[0];
      if (entry.isIntersecting && hasNextPage.value) {
        fetchNextPage();
      }
    },
    { threshold: 1.0 }
  );

  if (loadMoreObserverTarget.value) {
    observer.observe(loadMoreObserverTarget.value);
  }

  // 검색 영역이 화면에서 사라지면 새 게시글 버튼 노출
  searchAreaObserver = new IntersectionObserver(
    ([entry]) => {
      const hidden = !entry.isIntersecting;
      showNewButton.value = hidden;
      showScrollTopButton.value = hidden;
    },
    { threshold: 0 }
  );

  if (searchAreaRef.value) {
    searchAreaObserver.observe(searchAreaRef.value);
  }
});

// 옵저버 해제
onUnmounted(() => {
  if (observer && loadMoreObserverTarget.value) {
    observer.unobserve(loadMoreObserverTarget.value);
  }
  if (searchAreaObserver && searchAreaRef.value) {
    searchAreaObserver.unobserve(searchAreaRef.value);
  }
});

const goToCreatePost = () => {
  if (!authStore.isLoggedIn) {
    toast({
      title: "로그인이 필요한 서비스입니다.",
      description: "게시글을 작성하려면 먼저 로그인해주세요.",
    });
    router.push({
      name: "signin",
      query: { redirect: router.resolve({ name: "postCreate" }).path },
    });
    return;
  }
  router.push({ name: "postCreate" });
};

// 새 게시글 보기 버튼 클릭 시 최신 데이터로 갱신 + 상단 이동
const showLatestPosts = async () => {
  await refetch();
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 상단 이동
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 데이터 로드 후 저장된 스크롤 위치로 복원
const restoreScrollIfNeeded = async () => {
  if (
    hasRestoredScroll.value ||
    isRestoringScroll.value ||
    !pendingScroll.value
  )
    return;

  isRestoringScroll.value = true;
  await nextTick();

  let targetEl = null;
  if (pendingScroll.value.postId) {
    targetEl = document.querySelector(
      `[data-post-id="${pendingScroll.value.postId}"]`
    );
  }

  // 대상 게시글이 아직 렌더되지 않았으면 다음 페이지 불러오기
  if (!targetEl && hasNextPage.value && !isFetchingNextPage.value) {
    isRestoringScroll.value = false;
    await fetchNextPage();
    return;
  }

  const targetTop =
    pendingScroll.value.scrollY ??
    (targetEl ? targetEl.getBoundingClientRect().top + window.scrollY - 60 : 0);

  window.scrollTo({ top: targetTop, behavior: "auto" });
  hasRestoredScroll.value = true;
  isRestoringScroll.value = false;
  sessionStorage.removeItem("communityScroll");
};

watch(
  () => [data.value?.pages?.length, isFetchingNextPage.value],
  () => {
    restoreScrollIfNeeded();
  }
);
</script>
