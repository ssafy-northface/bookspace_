<!-- 
# Sign Up View
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
        <h1 class="text-3xl font-bold">회원가입</h1>
        <p class="mt-2 text-muted-foreground">
          독서의 즐거움을 함께 나누어보세요
        </p>
      </div>

      <!-- form -->
      <form @submit.prevent="signup" class="space-y-4">
        <!-- 아이디 -->
        <ValidatedInput
          v-model="loginId"
          :v$="v$.loginId"
          type="text"
          field="loginId"
          placeholder="아이디"
        />

        <!-- 비밀번호 -->
        <ValidatedInput
          v-model="password"
          :v$="v$.password"
          type="password"
          placeholder="비밀번호"
        />

        <!-- 비밀번호 확인 -->
        <ValidatedInput
          v-model="confirmPassword"
          :v$="v$.confirmPassword"
          type="password"
          placeholder="비밀번호 확인"
        />

        <!-- 이름 -->
        <ValidatedInput
          v-model="name"
          :v$="v$.name"
          type="text"
          placeholder="이름"
        />

        <!-- 이메일 + 인증 -->
        <div class="space-y-2">
          <!-- 이메일 입력 + 인증번호 받기 버튼 -->
          <div class="flex gap-2">
            <div class="flex-1">
              <ValidatedInput
                v-model="email"
                :v$="v$.email"
                type="email"
                field="email"
                placeholder="이메일"
                :disabled="isEmailVerified"
              />
            </div>
            <button
              type="button"
              @click="sendVerificationCode"
              :disabled="!isEmailValid || isSendingCode || isEmailVerified"
              class="px-4 py-3 text-sm font-medium text-white bg-blue-500 rounded-xl hover:bg-blue-600 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors whitespace-nowrap"
            >
              {{ isSendingCode ? '발송 중...' : isEmailVerified ? '인증완료' : '인증번호 받기' }}
            </button>
          </div>

          <!-- 인증번호 입력 (코드 발송 후 표시) -->
          <div v-if="isCodeSent && !isEmailVerified" class="flex gap-2">
            <input
              v-model="verificationCode"
              type="text"
              maxlength="6"
              placeholder="인증번호 6자리 입력"
              class="flex-1 px-4 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 outline-none focus:border-blue-500"
            />
            <button
              type="button"
              @click="verifyCode"
              :disabled="verificationCode.length !== 6 || isVerifying"
              class="px-4 py-3 text-sm font-medium text-white bg-emerald-500 rounded-xl hover:bg-emerald-600 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
            >
              {{ isVerifying ? '확인 중...' : '확인' }}
            </button>
          </div>

          <!-- 인증 상태 메시지 -->
          <p v-if="emailVerificationMessage" :class="isEmailVerified ? 'text-emerald-500' : 'text-blue-500'" class="text-sm">
            {{ emailVerificationMessage }}
          </p>
        </div>

        <!-- 닉네임 -->
        <ValidatedInput
          v-model="nickname"
          :v$="v$.nickname"
          type="text"
          field="nickname"
          placeholder="닉네임"
        />

        <!-- 전화번호 -->
        <ValidatedInput
          v-model="phone"
          :v$="v$.phone"
          type="text"
          placeholder="전화번호 (e.g.010-1234-5678)"
        />

        <!-- 생년월일 -->
        <ValidatedInput
          v-model="birth"
          :v$="v$.birth"
          type="text"
          placeholder="생년월일 (e.g.2025-12-17)"
        />

        <!-- 회원가입 버튼 -->
        <Button
          type="submit"
          :loading="isSubmitting"
          :disabled="v$.$invalid"
          class="w-full mt-6"
        >
          회원가입
        </Button>
      </form>

      <!-- 로그인 이동 -->
      <p class="mt-6 text-sm text-center text-muted-foreground">
        이미 계정이 있으신가요?
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
// 상태
const loginId = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const name = ref("");
const nickname = ref("");
const phone = ref("");
const birth = ref("");
const isSubmitting = ref(false);

// 이메일 인증 상태
const verificationCode = ref("");
const isCodeSent = ref(false);
const isSendingCode = ref(false);
const isVerifying = ref(false);
const isEmailVerified = ref(false);
const emailVerificationMessage = ref("");

const userStore = useUserStore();
const router = useRouter();

// 이메일 유효성 검사 (인증번호 받기 버튼 활성화용)
// 반드시 @ 뒤에 도메인이 있어야 함 (예: user@gmail.com)
const isEmailValid = computed(() => {
  const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  return emailRegex.test(email.value);
});

