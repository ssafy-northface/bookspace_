<template>
    <!-- 검색기준 + 검색창  -->
    <section class="flex items-center gap-2 mb-4">
        <!-- 책제목, 저자, 출판사 선택 -->
        <select
            v-model="type"
            class="
                h-9 rounded-md border border-input bg-background px-2 text-xs md:text-sm
                focus-visible:ring-[3px]
                focus-visible:ring-ring/50
                focus-visible:border-ring
                "
            >
            <option value="title">책제목</option>
            <option value="author">저자</option>
            <option value="publisher">출판사</option>
        </select>

        <!-- 검색창 -->
        <SearchInput 
            v-model="searchQuery" 
            placeholder="검색어를 입력하세요" 
            @search="onSearch"
        />
    </section> 

    <!-- 정렬기준 선택 -->
    <section class="mt-6 mb-6 flex justify-end gap-4">
        <span
            @click="setSort('latest')"
            :class="[
                'cursor-pointer text-sm',
                sort === 'latest' ? 'font-extrabold text-primary' : 'font-normal'
            ]"
        >
            출간일순
        </span>


        <span
            @click="setSort('popular')"
            :class="[
                'cursor-pointer text-sm',
                sort === 'popular' ? 'font-extrabold text-primary' : 'font-normal'
            ]"
        >
            인기순
        </span>
    </section>
</template>

<script setup>
import { ref } from 'vue';
import SearchInput from '../common/SearchInput.vue';

const type = ref("title");
const sort = ref("latest");
const searchQuery = ref("");

// 부모 BooksView에게 전달할 이벤트들
const emit = defineEmits(["search", "changeSort"]);

// 엔터 눌렀을 때
const onSearch = () => {
  emit("search",{
    query: searchQuery.value,
    type: type.value,
    sort: sort.value,
  })
}

// 정렬 기준 버튼 클릭했을 때
const setSort = (value) => {
  sort.value = value
  emit("changeSort", value)
}

</script>

<style scoped>

</style>