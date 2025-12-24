<template>
  <div class="chat-input-container">
    <div class="flex items-end gap-2 p-4 border-t border-border">
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
          class="w-full resize-none rounded-3xl border border-input bg-accent px-5 py-3 text-sm text-foreground outline-none transition focus:border-yellow-400 focus:ring-2 focus:ring-yellow-400/20 disabled:opacity-50 disabled:cursor-not-allowed placeholder:text-muted-foreground min-h-[44px] max-h-32 overflow-y-auto scrollbar-hide"
        ></textarea>
      </div>

      <!-- 전송 버튼-->
      <button
        @click="handleSend"
        :disabled="!canSend"
        class="flex-shrink-0 w-10 h-10 rounded-full bg-yellow-400 hover:bg-yellow-500 disabled:bg-gray-300 disabled:cursor-not-allowed flex items-center justify-center transition shadow-sm"
      >
        <svg
          class="w-4 h-4 text-gray-800"
          :class="{ 'text-gray-500': !canSend }"
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
/* Hide scrollbar but keep functionality */
.scrollbar-hide {
  -ms-overflow-style: none !important;
  scrollbar-width: none !important;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none !important;
  width: 0 !important;
  height: 0 !important;
}

.scrollbar-hide::-webkit-scrollbar-track {
  display: none !important;
}

.scrollbar-hide::-webkit-scrollbar-thumb {
  display: none !important;
}
</style>
