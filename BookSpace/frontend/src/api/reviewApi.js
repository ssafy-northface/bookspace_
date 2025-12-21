import httpClient from "./httpClient";

/**
 * 리뷰 API
 * - 조회: 비로그인 가능
 * - 작성/수정/삭제: 로그인 필요
 * - 별점: 0.5 단위
 * - 리뷰 작성은 isbn 기반
 */

/**
 * 리뷰 목록 조회 (도서 기준)
 * GET /reviews?bookId=10&sort=latest
 */
export const getReviewsByBookIdApi = (bookId, sort = "latest") => {
  return httpClient
    .get("/reviews", {
      params: { bookId, sort },
    })
    .then((res) => res.data); // List<ReviewResponseDto>
};

/**
 * 리뷰 목록 조회 (by user))
 * GET /reviews?userId=3&sort=latest
 */
export const getReviewsByUserIdApi = (userId, sort = "latest") => {
  return httpClient
    .get("/reviews", {
      params: { userId, sort },
    })
    .then((res) => res.data); // List<ReviewResponseDto>
};

/**
 * 로그인 유저 리뷰 목록 조회 (profile -> myReviews)
 * GET / reviews/me
 * @param {*} reviewId
 * @returns
 */
export const fetchMyReviewsApi = () => {
  return httpClient.get("/reviews/me").then((res) => res.data);
};

/**
 * 리뷰 단건 조회
 * GET /reviews/{reviewId}
 */
export const getReviewByIdApi = (reviewId) => {
  return httpClient.get(`/reviews/${reviewId}`).then((res) => res.data);
};

/**
 * 리뷰 작성
 * POST /reviews
 * payload: { isbn, reviewRating, reviewContent }
 */
export const createReviewApi = (payload) => {
  return httpClient.post("/reviews", payload).then((res) => res.data);
};

/**
 * 리뷰 수정 (작성자 본인만 가능)
 * PUT /reviews/{reviewId}
 * payload: { reviewRating, reviewContent }
 */
export const updateReviewApi = (reviewId, payload) => {
  return httpClient
    .put(`/reviews/${reviewId}`, payload)
    .then((res) => res.data);
};

/**
 * 리뷰 삭제 (작성자 본인만 가능)
 * DELETE /reviews/{reviewId}
 */
export const deleteReviewApi = (reviewId) => {
  return httpClient.delete(`/reviews/${reviewId}`).then((res) => res.data);
};
