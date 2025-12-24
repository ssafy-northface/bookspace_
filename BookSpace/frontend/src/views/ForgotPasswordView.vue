<!-- 
# Forgot Password View
- 아이디와 이메일로 본인 확인 후 비밀번호 재설정
-->
<template>
  <div
    class="flex flex-col items-center justify-center min-h-screen px-4 bg-gradient-to-b from-background to-muted/20"
  >
    <div class="w-full max-w-md">
      <!-- 로고 & 제목 -->
      <div class="mb-8 text-center">
        <AppLogo size="lg" />
        <h1 class="text-3xl font-bold">비밀번호 찾기</h1>
        <p class="mt-2 text-muted-foreground">
          가입 시 등록한 아이디와 이메일을 입력해주세요
        </p>
      </div>

      <!-- Step 1: 본인 확인 -->
      <form v-if="step === 1" @submit.prevent="verifyUser" class="space-y-4">
        <ValidatedInput
          v-model="loginId"
          :v$="v$.loginId"
          type="text"
          placeholder="아이디"
        />

        <ValidatedInput
          v-model="email"
          :v$="v$.email"
          type="email"
          placeholder="이메일"
        />

        <Button
          type="submit"
          :loading="isSubmitting"
          :disabled="v$.$invalid"
          class="w-full mt-6"
        >
          본인 확인
        </Button>
      </form>

      <!-- Step 2: 비밀번호 재설정 -->
      <form v-else-if="step === 2" @submit.prevent="resetPassword" class="space-y-4">
        <p class="text-sm text-center text-green-600 mb-4">
          ✓ 본인 확인이 완료되었습니다. 새 비밀번호를 입력해주세요.
        </p>

        <ValidatedInput
          v-model="newPassword"
          :v$="v$Reset.newPassword"
          type="password"
          placeholder="새 비밀번호"
        />

        <ValidatedInput
          v-model="confirmPassword"
          :v$="v$Reset.confirmPassword"
          type="password"
          placeholder="비밀번호 확인"
        />

        <Button
          type="submit"
          :loading="isSubmitting"
          :disabled="v$Reset.$invalid"
          class="w-full mt-6"
        >
          비밀번호 변경
        </Button>
      </form>

      <!-- Step 3: 완료 -->
      <div v-else class="text-center space-y-4">
        <div class="text-green-600 text-5xl mb-4">✓</div>
        <h2 class="text-xl font-bold">비밀번호가 변경되었습니다</h2>
        <p class="text-muted-foreground">
          새 비밀번호로 로그인해주세요.
        </p>
        <RouterLink to="/signin">
          <Button class="w-full mt-4">로그인하기</Button>
        </RouterLink>
      </div>

      <!-- 로그인 이동 (Step 1, 2에서만) -->
      <p v-if="step < 3" class="mt-6 text-sm text-center text-muted-foreground">
        비밀번호가 기억나셨나요?
        <RouterLink
          to="/signin"
          class="font-medium text-primary hover:underline"
        >
          로그인
        </RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { RouterLink } from "vue-router";
import AppLogo from "@/components/common/AppLogo.vue";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Button from "@/components/ui/Button.vue";
import useVuelidate from "@vuelidate/core";
import { required, email as emailValidator, minLength, sameAs } from "@vuelidate/validators";
import { verifyUserApi, resetPasswordApi } from "@/api/userApi";

// 단계 관리 (1: 본인확인, 2: 비밀번호 변경, 3: 완료)
const step = ref(1);

// Step 1: 본인 확인
const loginId = ref("");
const email = ref("");

// Step 2: 비밀번호 재설정
const newPassword = ref("");
const confirmPassword = ref("");

const isSubmitting = ref(false);

// Step 1 유효성 검사 규칙
const rules = {
  loginId: { required },
  email: { required, emailValidator },
};
const v$ = useVuelidate(rules, { loginId, email });

// Step 2 유효성 검사 규칙
const rulesReset = computed(() => ({
  newPassword: { 
    required, 
    minLength: minLength(8) 
  },
  confirmPassword: { 
    required, 
    sameAs: sameAs(newPassword) 
  },
}));
const v$Reset = useVuelidate(rulesReset, { newPassword, confirmPassword });

// 본인 확인
const verifyUser = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    const result = await verifyUserApi({
      userLoginId: loginId.value,
      userEmail: email.value,
    });

    if (result.verified) {
      step.value = 2;
    } else {
      alert("입력하신 정보와 일치하는 계정을 찾을 수 없습니다.");
    }
  } catch (err) {
    alert(err.response?.data?.message || "본인 확인에 실패했습니다.");
  } finally {
    isSubmitting.value = false;
  }
};

// 비밀번호 재설정
const resetPassword = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    await resetPasswordApi({
      userLoginId: loginId.value,
      userEmail: email.value,
      newPassword: newPassword.value,
    });

    step.value = 3;
  } catch (err) {
    alert(err.response?.data?.message || "비밀번호 변경에 실패했습니다.");
  } finally {
    isSubmitting.value = false;
  }
};
</script>