// 인증번호 발송
const sendVerificationCode = async () => {
  if (isSendingCode.value || !isEmailValid.value) return;

  isSendingCode.value = true;
  emailVerificationMessage.value = "";

  try {
    const result = await sendVerificationCodeApi(email.value);
    if (result.success) {
      isCodeSent.value = true;
      emailVerificationMessage.value = "인증번호가 이메일로 발송되었습니다. (5분 내 입력)";
    } else {
      emailVerificationMessage.value = result.message;
    }
  } catch (err) {
    emailVerificationMessage.value = err.response?.data?.message || "인증번호 발송에 실패했습니다.";
  } finally {
    isSendingCode.value = false;
  }
};

// 인증번호 확인
const verifyCode = async () => {
  if (isVerifying.value || verificationCode.value.length !== 6) return;

  isVerifying.value = true;

  try {
    const result = await verifyCodeApi(email.value, verificationCode.value);
    if (result.success) {
      isEmailVerified.value = true;
      emailVerificationMessage.value = "✓ 이메일 인증이 완료되었습니다.";
    } else {
      emailVerificationMessage.value = result.message;
    }
  } catch (err) {
    emailVerificationMessage.value = err.response?.data?.message || "인증번호가 일치하지 않습니다.";
  } finally {
    isVerifying.value = false;
  }
};

// 중복 체크용 함수 정의
const dupCheck = (type) =>
    helpers.withAsync(async (value) => {
      if(!value) return true;
      
      // 이메일 타입인 경우, 형식이 유효할 때만 중복 체크
      if (type === "email") {
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        if (!emailRegex.test(value)) return true; // 형식 검사 먼저 통과 필요
      }
      
      try {
        return await userStore.checkDuplicate(type, value);
      } catch {
        return true;
      }
    });

// vuelidate 규칙 정의
const rules = {
  loginId: { 
    required, 
    minLength: minLength(4),
    unique: dupCheck("loginId"),
  },
  email: { 
    required, 
    email: emailValidator,
    unique: dupCheck("email")
  },
  password: { required, minLength: minLength(8) },
  confirmPassword: { required, sameAs: sameAs(password) },
  name: { required },
  nickname: { 
    required,
    unique: dupCheck("nickname")
  },
  phone: {
    //000-0000-0000
    phoneFormat: helpers.regex(/^\d{3}-\d{4}-\d{4}$/),
    $autoDirty: true,
  },
  birth: {
    // 0000-00-00
    birthFormat: helpers.regex(/^\d{4}-\d{2}-\d{2}$/),
    $autoDirty: true,
  },
};

//vuelidate 인스턴스 생성
const v$ = useVuelidate(rules, {
  loginId,
  email,
  password,
  confirmPassword,
  name,
  nickname,
  phone,
  birth,
});

// 회원가입 확인
const signup = async () => {
  
  // 이미 요청 중이면 또 요청 안보내기
  if (isSubmitting.value) return;

  // 이메일 인증 확인
  if (!isEmailVerified.value) {
    alert("이메일 인증을 완료해주세요.");
    return;
  }

  // 유효성 검사
  const valid = await v$.value.$validate();
  if (!valid) {
    alert("입력한 정보를 다시 확인해주세요.")
    return;
  }
  
  // DTO 형식에 맞게 payload 구성
  const payload = {
    userLoginId : loginId.value,
    userPw: password.value,
    userName: name.value,
    userNickname: nickname.value,
    userEmail: email.value,
    userPhone: phone.value,
    userBirthDate: birth.value,
  };

  // 콘솔창에 출력해보기
  console.log("Signup Info: ", payload);
  
  try {
    isSubmitting.value = true;

    // 회원가입 API 호출
    await userStore.signup(payload);

    alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.") // 확인하기 위한 알림창. 나중에 삭제해도 됨
    await router.push("/signin");
  } catch (err) {
    const msg = err?.response?.data?.message ||
      "회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.";
      alert(msg);
  } finally {
    isSubmitting.value = false;
  }
};


import { ref, computed } from "vue";
import { RouterLink, useRouter } from "vue-router";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Button from "@/components/ui/Button.vue";
import AppLogo from "@/components/common/AppLogo.vue";
import useVuelidate from "@vuelidate/core";
import {
  required,
  email as emailValidator,
  minLength,
  maxLength,
  sameAs,
  helpers,
} from "@vuelidate/validators";
import { useUserStore } from "../stores/userStore";
import { sendVerificationCodeApi, verifyCodeApi } from "@/api/emailApi";
</script>