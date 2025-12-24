<!-- 
# Sign Up View
- 회원가입 폼 뷰 컴포넌트
- ValidatedInput: 폼 유효성 검사 & 에러 메세지 포함 컴포넌트 (src/components/ui/ValidatedInput.vue)
- 검사 시점: 각 인풋에서 포커스가 벗어날 때 검사 (blur) 
-->
<template>
  <div
    class="flex flex-col items-center justify-center min-h-screen px-4 bg-gradient-to-b from-background to-muted/20"
  >
    <div class="w-full max-w-md">
      <!-- 로고 & 제목 -->
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
        <EmailVerificationInput
          :email="email"
          :v$Email="v$.email"
          @update:email="email = $event"
          @verified="handleEmailVerified"
        />

        <!-- 닉네임 -->
        <ValidatedInput
          v-model="nickname"
          :v$="v$.nickname"
          type="text"
          field="nickname"
          placeholder="닉네임"
        />

        <!-- 전화번호 -->
        <PhoneNumberInput v-model="phone" :v$Phone="v$.phone" />

        <!-- 생년월일 -->
        <BirthDateInput v-model="birth" :v$Birth="v$.birth" />

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
import { RouterLink } from "vue-router";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Button from "@/components/ui/Button.vue";
import AppLogo from "@/components/common/AppLogo.vue";
import EmailVerificationInput from "@/components/signup/EmailVerificationInput.vue";
import PhoneNumberInput from "@/components/signup/PhoneNumberInput.vue";
import BirthDateInput from "@/components/signup/BirthDateInput.vue";
import { useSignupForm } from "@/composables/signup/useSignupForm";

// 회원가입 폼 로직
const {
  loginId,
  email,
  password,
  confirmPassword,
  name,
  nickname,
  phone,
  birth,
  isSubmitting,
  isEmailVerified,
  v$,
  signup,
  setIsEmailVerified,
} = useSignupForm();

// 이메일 인증 완료 핸들러
const handleEmailVerified = () => {
  setIsEmailVerified(true);
};
</script>
