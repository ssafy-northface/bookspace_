/*
# useRequireAuth
- 로그인이 필요한 action을 감싸는 헬퍼
- 버튼 클릭 / 토글 / api 요청 등 행동 단위 로그인 여부 체크
- 비로그인일 때: toast + 로그인 페이지로 보내기
- 로그인일 때: action 실행

[사용]
const {requireAuth} = useRequireAuth();
const onWriteReview = requireAuth(
  () => openReviewModal(),
  {
    loginMessage: "리뷰 작성은 로그인 후 가능합니다",
  }
);
*/
export function useRequireAuth(options = {}) {
  const { loginMessage = "로그인 후 이용 가능한 서비스입니다", onBlocked } =
    options;

  const router = useRouter(); // 이동 제어
  const route = useRoute(); // 현재 위치
  const authStore = useAuthStore(); // 로그인 상태
  const { toast } = useToast(); // 알림 ui

  // 로그인 후 돌아갈 경로
  const resolveRedirectPath = (redirectTarget) => {
    if (!redirectTarget) return route.fullPath || "/";
    const resolved = router.resolve(redirectTarget);
    return resolved?.fullPath || resolved?.path || route.fullPath || "/";
  };

  // 로그인 페이지로 이동시키는 함수
  const redirectToLogin = (redirectTarget) => {
    router.push({
      name: "signin",
      query: { redirect: resolveRedirectPath(redirectTarget) },
    });
  };

  const requireAuth = (action, actionOptions = {}) => {
    const redirectTarget = actionOptions.redirect;
    const localLoginMessage =
      actionOptions.loginMessage !== undefined
        ? actionOptions.loginMessage
        : loginMessage;
    const localOnBlocked = actionOptions.onBlocked || onBlocked;

    return async (...args) => {
      if (authStore.isLoggedIn) {
        return typeof action === "function" ? action(...args) : undefined;
      }

      if (localLoginMessage) {
        if (toast) {
          toast({
            title: localLoginMessage,
            description: "로그인 후 이용해주세요.",
          });
        } else {
          alert(localLoginMessage);
        }
      }

      if (typeof localOnBlocked === "function") {
        localOnBlocked();
      }

      redirectToLogin(redirectTarget);
      return undefined;
    };
  };

  return {
    isLoggedIn: computed(() => authStore.isLoggedIn),
    requireAuth,
    redirectToLogin,
  };
}

export default useRequireAuth;

import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "@/composables/useToast";
