<template>
  <BaseModal :visible="visible" maxWidth="max-w-md" @close="emitClose">
    <div class="space-y-4">
      <h3 class="text-lg font-semibold text-foreground">비밀번호 확인</h3>
      <p class="text-sm text-muted-foreground">
        계정 설정 페이지로 이동하려면 비밀번호를 입력해주세요.
      </p>
      <Input
        :model-value="password"
        type="password"
        placeholder="비밀번호를 입력하세요"
        @update:model-value="updatePassword"
        @keydown.enter.prevent="emitSubmit"
      />
      <p v-if="errorMessage" class="text-sm text-destructive">
        {{ errorMessage }}
      </p>
      <div class="flex justify-end gap-2">
        <button
          type="button"
          class="h-9 rounded-md px-4 text-sm font-semibold border border-input bg-background hover:bg-muted"
          @click="emitClose"
        >
          취소
        </button>
        <button
          type="button"
          class="h-9 rounded-md px-4 text-sm font-semibold text-white bg-primary hover:bg-primary/90 disabled:opacity-60"
          :disabled="loading"
          @click="emitSubmit"
        >
          확인
        </button>
      </div>
    </div>
  </BaseModal>
</template>

<script setup>
import BaseModal from "@/components/common/BaseModal.vue";
import Input from "@/components/ui/Input.vue";

const props = defineProps({
  visible: { type: Boolean, default: false },
  password: { type: String, default: "" },
  errorMessage: { type: String, default: "" },
  loading: { type: Boolean, default: false },
});

const emit = defineEmits(["update:password", "submit", "close"]);

const emitClose = () => emit("close");
const emitSubmit = () => emit("submit");
const updatePassword = (val) => emit("update:password", val);
</script>
