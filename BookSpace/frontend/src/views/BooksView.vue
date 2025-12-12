<template>
    <ViewHeader
        title = "도서 목록"
        description="다양한 책들을 탐색해보세요"
        align="left"
        size="lg"
    />
    <div>

    <!-- 검색 + 정렬  -->
    <BookSearchInput @search="handleSearch" @change-sort="handleSort" ></BookSearchInput>
    
    <!-- 도서 목록 -->
    <!-- <BookList :books="bookStore.books"></BookList> -->
    <!-- 테스트용 -->
    <!-- 도서 목록 -->
    <BookList
      v-if="books.length"
      :books="books"
    />

    <!-- 검색 결과 없음 -->
    <div
      v-else
      class="flex min-h-[160px] flex-col items-center justify-center rounded-xl border border-dashed text-sm text-muted-foreground mt-6"
    >
      검색 결과가 없습니다.
      <span class="mt-1">검색어를 바꿔보거나 다른 카테고리를 선택해보세요.</span>
    </div>


  </div>

</template>

<script setup>
import { ref } from 'vue';
import ViewHeader from '../components/common/ViewHeader.vue';
import BookList from '../components/book/BookList.vue';
import BookSearchInput from '../components/book/BookSearchInput.vue';


const handleSearch = (payload) => {
    console.log(payload)
}

const handleSort = (newSort) => {
    console.log(newSort)
}


// ✅ 일단은 로컬 상태로만 테스트
const hasSearched = ref(false);

// 테스트용 책
const books = ref([
  {
    bookId: 1,
    bookTitle: "어린 왕자",
    bookAuthor: "생텍쥐페리",
    bookImageURL:
      "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f",
  },
  {
    bookId: 2,
    bookTitle: "데미안",
    bookAuthor: "헤르만 헤세",
    bookImageURL:
      "https://images.unsplash.com/photo-1519681393784-d120267933ba",
  },
  {
    bookId: 3,
    bookTitle: "1984",
    bookAuthor: "조지 오웰",
    bookImageURL:
      "https://images.unsplash.com/photo-1544936207-507437cdaece",
  },
  {
    bookId: 4,
    bookTitle: "해리 포터와 마법사의 돌",
    bookAuthor: "J.K. 롤링",
    bookImageURL:
      "https://images.unsplash.com/photo-1532012197267-da84d127e765",
  },
  {
    bookId: 5,
    bookTitle: "연금술사",
    bookAuthor: "파울로 코엘료",
    bookImageURL:
      "https://images.unsplash.com/photo-1507842217343-583bb7270b66",
  },
  {
    bookId: 6,
    bookTitle:
      "나미야 잡화점의 기적인데 책 제목 길이가 길어지면 어떻게 되는지",
    bookAuthor: "히가시노 게이고",
    bookImageURL:
      "https://images.unsplash.com/photo-1516979187457-637abb4f9353",
  },
  {
    bookId: 7,
    bookTitle: "인간 실격",
    bookAuthor: "다자이 오사무",
    bookImageURL:
      "https://images.unsplash.com/photo-1529651737248-dad5e287768e",
  },
  {
    bookId: 8,
    bookTitle: "죽음",
    bookAuthor: "베르나르 베르베르",
    bookImageURL:
      "https://images.unsplash.com/photo-1528209392022-540d8ab3a5e1",
  },
  {
    bookId: 9,
    bookTitle: "메밀꽃 필 무렵",
    bookAuthor: "이효석",
    bookImageURL:
      "https://images.unsplash.com/photo-1519681393784-d120267933ba",
  },
  {
    bookId: 10,
    bookTitle: "불편한 편의점",
    bookAuthor: "김호연",
    bookImageURL:
      "https://images.unsplash.com/photo-1526318472351-bc6fa98d1c04",
  },
]);

   
</script>

<style scoped>

</style>