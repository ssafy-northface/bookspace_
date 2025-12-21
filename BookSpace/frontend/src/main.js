import App from "./App.vue";
import { createApp } from "vue";
import { createPinia } from "pinia";
import router from "./router";
import "./styles/global.css";
import PrimeVue from "primevue/config";
import { VueQueryPlugin } from "@tanstack/vue-query";
import { queryClient } from "./plugins/vueQuery";

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
app.use(VueQueryPlugin, { queryClient });

app.mount("#app");
