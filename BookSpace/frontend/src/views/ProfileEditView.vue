<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4 bg-gradient-to-b from-background to-muted/20">
    <div class="w-full max-w-2xl space-y-8">
      <div class="space-y-2 text-center">
        <h1 class="text-3xl font-bold">계정 정보 수정</h1>
        <p class="text-sm text-muted-foreground">
          이메일과 닉네임은 중복 여부를 확인한 후 저장됩니다.
        </p>
      </div>

      <form @submit.prevent="onSubmit" class="space-y-4">
        <div class="grid gap-4 md:grid-cols-2">
          <div class="space-y-1">
            <label class="text-sm font-medium text-muted-foreground">아이디</label>
            <Input v-model="loginId" disabled placeholder="아이디" />
          </div>
          <div class="space-y-1">
            <label class="text-sm font-medium text-muted-foreground">이름</label>
            <ValidatedInput v-model="name" :v$="v$.name" placeholder="이름" />
          </div>
        </div>

        <div class="space-y-1">
          <label class="text-sm font-medium text-muted-foreground">이메일</label>
          <ValidatedInput
            v-model="email"
            :v$="v$.email"
            field="email"
            type="email"
            placeholder="이메일"
          />
        </div>

        <div class="space-y-1">
          <label class="text-sm font-medium text-muted-foreground">닉네임</label>
          <ValidatedInput
            v-model="nickname"
            :v$="v$.nickname"
            field="nickname"
            placeholder="닉네임"
          />
        </div>

        <div class="grid gap-4 md:grid-cols-2">
          <div class="space-y-1">
            <label class="text-sm font-medium text-muted-foreground">전화번호</label>
            <ValidatedInput
              v-model="phone"
              :v$="v$.phone"
              placeholder="010-1234-5678"
            />
          </div>
          <div class="space-y-1">
            <label class="text-sm font-medium text-muted-foreground">생년월일</label>
            <ValidatedInput
              v-model="birth"
              :v$="v$.birth"
              placeholder="1990-01-01"
            />
          </div>
        </div>

        <div class="flex justify-end gap-2 pt-4">
          <button
            type="button"
            class="h-10 px-4 rounded-md border border-input bg-background text-sm font-semibold hover:bg-muted"
            @click="goBack"
          >
            취소
          </button>
          <Button type="submit" :loading="isSubmitting" class="h-10 px-5">
            저장하기
          </Button>
        </div>

        <button
          type="button"
          class="mt-2 text-sm text-destructive underline-offset-4 hover:underline"
          @click="handleDeleteAccount"
        >
          회원 탈퇴하기
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import useVuelidate from "@vuelidate/core";
import {
  required,
  email as emailValidator,
  helpers,
} from "@vuelidate/validators";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Input from "@/components/ui/Input.vue";
import Button from "@/components/ui/Button.vue";
import { useUserStore } from "@/stores/userStore";
import { useAuthStore } from "@/stores/authStore";

const userStore = useUserStore();
const authStore = useAuthStore();
const router = useRouter();

const loginId = ref("");
const name = ref("");
const email = ref("");
const nickname = ref("");
const phone = ref("");
const birth = ref("");
const isSubmitting = ref(false);
const initial = ref({
  email: "",
  nickname: "",
});

const userId = computed(() => userStore.me?.userId);

const dupCheck = (type, key) =>
  helpers.withAsync(async (value) => {
    if (!value) return true;
    if (value === initial.value[key]) return true;
    try {
      return await userStore.checkDuplicate(type, value);
    } catch {
      return true;
    }
  });

const rules = computed(() => ({
  name: { required },
  email: { required, email: emailValidator, unique: dupCheck("email", "email") },
  nickname: { required, unique: dupCheck("nickname", "nickname") },
  phone: {
    phoneFormat: helpers.regex(/^(\d{3}-\d{4}-\d{4})?$/),
    $autoDirty: true,
  },
  birth: {
    birthFormat: helpers.regex(/^(\d{4}-\d{2}-\d{2})?$/),
    $autoDirty: true,
  },
}));

const v$ = useVuelidate(rules, {
  name,
  email,
  nickname,
  phone,
  birth,
});

const hydrateForm = (data) => {
  if (!data) return;
  loginId.value = data.userLoginId ?? data.loginId ?? "";
  name.value = data.userName ?? data.name ?? "";
  email.value = data.userEmail ?? data.email ?? "";
  nickname.value = data.userNickname ?? data.nickname ?? "";
  phone.value = data.userPhone ?? data.phone ?? "";
  birth.value = data.userBirthDate ?? data.birth ?? "";
  initial.value = {
    email: email.value,
    nickname: nickname.value,
  };
};

onMounted(async () => {
  if (!userStore.me) {
    await userStore.fetchMyInfo();
  }
  hydrateForm(userStore.me);
});

const goBack = () => {
  router.push({ name: "profile" });
};

const onSubmit = async () => {
  const valid = await v$.value.$validate();
  if (!valid) {
    alert("입력한 정보를 다시 확인해주세요.");
    return;
  }
  if (!userId.value) {
    alert("유저 정보를 불러오지 못했습니다.");
    return;
  }
  const payload = {
    userName: name.value,
    userNickname: nickname.value,
    userEmail: email.value,
    userPhone: phone.value || null,
    userBirthDate: birth.value || null,
  };
  try {
    isSubmitting.value = true;
    await userStore.updateUser(userId.value, payload);
    await userStore.fetchMyInfo();
    alert("계정 정보가 수정되었습니다.");
    router.push({ name: "profile" });
  } catch (err) {
    const msg =
      err?.response?.data?.message ??
      "정보 수정에 실패했습니다. 잠시 후 다시 시도해주세요.";
    alert(msg);
  } finally {
    isSubmitting.value = false;
  }
};

const handleDeleteAccount = async () => {
  if (!userId.value) return;
  const ok = confirm("정말로 회원 탈퇴하시겠습니까?");
  if (!ok) return;
  try {
    await userStore.softDeleteUser(userId.value);
    authStore.logout();
    alert("회원 탈퇴가 완료되었습니다.");
    router.push("/");
  } catch (err) {
    const msg =
      err?.response?.data?.message ??
      "회원 탈퇴에 실패했습니다. 잠시 후 다시 시도해주세요.";
    alert(msg);
  }
};
</script>
