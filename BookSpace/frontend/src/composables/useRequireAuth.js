import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "@/composables/useToast";

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
    // redirectTarget이 없으면 현재 경로 사용
    if (!redirectTarget) {
      return route.fullPath || "/";
    }

    // 이미 절대 경로인 경우 그대로 사용
    if (typeof redirectTarget === "string" && redirectTarget.startsWith("/")) {
      return redirectTarget;
    }

    // 라우터로 경로 해석 시도
    try {
      const resolved = router.resolve(redirectTarget);
      return resolved?.fullPath || resolved?.path || route.fullPath || "/";
    } catch (e) {
      // 해석 실패 시 현재 경로 또는 홈으로
      console.warn("Failed to resolve redirect path:", redirectTarget);
      return route.fullPath || "/";
    }
  };

  // 로그인 페이지로 이동시키는 함수
  const redirectToLogin = (
    redirectTarget,
    loginMessage = null,
    skipSessionStorage = false
  ) => {
    const redirectPath = resolveRedirectPath(redirectTarget);

    // useRequireAuth에서 이미 토스트를 띄웠으므로 sessionStorage에 저장하지 않음
    // 라우터 가드에서만 sessionStorage를 사용 (skipSessionStorage가 false일 때만)
    if (loginMessage && !skipSessionStorage) {
      sessionStorage.setItem("loginRequiredMessage", loginMessage);
    }

    router.push({
      name: "signin",
      query: { redirect: redirectPath },
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
        toast({
          title: "로그인 필요",
          description: localLoginMessage,
          variant: "destructive",
        });
      }

      if (typeof localOnBlocked === "function") {
        localOnBlocked(...args);
      }

      // useRequireAuth에서 이미 토스트를 띄웠으므로 sessionStorage에 저장하지 않음
      redirectToLogin(redirectTarget, localLoginMessage, true);
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
