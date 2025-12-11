import App from "./App.vue";
import { createApp } from "vue";
import { createPinia } from "pinia";
import router from "./router";
import "./styles/global.css";
import PrimeVue from "primevue/config";
import { VueQueryPlugin } from "@tanstack/vue-query";

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(PrimeVue, {
  theme: {
    preset: "none", // 커스텀 모드 -TODO: global.css에서 prime vue에서 사용하는 CSS 변수로 테마 설정 필요
    options: {
      darkModeSelector: ".dark", // tailwindcss dark 모드와 연동
    },
  },
});

// vue query 플러그인 등록
app.use(VueQueryPlugin, {
  queryClientConfig: {
    defaultOptions: {
      queries: {
        retry: 1, // 쿼리 실패 시 재시도 횟수
        staleTime: 1000 * 60 * 1, // 1분 동안 fresh 데이터로 간주
        cacheTime: 1000 * 60 * 5, // 5 동안 캐시 유지
      },
    },
  },
});

app.mount("#app");
