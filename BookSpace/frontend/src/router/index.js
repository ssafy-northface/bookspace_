// vue router 세팅 파일
import { createRouter, createWebHistory } from "vue-router";
import HomeView from "@/pages/HomeView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: "/", component: HomeView }],
});

export default router;
