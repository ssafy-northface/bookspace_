<template>
  <div
    class="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-background to-muted/20 px-4"
  >
    <div class="w-full max-w-md">
      <!-- 로고 -->
      <div class="mb-8 text-center">
        <RouterLink to="/" class="inline-flex items-center gap-2 mb-4">
          <BookOpen class="h-8 w-8" />
          <span class="text-2xl font-semibold">Bookspace</span>
        </RouterLink>

        <h1 class="text-3xl font-bold">회원가입</h1>
        <p class="mt-2 text-muted-foreground">
          독서의 즐거움을 함께 나눌 준비되셨나요?
        </p>
      </div>

      <!-- 입력 필드 -->
      <div class="space-y-4">
        <Input v-model="id" type="text" placeholder="아아디" />

        <Input v-model="password" type="password" placeholder="비밀번호" />

        <Input
          v-model="confirmPassword"
          type="password"
          placeholder="비밀번호 확인"
        />

        <Input v-model="name" type="email" placeholder="이름" />

        <Input v-model="nickname" type="text" placeholder="닉네임" />

        <Input v-model="email" type="email" placeholder="이메일" />

        <Input v-model="number" type="email" placeholder="전화번호" />

        <Input v-model="number" type="email" placeholder="생년월일" />
      </div>

      <!-- 회원가입 버튼 -->
      <!-- 버튼 -->
      <Button class="w-full mt-6" @click="login"> 회원가입 </Button>

      <!-- 로그인 페이지로 이동 -->
      <p class="mt-6 text-center text-sm text-muted-foreground">
        이미 계정이 있으신가요?
        <RouterLink
          to="/login"
          class="font-medium text-primary hover:underline"
        >
          로그인
        </RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { RouterLink } from "vue-router";
import Input from "@/components/ui/Input.vue";
import { BookOpen } from "lucide-vue-next";
import Button from "@/components/ui/Button.vue";

// 입력 상태
const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const nickname = ref("");

// 회원가입 실행
const signup = () => {
  console.log("email:", email.value);
  console.log("password:", password.value);
  console.log("confirmPassword:", confirmPassword.value);
  console.log("nickname:", nickname.value);

  // TODO: 서버 요청 작성
  // await axios.post("/api/signup", {
  //   email: email.value,
  //   password: password.value,
  //   nickname: nickname.value
  // });
};
</script>
