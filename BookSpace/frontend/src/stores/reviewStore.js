import { defineStore } from "pinia";
import {
  getReviewsByBookIdApi,
  getReviewsByUserIdApi,
  getReviewByIdApi,
  createReviewApi,
  updateReviewApi,
  deleteReviewApi,
} from "@/api/reviewApi";

export const useReviewStore = defineStore("review", {
  state: () => ({
    // 도서 상세: bookId 기준 리뷰 목록
    reviewsByBook: [],
    loadingByBook: false,

    // 마이페이지: userId 기준 리뷰 목록
    reviewsByUser: [],
    loadingByUser: false,

    // 리뷰 단건 (필요 시)
    reviewDetail: null,
    loadingDetail: false,

    // 작성 / 수정 / 삭제 공통
    submitting: false,
  }),

  getters: {
    reviewCountByBook(state) {
      return state.reviewsByBook.length;
    },
    reviewCountByUser(state) {
      return state.reviewsByUser.length;
    },
  },

  actions: {
    // 1. 리뷰 목록 조회 by bookId
    async fetchReviewsByBookId(bookId, sort = "latest") {
      this.loadingByBook = true;
      try {
        const data = await getReviewsByBookIdApi(bookId, sort);
        this.reviewsByBook = Array.isArray(data) ? data : [];
        return this.reviewsByBook;
      } finally {
        this.loadingByBook = false;
      }
    },

    // 2. 리뷰 목록 조회 by userId
    async fetchReviewsByUserId(userId, sort = "latest") {
      this.loadingByUser = true;
      try {
        const data = await getReviewsByUserIdApi(userId, sort);
        this.reviewsByUser = Array.isArray(data) ? data : [];
        return this.reviewsByUser;
      } finally {
        this.loadingByUser = false;
      }
    },

    // 3. 리뷰 단건 조회 by reviewId
    async fetchReviewById(reviewId) {
      this.loadingDetail = true;
      try {
        const data = await getReviewByIdApi(reviewId);
        this.reviewDetail = data ?? null;
        return this.reviewDetail;
      } finally {
        this.loadingDetail = false;
      }
    },

    // 4. 리뷰 작성
    async createReview(payload, { refreshBookId, sort = "latest" } = {}) {
      this.submitting = true;
      try {
        const data = await createReviewApi(payload);

        // 작성 후 도서 리뷰 목록 갱신
        if (refreshBookId) {
          await this.fetchReviewsByBookId(refreshBookId, sort);
        }

        return data;
      } finally {
        this.submitting = false;
      }
    },

    // 5. 리뷰 수정
    async updateReview(
      reviewId,
      payload,
      { refreshBookId, refreshUserId, sort = "latest" } = {}
    ) {
      this.submitting = true;
      try {
        const data = await updateReviewApi(reviewId, payload);

        if (refreshBookId) {
          await this.fetchReviewsByBookId(refreshBookId, sort);
        }
        if (refreshUserId) {
          await this.fetchReviewsByUserId(refreshUserId, sort);
        }

        return data;
      } finally {
        this.submitting = false;
      }
    },

    // 6. 리뷰 삭제
    async deleteReview(
      reviewId,
      { refreshBookId, refreshUserId, sort = "latest" } = {}
    ) {
      this.submitting = true;
      try {
        const data = await deleteReviewApi(reviewId);

        if (refreshBookId) {
          await this.fetchReviewsByBookId(refreshBookId, sort);
        }
        if (refreshUserId) {
          await this.fetchReviewsByUserId(refreshUserId, sort);
        }

        return data;
      } finally {
        this.submitting = false;
      }
    },

    // 7. 상태 초기화
    reset() {
      this.reviewsByBook = [];
      this.loadingByBook = false;

      this.reviewsByUser = [];
      this.loadingByUser = false;

      this.reviewDetail = null;
      this.loadingDetail = false;

      this.submitting = false;
    },
  },
});