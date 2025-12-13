// post 도메인 관련 API

import httpClient from "./httpClient";

// [C] 게시글 생성
export const createPostApi = async (payload) => {
  // payload: { userId, bookId?, isbn?, postTitle, postContent }
  const body = { ...payload };

  // 빈 값은 전송 X
  Object.keys(body).forEach((key) => {
    if (body[key] === "" || body[key] === null || body[key] === undefined) {
      delete body[key];
    }
  });

  const res = await httpClient.post("/posts", body);
  return res.data;
};

// [R] 게시글 목록 조회 (페이징)
export const fetchPosts = async ({ pageParam = 0 }) => {
  const res = await httpClient.get("/posts", {
    params: {
      page: pageParam,
      size: 10,
    },
  });

  // 백엔드 응답 (PostPageResponseDto) 구조
  const responseData = res.data;

  // useInfiniteQuery에 필요한 데이터를 반환
  // 데이터 경로: responseData.content.posts
  return {
    posts: responseData.content.posts,
    nextPage: responseData.nextPage, // 다음 페이지 번호 (백엔드에서 제공)
    hasNext: responseData.hasNext, // 다음 페이지 존재 여부 (백엔드에서 제공)
  };
};
