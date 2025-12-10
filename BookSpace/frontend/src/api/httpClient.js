// 공통 axios & 에러 전달
import axios from "axios";

const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: false, // 필요 시 true
});

// 요청 인터셉터
// Authorization 토큰을 header에 자동으로 추가
httpClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터
// 에러 공통 처리
httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const status = error.response.status;
      const code = error.response.data?.code;
      const message = error.response.data?.message;

      console.error(
        `[API ERROR] status=${status}, code=${code}, message=${message}`
      );
    } else if (error.request) {
      console.error("[NETWORK ERROR] 서버 응답 없음");
    } else {
      console.error("[UNKNOWN ERROR]", error);
    }

    // 여기서 UI는 건드리지 않고, 에러만 위로 전달
    return Promise.reject(error);
  }
);

export default httpClient;

