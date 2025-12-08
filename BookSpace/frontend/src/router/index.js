
// // src/router/index.js
// import { createRouter, createWebHistory } from 'vue-router'

// // === views import ===
// import HomeView from '@/views/HomeView.vue'
// import AiRecommendView from '@/views/AiRecommendView.vue'
// import BooksView from '@/views/BooksView.vue'
// import BookDetailView from '@/views/BookDetailView.vue'
// import CommunityView from '@/views/CommunityView.vue'
// import PostDetailView from '@/views/PostDetailView.vue'
// import ProfileView from '@/views/ProfileView.vue'
// import LoginView from '@/views/LoginView.vue'
// import SignupView from '@/views/SignupView.vue'

// // === 라우트 정의 ===
// const routes = [
//   {
//     path: '/',
//     name: 'home',
//     component: HomeView,
//   },
//   {
//     path: '/ai-recommend',
//     name: 'aiRecommend',
//     component: AiRecommendView,
//   },
//   {
//     path: '/books',
//     name: 'books',
//     component: BooksView,
//   },
//   {
//     path: '/books/:bookId',
//     name: 'bookDetail',
//     component: BookDetailView,
//     props: true, // BookDetailView에서 route.params.bookId를 props로 받을 수 있게
//   },
//   {
//     path: '/community',
//     name: 'community',
//     component: CommunityView,
//   },
//   {
//     path: '/community/:postId',
//     name: 'postDetail',
//     component: PostDetailView,
//     props: true,
//   },
//   {
//     path: '/profile',
//     name: 'profile',
//     component: ProfileView,
//     meta: { requiresAuth: true }, // 나중에 로그인 필요하게 막을 때 사용
//   },
//   {
//     path: '/login',
//     name: 'login',
//     component: LoginView,
//   },
//   {
//     path: '/signup',
//     name: 'signup',
//     component: SignupView,
//   },
//   // 필요하면 404 페이지도 나중에 추가 가능
// ]

// // === 라우터 생성 ===
// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes,
//   scrollBehavior() {
//     // 라우트 이동 시 항상 맨 위로
//     return { top: 0 }
//   },
// })

// export default router


// vue router 세팅 파일
import { createRouter, createWebHistory } from "vue-router";
import HomeView from "@/pages/HomeView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: "/", component: HomeView }],
});

export default router;

