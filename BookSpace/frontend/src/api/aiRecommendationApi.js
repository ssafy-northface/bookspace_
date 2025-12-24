// src/api/aiRecommendationApi.js
import httpClient from "./httpClient";

const BASE = "/api/recommend";

/**
 * AI 도서 추천 요청
 * POST /api/recommend
 * @param {string} userRequest - 사용자가 입력한 감정이나 상황
 * @returns {Promise<AiRecommendationResponseDto>}
 */
export const getAiRecommendation = async (userRequest) => {
  if (!userRequest || !userRequest.trim()) {
    throw new Error("userRequest is required");
  }

  const res = await httpClient.post(BASE, {
    userRequest: userRequest.trim(),
  });

  return res.data; // AiRecommendationResponseDto
};
