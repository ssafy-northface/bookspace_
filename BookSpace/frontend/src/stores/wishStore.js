import { defineStore } from "pinia";
import wishApi from "@/api/wishApi";

/**
 * Wish(찜) Store
 * - 찜 추가는 isbn 필요, 찜 해제는 bookId 필요 (백엔드 스펙)
 *    => 프론트는 isbn 보내고 -> 백엔드가 isbn으로 db확인
 *    -> 있으면 기존 bookId, 없으면 저장 후 새 bookId 생성 -> 해당 bookId로 wish를 저장
 * - 낙관적 업데이트: UI 먼저 반영 -> 실패 시 롤백
 */

export const useWishStore = defineStore("wish", {
  state: () => ({
    // { [isbn]: boolean }
    isWishedByIsbn: {},

    // { [isbn]: boolean } 중복 클릭 방지
    // 찜 버튼 중복 클릭 방지를 위한 로딩 상태
    // 낙관적 업데이트 이후 서버 응답을 기다리는 상태
    // UI는 즉시 반영되며, 서버 요청 완료시 해제됨
    loadingByIsbn: {},
  }),

  getters: {
    isWished: (state) => (isbn) => {
      if (!isbn) return false;
      return !!state.isWishedByIsbn[isbn];
    },
    isLoading: (state) => (isbn) => {
      if (!isbn) return false;
      return !!state.loadingByIsbn[isbn];
    },
  },

  actions: {
    setIsWished(isbn, value) {
      if (!isbn) return;
      this.isWishedByIsbn[isbn] = !!value;
    },

    /**
     * 찜 토글 (낙관적 업데이트)
     *
     * @param {Object} params
     * @param {string} params.isbn   - 항상 필요
     * @param {number|null} params.bookId - 찜 해제시 필요
     */
    async toggleWish({ isbn, bookId }) {
      if (!isbn) throw new Error("isbn is required");

      if (this.loadingByIsbn[isbn]) return;

      const prev = !!this.isWishedByIsbn[isbn];
      const next = !prev;

      // 1. 로딩 ON
      this.loadingByIsbn[isbn] = true;

      // 2. 낙관적 업데이트
      this.isWishedByIsbn[isbn] = next;

      try {
        if (next) {
          // 찜 추가 → isbn만 보내면 서버가 bookId 보장
          await wishApi.addWish(isbn);
        } else {
          // 찜 해제 → bookId 필요
          if (!bookId) {
            throw new Error("bookId is required to remove wish");
          }
          await wishApi.removeWish(bookId);
        }
      } catch (err) {
        // 3. 실패 시 롤백
        this.isWishedByIsbn[isbn] = prev;
        throw err;
      } finally {
        // 4. 로딩 OFF
        this.loadingByIsbn[isbn] = false;
      }
    },

    /**
     * 로그아웃 시 초기화
     */
    reset() {
      this.isWishedByIsbn = {};
      this.loadingByIsbn = {};
    },
  },
});