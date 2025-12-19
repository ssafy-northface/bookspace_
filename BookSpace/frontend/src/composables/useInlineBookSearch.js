import { ref } from "vue";
import { searchBooks } from "@/api/bookApi";

/**
 *  게시물 작성 / 수정 뷰에서 책 검색 전용
 */
export function useInlineBookSearch() {
  const results = ref([]);
  const loading = ref(false);
  const error = ref("");
  const lastQuery = ref("");
  const lastType = ref("title");

  const search = async ({ query, type = "title" } = {}) => {
    const trimmed = (query ?? "").trim(); // 검색어 정규화
    lastQuery.value = trimmed;
    lastType.value = type ?? "title";
    error.value = "";

    // 검색어가 없으면 결과 초기화
    if (!trimmed) {
      results.value = [];
      return;
    }

    loading.value = true;
    try {
      // 서버 책 검색 API 호출
      const data = await searchBooks({
        query: trimmed,
        type: lastType.value,
      });
      results.value = Array.isArray(data) ? data : [];
    } catch (e) {
      error.value =
        e?.response?.data?.message || "검색 결과를 불러오지 못했습니다.";
      results.value = [];
    } finally {
      loading.value = false;
    }
  };

  //  검색 결과 초기화
  const clear = () => {
    results.value = [];
    error.value = "";
  };

  return {
    results,
    loading,
    error,
    lastQuery,
    lastType,
    search,
    clear,
  };
}
