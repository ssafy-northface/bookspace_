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

      <!-- Step 2: 이메일 인증 -->
      <form
        v-else-if="step === 2"
        @submit.prevent="verifyEmailCode"
        class="space-y-4"
      >
        <p class="text-sm text-center text-green-600 mb-4">
          ✓ 본인 확인이 완료되었습니다. 이메일로 발송된 인증 코드를
          입력해주세요.
        </p>

        <div class="space-y-2">
          <div class="flex gap-2 items-start">
            <div class="flex-1">
              <ValidatedInput
                v-model="verificationCode"
                :v$="v$Email.verificationCode"
                type="text"
                placeholder="인증 코드 6자리"
                maxlength="6"
                @input="handleCodeInput"
              />
            </div>
            <Button
              type="button"
              @click="sendVerificationCode"
              :loading="isSendingCode"
              :disabled="isSendingCode || codeResendCooldown > 0"
              variant="outline"
              class="whitespace-nowrap h-[48px]"
            >
              {{
                codeResendCooldown > 0 ? `${codeResendCooldown}초` : "재발송"
              }}
            </Button>
          </div>
          <p class="text-xs text-muted-foreground">
            {{ email }}로 인증 코드를 발송했습니다. (유효시간: 5분)
          </p>
        </div>

        <Button
          type="submit"
          :loading="isSubmitting"
          :disabled="v$Email.$invalid"
          class="w-full mt-6"
        >
          인증 확인
        </Button>
      </form>

      <!-- Step 3: 비밀번호 재설정 -->
      <form
        v-else-if="step === 3"
        @submit.prevent="
          route.query.token ? resetPasswordByToken() : resetPassword()
        "
        class="space-y-4"
      >
        <p class="text-sm text-center text-green-600 mb-4">
          ✓ 이메일 인증이 완료되었습니다. 새 비밀번호를 입력해주세요.
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

      <!-- Step 4: 완료 -->
      <div v-else class="text-center space-y-4">
        <div class="text-green-600 text-5xl mb-4">✓</div>
        <h2 class="text-xl font-bold">비밀번호가 변경되었습니다</h2>
        <p class="text-muted-foreground">새 비밀번호로 로그인해주세요.</p>
        <RouterLink to="/signin">
          <Button class="w-full mt-4">로그인하기</Button>
        </RouterLink>
      </div>

      <!-- 로그인 이동 (Step 1, 2, 3에서만) -->
      <p v-if="step < 4" class="mt-6 text-sm text-center text-muted-foreground">
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
import { ref, computed, onUnmounted, onMounted } from "vue";
import { RouterLink, useRoute, useRouter } from "vue-router";
import AppLogo from "@/components/common/AppLogo.vue";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Button from "@/components/ui/Button.vue";
import useVuelidate from "@vuelidate/core";
import {
  required,
  email as emailValidator,
  minLength,
  sameAs,
} from "@vuelidate/validators";
import {
  verifyUserApi,
  resetPasswordApi,
  resetPasswordByTokenApi,
} from "@/api/userApi";
import { sendVerificationCodeApi, verifyCodeApi } from "@/api/emailApi";
import { useToast } from "@/composables/useToast";

const { toast } = useToast();
const route = useRoute();
const router = useRouter();

// 단계 관리 (1: 본인확인, 2: 이메일 인증, 3: 비밀번호 변경, 4: 완료)
const step = ref(1);

// Step 1: 본인 확인
const loginId = ref("");
const email = ref("");

// Step 2: 이메일 인증
const verificationCode = ref("");
const isSendingCode = ref(false);
const codeResendCooldown = ref(0);
let cooldownTimer = null;

// Step 3: 비밀번호 재설정
const newPassword = ref("");
const confirmPassword = ref("");

const isSubmitting = ref(false);

// Step 1 유효성 검사 규칙
const rules = {
  loginId: { required },
  email: { required, emailValidator },
};
const v$ = useVuelidate(rules, { loginId, email });

// Step 2 유효성 검사 규칙 (이메일 인증)
const rulesEmail = {
  verificationCode: {
    required,
    minLength: minLength(6),
  },
};
const v$Email = useVuelidate(rulesEmail, { verificationCode });

