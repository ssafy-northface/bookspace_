// post 도메인 관련 API

import httpClient from "./httpClient";

/**
 * [C] 게시글 생성
 * @param {*} payload
 */
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

/**
 *  [R] 게시글 전체 조회 (페이징)
 * @param {*} param0
 * @returns
 */
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

/**
 * [R] 게시글 단건 조회
 * @param {*} postId
 * @returns
 */
export const fetchPostDetail = async (postId) => {
  const res = await httpClient.get(`/posts/${postId}`);
  return res.data;
};

/**
 * [U] 게시글 수정
 * @param {*} postId
 * @param {*} payload
 * @returns
 */
export const updatePostApi = async (postId, payload) => {
  const body = { ...payload };

  Object.keys(body).forEach((key) => {
    if (body[key] === "" || body[key] === null || body[key] === undefined) {
      delete body[key];
    }
  });

  const res = await httpClient.put(`/posts/${postId}`, body);
  return res.data;
};

/**
 * [D] 게시글 삭제
 * @param {*} postId
 * @returns
 */
export const deletePostApi = async (postId) => {
  const res = await httpClient.delete(`/posts/${postId}`);
  return res.data;
};

// -------------------------- 게시글 좋아요 --------------------------
/**
 * 게시글 좋아요
 * @param {*} postId
 * @returns
 */
export const postLikes = async (postId) => {
  console.log("post likes");
  const res = await httpClient.post(`/posts/${postId}/like`);
  return res.data;
};

/**
 * 게시글 좋아요 취소
 */
export const deleteLikes = async (postId) => {
  console.log("post likes non nonono");
  const res = await httpClient.delete(`/posts/${postId}/like`);
  return res.data;
};

// -------------------------- 게시글 댓글  --------------------------
/**
 * [C] 댓글 작성 (parentCommentId 없으면 일반 댓글)
 */
export const createCommentApi = async (postId, payload) => {
  const body = { ...payload };
  Object.keys(body).forEach((key) => {
    if (body[key] === "" || body[key] === null || body[key] === undefined) {
      delete body[key];
    }
  });

  const res = await httpClient.post(`/posts/${postId}/comments`, body);
  return res.data;
};

/**
 * [R] 댓글 목록 조회 (대댓글 포함)
 * @param {*} postId
 * @returns
 */
export const fetchCommentsApi = async (postId) => {
  const res = await httpClient.get(`/posts/${postId}/comments`);
  return res.data;
};

/**
 * [U] 댓글 수정
 */
export const updateCommentApi = async (commentId, payload) => {
  const body = { ...payload };
  Object.keys(body).forEach((key) => {
    if (body[key] === "" || body[key] === null || body[key] === undefined) {
      delete body[key];
    }
  });

  const res = await httpClient.put(`/comments/${commentId}`, body);
  return res.data;
};

/**
 * [D] 댓글 삭제
 */
export const deleteCommentApi = async (commentId) => {
  const res = await httpClient.delete(`/comments/${commentId}`);
  return res.data;
};
