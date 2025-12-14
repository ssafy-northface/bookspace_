import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { fetchDefaultBooks, searchBooks } from "@/api/bookApi";

export function useBookSearch() {
  const route = useRoute();
  const router = useRouter();

  // URL 상태
  const query = computed(() => (route.query.query ?? "").toString().trim());
  const type = computed(() => (route.query.type ?? "title").toString());
  const sort = computed(() => (route.query.sort ?? "latest").toString()); // 디폴트 latest

  const isSearchMode = computed(() => query.value.length > 0);

  // 데이터 상태
  const books = ref([]);
  const loading = ref(false);
  const error = ref("");

  // URL 변화에 따른 자동 fetch
  watch(
    () => [query.value, type.value, sort.value],
    async ([q, t, s]) => {
      loading.value = true;
      error.value = "";

      try {
        if (!q) {
          books.value = await fetchDefaultBooks({ type: "bestseller" });
          return;
        }

        books.value = await searchBooks({
          query: q,
          type: t,
          sort: s,
        });
      } catch (e) {
        error.value = e?.response?.data?.message || "검색 결과를 불러오지 못했습니다.";
        books.value = [];
      } finally {
        loading.value = false;
      }
    },
    { immediate: true }
  );

  // 검색 확정
  const search = ({ query, type }) => {
    const trimmed = (query ?? "").trim();
    if (!trimmed) {
      router.push({ path: "/books" });
      return;
    }

    router.push({
      path: "/books",
      query: {
        query: trimmed,
        type: type ?? "title",
        sort: "latest",
      },
    });
  };

  // 정렬 변경
  const changeSort = (newSort) => {
    router.push({
      query: {
        ...route.query,
        sort: newSort,
      },
    });
  };

  return {
    // state
    query,
    type,
    sort,
    isSearchMode,

    // data
    books,
    loading,
    error,

    // actions
    search,
    changeSort,
  };
}
