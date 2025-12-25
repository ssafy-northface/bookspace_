<!-- 
# Sign In View
- <ValidatedInput>: 폼 유효성 검사 & 에러 메세지 포함 컴포넌트 (src/components/ui/ValidatedInput.vue)
- 검사 시점: 각 인풋에서 포커스가 벗어날 때 검사 (blur) 
-->
<template>
  <div
    class="flex flex-col items-center justify-center min-h-screen px-4 bg-gradient-to-b from-background to-muted/20"
  >
    <div class="w-full max-w-md">
      <!-- 로고 & 로그인 -->
      <div class="mb-8 text-center">
        <AppLogo size="lg" />
        <h1 class="text-3xl font-bold">로그인</h1>
        <p class="mt-2 text-muted-foreground">
          독서의 즐거움을 함께 나누어보세요
        </p>
      </div>
      <!-- form -->
      <form @submit.prevent="login" class="space-y-4">
        <ValidatedInput
          v-model="loginId"
          :v$="v$.loginId"
          type="text"
          placeholder="아이디"
        />

        <ValidatedInput
          v-model="password"
          :v$="v$.password"
          type="password"
          placeholder="비밀번호"
        />

        <!-- 버튼 -->
        <Button
          type="submit"
          :loading="isSubmitting"
          :disabled="v$.$invalid"
          class="w-full mt-6"
        >
          로그인
        </Button>
      </form>

      <!-- 비밀번호 찾기 링크 -->
      <p class="mt-4 text-sm text-center">
        <RouterLink
          to="/forgot-password"
          class="text-muted-foreground hover:text-primary hover:underline"
        >
          비밀번호를 잊으셨나요?
        </RouterLink>
      </p>

      <!-- 회원가입 링크 -->
      <p class="mt-4 text-sm text-center text-muted-foreground">
        계정이 없으신가요?
        <RouterLink
          to="/signup"
          class="font-medium text-primary hover:underline"
        >
          회원가입
        </RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup>
import { nextTick, ref, onMounted } from "vue";
import { RouterLink, useRoute, useRouter } from "vue-router";
import AppLogo from "@/components/common/AppLogo.vue";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Button from "@/components/ui/Button.vue";
import useVuelidate from "@vuelidate/core";
import { required } from "@vuelidate/validators";
import { useAuthStore } from "../stores/authStore";
import { useToast } from "@/composables/useToast";

const route = useRoute();

// 상태
const loginId = ref("");
const password = ref("");

// 로그인 필요 메시지 확인 (useRequireAuth에서 설정한 경우)
onMounted(() => {
  const loginMessage = sessionStorage.getItem('loginRequiredMessage');
  if (loginMessage) {
    sessionStorage.removeItem('loginRequiredMessage');
    toast({
      title: "로그인 필요",
      description: loginMessage,
      variant: "destructive",
    });
  }
});

// Vuelidate 규칙 정의
const rules = {
  loginId: { required },
  password: { required },
};

// Vuelidate 인스턴스 생성
const v$ = useVuelidate(rules, { loginId, password }); //useVuelidate로 규칙 적용

const errorMessage = ref("");
const isSubmitting = ref(false);

const authStore = useAuthStore();
const router = useRouter();
const { toast } = useToast();

// 로그인 함수
const login = async () => {
  if (isSubmitting.value) return;

  await nextTick;
  isSubmitting.value = true;
  errorMessage.value = "";

  try {
    await authStore.login({
      userLoginId: loginId.value,
      userPw: password.value,
    });

    // 리다이렉트 경로 확인 (쿼리 파라미터는 배열일 수도 있음)
    const redirect = Array.isArray(route.query.redirect) 
      ? route.query.redirect[0] 
      : route.query.redirect;
    
    // 유효한 리다이렉트 경로인지 확인
    if (redirect && typeof redirect === "string" && redirect.startsWith("/")) {
      // 상대 경로가 아닌 절대 경로인지 확인하고, 같은 origin인지 확인
      try {
        const redirectUrl = new URL(redirect, window.location.origin);
        if (redirectUrl.origin === window.location.origin) {
          await router.replace(redirect); // 로그인 페이지 히스토리 남지 않게
          toast({
            title: "로그인 성공",
            description: "원하시던 페이지로 이동했습니다.",
          });
          return;
        }
      } catch (e) {
        // URL 파싱 실패 시 기본 경로로 이동
        console.warn("Invalid redirect URL:", redirect);
      }
    }
    
    // 리다이렉트 경로가 없거나 유효하지 않은 경우 홈으로 이동
    await router.replace({ name: "home" });
    toast({
      title: "로그인 성공",
      description: "환영합니다!",
    });
  } catch (err) {
    const msg =
      // 백에서 작성한 error 메시지 나옴 => "자격증명에 실패했습니다"
      // err.response?.data?.message ||
      "로그인에 실패했습니다. 아이디/비밀번호를 다시 확인해주세요.";
    errorMessage.value = msg;

    // 로그인 실패 시 toast
    toast({
      title: "로그인 실패",
      description: msg,
      variant: "destructive",
    });
  } finally {
    isSubmitting.value = false;
  }
};
</script>
