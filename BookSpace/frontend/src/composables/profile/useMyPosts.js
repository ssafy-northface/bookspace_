import { computed } from "vue";
import { useQuery } from "@tanstack/vue-query";
import { fetchMyPostsApi } from "@/api/postApi";
import { useAuthStore } from "@/stores/authStore";

export function useMyPosts() {
  const authStore = useAuthStore();

  return useQuery({
    queryKey: ["my-posts"],
    enabled: computed(() => authStore.isLoggedIn),
    queryFn: async () => {
      const res = await fetchMyPostsApi();
      const raw = res?.data ?? res;

      const list =
        Array.isArray(raw?.content?.posts) ? raw.content.posts :
        Array.isArray(raw?.content) ? raw.content :
        Array.isArray(raw?.posts) ? raw.posts :
        Array.isArray(raw) ? raw : [];

      return list.map((post) => ({
        postId: post.postId ?? post.id,
        userNickName:
          post.userNickName ??
          post.userNickname ??
          post.userNick ??
          post.nickname ??
          post.authorName ??
          "",
        userProfileImage: post.userProfileImage ?? post.profileImage ?? "",
        postDate: post.postDate ?? post.createdAt ?? post.created_at ?? post.date,
        date: post.postDate ?? post.createdAt ?? post.created_at ?? post.date,
        postTitle: post.postTitle ?? post.title ?? "제목 없음",
        postContent: post.postContent ?? post.content ?? "",
        title: post.postTitle ?? post.title ?? "제목 없음",
        content: post.postContent ?? post.content ?? "",
        likeCount: post.likeCount ?? post.likes ?? 0,
        liked: post.liked ?? post.isLiked ?? false,
        commentCount: post.commentCount ?? post.comments ?? post.commentCnt ?? 0,
        postViewCnt: post.postViewCnt ?? post.viewCnt ?? post.views ?? 0,
        views: post.postViewCnt ?? post.viewCnt ?? post.views ?? 0,
        bookImageUrl: post.bookImageUrl ?? post.bookImage ?? post.cover ?? "",
        bookImage: post.bookImage ?? post.bookImageUrl ?? post.cover ?? "",
        bookTitle: post.bookTitle ?? post.titleOfBook ?? post.book?.title ?? "",
        bookAuthor: post.bookAuthor ?? post.book?.author ?? "",
      }));
    },
    staleTime: 1000 * 30,
  });
}
