import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import tailwindcss from "@tailwindcss/vite";
import { fileURLToPath, URL } from "node:url";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), tailwindcss],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
    extensions: [".js", ".ts", ".vue", ".json"], // 파일 확장자 자동 인식
  },
  server: {
    host: true, // 외부 접속 허용
    port: 5173, // 포트 고정
    strictPort: true, // 포트 중복 시 실행 안 함
    allowedHosts: "all", // 모든 호스트 허용 (ngrok 등)
    // 프록시 설정: /api 요청을 백엔드로 전달 (CORS, Mixed Content 문제 해결)
    proxy: {
      "/api": {
        target: "http://localhost:8080", // 백엔드 주소
        changeOrigin: true, // Origin 헤더를 백엔드 주소로 변경
        rewrite: (path) => path.replace(/^\/api/, ""), // /api 접두사 제거
        secure: false, // HTTPS 인증서 검증 비활성화
      },
    },
  },
});
