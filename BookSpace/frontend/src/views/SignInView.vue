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
        <Input v-model="loginId" type="text" placeholder="아이디" />

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
import { RouterLink , useRouter} from "vue-router";
import { BookOpen } from "lucide-vue-next";
import Input from "@/components/ui/Input.vue";
import Button from "@/components/ui/Button.vue";
import { useAuthStore } from "../stores/authStore";

// 상태
const loginId = ref("");
const password = ref("");

const errorMessage = ref("");
const isSubmitting = ref(false);

const authStore = useAuthStore();
const router = useRouter();

// // 로그인 함수
// const login = () => {
//   console.log("Email:", email.value);
//   console.log("Password:", password.value);

//   // 서버 전달 가능
//   // await axios.post("/api/login", {
//   //   email: email.value,
//   //   password: password.value
//   // });
// };

// 로그인 함수
const login = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;
  errorMessage.value = "";

  try {
    await authStore.login({
      userLoginId: loginId.value,   // 🔹 백엔드 DTO 필드명에 맞춰서 매핑
      userPw: password.value,
    });

    // 로그인 성공 메시지 (나중에 삭제해도 됨)
    alert("로그인 성공!");

    // 로그인 성공 시 메인으로 이동 (원하는 경로로 수정 가능)
    await router.push("/");
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
