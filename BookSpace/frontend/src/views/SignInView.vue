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
import { nextTick, ref } from "vue";
import { RouterLink, useRoute, useRouter } from "vue-router";
import AppLogo from "@/components/common/AppLogo.vue";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Button from "@/components/ui/Button.vue";
import useVuelidate from "@vuelidate/core";
import { required } from "@vuelidate/validators";
import { useAuthStore } from "../stores/authStore";

const route = useRoute();

// 상태
const loginId = ref("");
const password = ref("");

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

    const redirect = route.query.redirect;
    if (typeof redirect === "string" && redirect.startsWith("/")) {
      await router.replace(redirect); // 로그인 페이지 히스토리 남지 않게
    } else {
      await router.replace({ name: "home" });
    }
    alert("로그인 성공!");
  } catch (err) {
    const msg =
      // 백에서 작성한 error 메시지 나옴 => "자격증명에 실패했습니다"
      // err.response?.data?.message ||
      "로그인에 실패했습니다. 아이디/비밀번호를 다시 확인해주세요.";
    errorMessage.value = msg;

    // 로그인 실패 시 alert
    alert(msg);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
