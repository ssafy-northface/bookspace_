import httpClient from "./httpClient";

/**
 * Wish(찜) API
 * - POST   /wishes          : 찜 추가 (body: { isbn })
 * - DELETE /wishes/{bookId} : 찜 해제
 * - GET    /wishes          : 내 찜 목록 조회
 */
export const wishApi = {
  /**
   * 찜 추가 (isbn 기반)
   * @param {string} isbn
   */
  addWish(isbn) {
    return httpClient.post("/wishes", { isbn });
  },

  /**
   * 찜 해제 (bookId 기반)
   * @param {number|string} bookId
   */
  removeWish(bookId) {
    return httpClient.delete(`/wishes/${bookId}`);
  },

  /**
   * 내 찜 목록 조회
   */
  getMyWishes() {
    return httpClient.get("/wishes");
  },
};

export default wishApi;