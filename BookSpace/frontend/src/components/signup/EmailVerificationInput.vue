<template>
  <div class="space-y-2">
    <!-- 이메일 입력 + 인증번호 받기 버튼 -->
    <div class="flex gap-2">
      <div class="flex-1">
        <ValidatedInput
          :model-value="email"
          @update:model-value="$emit('update:email', $event)"
          :v$="v$Email"
          type="email"
          field="email"
          placeholder="이메일"
          :disabled="isEmailVerified"
        />
      </div>
      <Button
        type="button"
        @click="handleSendCode"
        :disabled="!isEmailValid || isSendingCode || isEmailVerified"
        :loading="isSendingCode"
        className="whitespace-nowrap h-[48px] rounded-xl bg-blue-500 hover:bg-blue-600 disabled:bg-gray-300 self-start"
      >
        {{ isEmailVerified ? "인증완료" : "인증번호 받기" }}
      </Button>
    </div>

    <!-- 인증번호 입력 (코드 발송 후 표시) -->
    <div v-if="isCodeSent && !isEmailVerified" class="flex gap-2">
      <input
        :model-value="verificationCode"
        @input="handleCodeInput"
        type="text"
        maxlength="6"
        placeholder="인증번호 6자리 입력"
        class="flex-1 px-4 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 outline-none focus:border-blue-500"
      />
      <Button
        type="button"
        @click="handleVerifyCode"
        :disabled="verificationCode.length !== 6 || isVerifying"
        :loading="isVerifying"
        className="h-[48px] bg-emerald-500 hover:bg-emerald-600 disabled:bg-gray-300"
      >
        확인
      </Button>
    </div>

    <!-- 인증 상태 메시지 -->
    <p
      v-if="emailVerificationMessage"
      :class="isEmailVerified ? 'text-emerald-500' : 'text-blue-500'"
      class="text-sm"
    >
      {{ emailVerificationMessage }}
    </p>
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
import ValidatedInput from "@/components/ui/ValidatedInput.vue";
import Button from "@/components/ui/Button.vue";
import { useEmailVerification } from "@/composables/signup/useEmailVerification";

const props = defineProps({
  email: {
    type: String,
    required: true,
  },
  v$Email: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["update:email", "verified"]);

// 이메일 유효성 검사 (인증번호 받기 버튼 활성화용)
const isEmailValid = computed(() => {
  const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  // 이메일 형식이 유효하고, 중복 체크를 통과했을 때만 활성화
  return emailRegex.test(props.email) && !props.v$Email.$error;
});

const emailRef = computed(() => props.email);

const {
  verificationCode,
  isCodeSent,
  isSendingCode,
  isVerifying,
  isEmailVerified,
  emailVerificationMessage,
  sendVerificationCode,
  verifyCode: verifyCodeFn,
} = useEmailVerification(emailRef, () => emit("verified"));

const handleSendCode = async () => {
  await sendVerificationCode();
};

const handleVerifyCode = async () => {
  const result = await verifyCodeFn();
  if (result) {
    emit("verified");
  }
};

const handleCodeInput = (event) => {
  const value = event.target.value.replace(/[^0-9]/g, "").slice(0, 6);
  verificationCode.value = value;
  event.target.value = value;
};
</script>
