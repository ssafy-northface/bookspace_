// src/stores/bookStore.js
import { defineStore } from "pinia";
import { fetchDefaultBooks, searchBooks } from "@/api/bookApi";

export const useBookStore = defineStore("book", {
  state: () => ({
    books: [],
    loading: false,
    error: null,

    // 화면 상태 유지용
    lastQuery: "",
    lastSearchType: "title", // title|author|publisher|isbn
    lastSort: "latest",      // latest|popular|accuracy
    lastDefaultType: "bestseller", // bestseller|new
    hasSearched: false,
  }),

  getters: {
    isEmpty(state) {
      return !state.loading && state.books.length === 0;
    },
  },

  actions: {
    async loadDefault({ type = "bestseller" } = {}) {
      this.loading = true;
      this.error = null;

      try {
        this.lastDefaultType = type;
        this.hasSearched = false;

        const data = await fetchDefaultBooks({ type });
        this.books = Array.isArray(data) ? data : [];
      } catch (e) {
        this.error =
          e?.response?.data?.message ||
          "기본 도서 목록을 불러오지 못했습니다.";
        this.books = [];
      } finally {
        this.loading = false;
      }
    },

    async search({ query, type = "title", sort = "latest" } = {}) {
      this.loading = true;
      this.error = null;

      try {
        this.lastQuery = query ?? "";
        this.lastSearchType = type;
        this.lastSort = sort;
        this.hasSearched = true;

        const data = await searchBooks({ query, type, sort });
        this.books = Array.isArray(data) ? data : [];
      } catch (e) {
        this.error =
          e?.response?.data?.message ||
          "도서 검색에 실패했습니다.";
        this.books = [];
      } finally {
        this.loading = false;
      }
    },

    // 검색어 지우고 기본목록으로 되돌릴 때
    async resetToDefault() {
      await this.loadDefault({ type: this.lastDefaultType || "bestseller" });
    },
  },
});
