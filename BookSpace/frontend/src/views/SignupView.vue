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

const userStore = useUserStore();
const router = useRouter();

// vuelidate 규칙 정의
const rules = {
  loginId: { required, minLength: minLength(4) },
  email: { required, email: emailValidator },
  password: { required, minLength: minLength(8) },
  confirmPassword: { required, sameAs: sameAs(password) },
  name: { required },
  nickname: { required },
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


import { ref } from "vue";
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
</script>