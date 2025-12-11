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
          v-model="id"
          :v$="v$.id"
          type="text"
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

        <!-- 이메일 -->
        <ValidatedInput
          v-model="email"
          :v$="v$.email"
          type="email"
          placeholder="이메일"
        />

        <!-- 닉네임 -->
        <ValidatedInput
          v-model="nickname"
          :v$="v$.nickname"
          type="text"
          placeholder="닉네임"
        />

        <!-- 전화번호 -->
        <ValidatedInput
          v-model="phone"
          :v$="v$.phone"
          type="number"
          placeholder="전화번호 (e.g.01012345678)"
        />

        <!-- 생년월일 -->
        <ValidatedInput
          v-model="birth"
          :v$="v$.birth"
          type="number"
          placeholder="생년월일 (e.g.20251217)"
        />

        <!-- 회원가입 버튼 -->
        <Button type="submit" :disabled="v$.$invalid" class="w-full mt-6">
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
const id = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const nickname = ref("");
const phone = ref("");
const birth = ref("");

// vuelidate 규칙 정의
const rules = {
  id: { required, minLength: minLength(4) },
  email: { required, email: emailValidator },
  password: { required, minLength: minLength(8) },
  confirmPassword: { required, sameAs: sameAs(password) },
  nickname: { required },
  phone: {
    required,
    numeric: helpers.regex(/^[0-9]*$/),
    minLength: minLength(11),
    maxLength: maxLength(11),
  },
  birth: {
    required,
    numeric: helpers.regex(/^[0-9]*$/),
    minLength: minLength(8),
    maxLength: maxLength(8),
  },
};

//vuelidate 인스턴스 생성
const v$ = useVuelidate(rules, {
  id,
  email,
  password,
  confirmPassword,
  nickname,
  phone,
  birth,
});

// 회원가입 확인
const signup = async () => {
  //   const valid = await v$.value.$validate();
  //   if (!valid) {
  //     console.log("실패", v$.value.$errors);
  //     return;
  //   }

  console.log({
    id: id.value,
    email: email.value,
    password: password.value,
    confirmPassword: confirmPassword.value,
    nickname: nickname.value,
    phone: phone.value,
    birth: birth.value,
  });
};

import { ref } from "vue";
import { RouterLink } from "vue-router";
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
</script>
