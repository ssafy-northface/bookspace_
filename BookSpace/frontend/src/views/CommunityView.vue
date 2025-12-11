// TODO 스타일 수정
<template>
  <ViewHeader
    title="커뮤니티"
    description="커뮤니티 게시판 목록을 확인하세요."
    align="left"
    size="lg"
  />

  <!-- 검색인풋 + 게시글 작성 버튼 -->
  <section class="flex items-center justify-between gap-4 mt-6 mb-4">
    <SearchInput placeholder="게시판 검색..." />

    <Button><PlusIcon class="w-5 h-5 text-white" /> 게시글 작성</Button>
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
  </section>
</template>

<script setup>
import ViewHeader from "../components/common/ViewHeader.vue";
import SearchInput from "../components/common/SearchInput.vue";
import Button from "../components/ui/Button.vue";
import { PlusIcon } from "lucide-vue-next";
import PostCard from "@/components/community/PostCard";
import { useInfiniteQuery } from "@tanstack/vue-query";
import { fetchPosts } from "../api/postApi";
import { onMounted, watch, ref, onUnmounted } from "vue";

// 게시글 목록 경로: data.pages[0].content.posts
const { data, fetchNextPage, hasNextPage, isFetchingNextPage, status } =
  useInfiniteQuery({
    queryKey: ["posts"],
    queryFn: fetchPosts,
    staleTime: 5000, // 5초 동안만 fresh 상태 유지
    cacheTime: 30000, // 30초 후 캐시 제거
    refetchOnWindowFocus: false, // 탭 전환할 때 자동 리패치 방지
    getNextPageParam: (lastPage) => {
      return lastPage.hasNext ? lastPage.nextPage : undefined;
    },
    // onSuccess: (data) => {
    //   console.log(JSON.parse(JSON.stringify(data)));
    // },
  });

// 무한 스크롤 옵저버 대상
const loadMoreObserverTarget = ref(null);
let observer = null;

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
});

// 옵저버 해제
onUnmounted(() => {
  if (observer && loadMoreObserverTarget.value) {
    observer.unobserve(loadMoreObserverTarget.value);
  }
});
</script>
