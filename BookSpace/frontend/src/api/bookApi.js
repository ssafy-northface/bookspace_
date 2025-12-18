// src/api/bookApi.js
import httpClient from "./httpClient"; 

const BASE = "/books";

/**
 * 기본 도서 목록 (검색어 없이)
 * GET /books?type=bestseller|new
 */
export const fetchDefaultBooks = async ({ type = "bestseller" } = {}) => {
  const res = await httpClient.get(BASE, {
    params: { type },
  });
  return res.data; // List<BookSearchResponseDto>
};

/**
 * 알라딘 기반 검색
 * GET /books/search?query=...&type=...&sort=...
 */
export const searchBooks = async ({
  query,
  type = "title",
  sort = "latest",
} = {}) => {
  if (!query || !query.trim()) return []; // 빈 검색어 방어

  const res = await httpClient.get(`${BASE}/search`, {
    params: {
      query: query.trim(),
      type,
      sort,
    },
  });
  return res.data; // List<BookSearchResponseDto>
};

/**
 * 도서 상세조회 (ISBN 기반)
 * GET /books/detail/{isbn}
 * -> BookDetailResponseDto
 */
export const fetchBookDetailByIsbn = async (isbn) => {
  if (!isbn || !String(isbn).trim()) {
    throw new Error("isbn is required");
  }
  const res = await httpClient.get(
    `${BASE}/detail/${encodeURIComponent(String(isbn).trim())}`
  );
  return res.data; // BookDetailResponseDto
};

/**
 * ISBN 단건 조회 (bookId 확보 목적)
 * GET /books/{isbn}
 * 현재 백엔드가 return null 이라 프론트에서 바로 쓰면 깨짐.
 */
export const fetchBookByIsbn = async (isbn) => {
  const res = await httpClient.get(`${BASE}/${encodeURIComponent(isbn)}`);
  return res.data; // BookSearchResponseDto (예정)
};
