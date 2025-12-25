<template>
  <div class="chat-input-container">
    <div class="flex items-end gap-3 p-4">
      <!-- 입력 필드 -->
      <div class="flex-1">
        <textarea
          ref="inputRef"
          v-model="inputText"
          @keydown="handleKeydown"
          @input="adjustHeight"
          @focus="handleFocus"
          @blur="handleBlur"
          :placeholder="placeholder"
          :disabled="disabled"
          rows="1"
          class="w-full resize-none rounded-2xl border-2 border-gray-200 dark:border-slate-600 bg-white dark:bg-slate-800 px-5 py-3 text-sm text-card-foreground outline-none transition-all duration-200 focus:border-blue-400 dark:focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30 disabled:opacity-50 disabled:cursor-not-allowed placeholder:text-muted-foreground min-h-[44px] max-h-32 overflow-y-auto scrollbar-hide shadow-sm"
        ></textarea>
      </div>

      <!-- 전송 버튼-->
      <button
        @click="handleSend"
        :disabled="!canSend"
        class="flex-shrink-0 w-11 h-11 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 disabled:from-gray-300 disabled:to-gray-400 disabled:cursor-not-allowed flex items-center justify-center transition-all duration-200 shadow-md hover:shadow-lg hover:scale-105"
      >
        <svg
          class="w-5 h-5 text-white"
          :class="{ 'text-gray-100': !canSend }"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          viewBox="0 0 24 24"
        >
          <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from "vue";

const props = defineProps({
  placeholder: {
    type: String,
    default: "메시지를 입력하세요...",
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["send", "input-active", "input-inactive"]);

const inputRef = ref(null);
const inputText = ref("");

const canSend = computed(() => {
  return inputText.value.trim().length > 0 && !props.disabled;
});

const handleSend = () => {
  if (!canSend.value) return;

  const message = inputText.value.trim();
  emit("send", message);
  inputText.value = "";

  // 텍스트 영역 높이 초기화
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.style.height = "auto";
    }
  });
};

const handleKeydown = (event) => {
  // Enter = 메시지 전송
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    handleSend();
  }
  // Shift + Enter = 새 줄 (기본 동작)
};

const adjustHeight = () => {
  const textarea = inputRef.value;
  if (!textarea) return;

  textarea.style.height = "auto";
  textarea.style.height = `${Math.min(textarea.scrollHeight, 128)}px`;
};

const handleFocus = () => {
  emit("input-active");
};

const handleBlur = () => {
  emit("input-inactive");
};

// 입력 텍스트 설정
const setInputText = (text) => {
  inputText.value = text;
  nextTick(() => adjustHeight());
};

defineExpose({
  setInputText,
});
</script>

<style scoped>
</style>