// Step 3 유효성 검사 규칙 (비밀번호 재설정)
const rulesReset = computed(() => ({
  newPassword: {
    required,
    minLength: minLength(8),
  },
  confirmPassword: {
    required,
    sameAs: sameAs(newPassword),
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
      // 재설정 링크가 발송된 경우 (가입 시 이메일 인증 완료된 사용자)
      if (result.isLinkSent && result.resetLink) {
        toast({
          title: "재설정 링크 발송",
          description: "이메일로 비밀번호 재설정 링크를 발송했습니다.",
        });
        step.value = 3; // 링크 발송 시 이메일 인증 단계 건너뛰기
      } else {
        // 인증 코드 발송 (가입 시 이메일 인증 안 한 사용자)
        await sendVerificationCode();
        step.value = 2;
      }
    } else {
      toast({
        title: "본인 확인 실패",
        description: "입력하신 정보와 일치하는 계정을 찾을 수 없습니다.",
        variant: "destructive",
      });
    }
  } catch (err) {
    toast({
      title: "본인 확인 실패",
      description: err.response?.data?.message || "본인 확인에 실패했습니다.",
      variant: "destructive",
    });
  } finally {
    isSubmitting.value = false;
  }
};

// 이메일 인증 코드 발송
const sendVerificationCode = async () => {
  if (isSendingCode.value) return;
  isSendingCode.value = true;

  try {
    const result = await sendVerificationCodeApi(email.value);

    if (result.success) {
      toast({
        title: "인증 코드 발송",
        description: "이메일로 인증 코드를 발송했습니다.",
      });

      // 재발송 쿨다운 시작 (60초)
      codeResendCooldown.value = 60;
      if (cooldownTimer) clearInterval(cooldownTimer);
      cooldownTimer = setInterval(() => {
        codeResendCooldown.value--;
        if (codeResendCooldown.value <= 0) {
          clearInterval(cooldownTimer);
          cooldownTimer = null;
        }
      }, 1000);
    } else {
      toast({
        title: "발송 실패",
        description: result.message || "인증 코드 발송에 실패했습니다.",
        variant: "destructive",
      });
    }
  } catch (err) {
    toast({
      title: "발송 실패",
      description:
        err.response?.data?.message || "인증 코드 발송에 실패했습니다.",
      variant: "destructive",
    });
  } finally {
    isSendingCode.value = false;
  }
};

// 인증 코드 입력 핸들러 (숫자만 입력)
const handleCodeInput = (event) => {
  const value = event.target.value.replace(/[^0-9]/g, "");
  verificationCode.value = value;
  event.target.value = value;
};

// 이메일 인증 코드 검증
const verifyEmailCode = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    const result = await verifyCodeApi(email.value, verificationCode.value);

    if (result.success) {
      toast({
        title: "인증 완료",
        description: "이메일 인증이 완료되었습니다.",
      });
      step.value = 3;
    } else {
      toast({
        title: "인증 실패",
        description:
          result.message || "인증 코드가 일치하지 않거나 만료되었습니다.",
        variant: "destructive",
      });
    }
  } catch (err) {
    toast({
      title: "인증 실패",
      description:
        err.response?.data?.message || "인증 코드 검증에 실패했습니다.",
      variant: "destructive",
    });
  } finally {
    isSubmitting.value = false;
  }
};

// 비밀번호 재설정 (코드 인증 후)
const resetPassword = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    await resetPasswordApi({
      userLoginId: loginId.value,
      userEmail: email.value,
      newPassword: newPassword.value,
    });

    step.value = 4;
  } catch (err) {
    toast({
      title: "비밀번호 변경 실패",
      description:
        err.response?.data?.message || "비밀번호 변경에 실패했습니다.",
      variant: "destructive",
    });
  } finally {
    isSubmitting.value = false;
  }
};

// 비밀번호 재설정 (토큰 사용 - 링크 클릭 시)
const resetPasswordByToken = async () => {
  const token = route.query.token;
  if (!token) {
    toast({
      title: "유효하지 않은 링크",
      description: "재설정 링크가 올바르지 않습니다.",
      variant: "destructive",
    });
    return;
  }

  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    await resetPasswordByTokenApi({
      token: token,
      newPassword: newPassword.value,
    });

    // 토큰 제거하고 완료 페이지로
    router.replace({ path: route.path, query: {} });
    step.value = 4;
  } catch (err) {
    toast({
      title: "비밀번호 변경 실패",
      description:
        err.response?.data?.message || "비밀번호 변경에 실패했습니다.",
      variant: "destructive",
    });
  } finally {
    isSubmitting.value = false;
  }
};

// URL에 토큰이 있는 경우 처리
onMounted(() => {
  const token = route.query.token;
  if (token) {
    // 토큰이 있으면 바로 비밀번호 재설정 단계로
    step.value = 3;
    toast({
      title: "비밀번호 재설정",
      description: "새 비밀번호를 입력해주세요.",
    });
  }
});

// 컴포넌트 언마운트 시 타이머 정리
onUnmounted(() => {
  if (cooldownTimer) {
    clearInterval(cooldownTimer);
  }
});
</script>
