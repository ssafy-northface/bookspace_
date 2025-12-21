// vue router 세팅 파일
import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

// layout
import DefaultLayout from "@/components/layout/DefaultLayout.vue";
// views
import HomeView from "@/views/HomeView";
import AiRecommendView from "@/views/AiRecommendView";
import BooksView from "@/views/BooksView";
import BookDetailView from "@/views/BookDetailView";
import CommunityView from "@/views/CommunityView";
import PostDetailView from "@/views/PostDetailView";
import ProfileView from "@/views/ProfileView";
import PostCreateUpdateView from "@/views/PostCreateUpdateView";
import SignInView from "../views/SignInView";
import SignupView from "@/views/SignupView";

const router = createRouter({
  history: createWebHistory(),

  routes: [
    {
      path: "/",
      component: DefaultLayout,
      children: [
        // home
        { path: "", name: "home", component: HomeView },
        // ai recommend
        {
          path: "ai-recommend",
          name: "aiRecommend",
          component: AiRecommendView,
        },
        // books
        { path: "/books", name: "books", component: BooksView },
        // book detail
        {
          path: "/books/:isbn",
          name: "bookDetail",
          component: BookDetailView,
          props: true,
        },
        // community
        { path: "/community", name: "community", component: CommunityView },
        // post create
        {
          path: "/community/create",
          name: "postCreate",
          component: PostCreateUpdateView,
          meta: { requiresAuth: true },
        },
        // post detail
        {
          path: "/community/posts/:postId",
          name: "postDetail",
          component: PostDetailView,
          props: true,
        },
        // profile - 로그인 필요
        {
          path: "/profile",
          name: "profile",
          component: ProfileView,
          meta: { requiresAuth: true },
        },
      ],
    },
    //  layout(헤더, 푸터) 적용 없는 페이지
    { path: "/signin", name: "signin", component: SignInView },
    { path: "/signup", name: "signup", component: SignupView },
  ],
});

/* 
전역 가드 추가: 인증이 필요한 페이지 접근 제어
- meta: {requiresAuth:true} // 로그인 여부 체크
- 페이지 단위 로그인 요구 처리 (기능 단위 로그인 요구 처리는 composables > useRequireAuth)
*/
router.beforeEach((to) => {
  const authStore = useAuthStore();

  // 인증이 필요한데 비로그인 유저면 signin으로 보내고
  // 원래 가려던 경로 (to.fullPath)를 redirect로 들고감
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return {
      name: "signin",
      query: { redirect: to.fullPath },
    };
  }
  return true;
});
export default router;
