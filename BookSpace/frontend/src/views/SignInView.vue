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

        <h1 class="text-3xl font-bold">로그인</h1>
        <p class="mt-2 text-muted-foreground">
          독서의 즐거움을 함께 나누어보세요
        </p>
      </div>

      <div class="space-y-4">
        <Input v-model="email" type="email" placeholder="이메일" />

        <Input v-model="password" type="password" placeholder="비밀번호" />
      </div>

      <!-- 버튼 -->
      <Button class="w-full mt-6" @click="login"> 로그인 </Button>

      <!-- 회원가입 링크 -->
      <p class="mt-6 text-center text-sm text-muted-foreground">
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
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { BookOpen } from "lucide-vue-next";
import Input from "@/components/ui/Input.vue";
import Button from "@/components/ui/Button.vue";

// 상태
const email = ref("");
const password = ref("");

// 로그인 함수
const login = () => {
  console.log("Email:", email.value);
  console.log("Password:", password.value);

  // 서버 전달 가능
  // await axios.post("/api/login", {
  //   email: email.value,
  //   password: password.value
  // });
};
</script>
