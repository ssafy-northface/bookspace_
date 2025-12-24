<template>
  <div class="space-y-1">
    <div class="flex gap-2 items-center w-full">
      <input
        ref="phonePart1Ref"
        :model-value="phonePart1"
        @input="handleInput(1, $event)"
        @keydown="handleKeydown(1, $event)"
        type="text"
        maxlength="3"
        placeholder="010"
        class="w-20 px-3 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-500 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30 transition-all text-center flex-shrink-0"
      />
      <span class="text-gray-400 text-sm w-3 text-center flex-shrink-0">-</span>
      <input
        ref="phonePart2Ref"
        :model-value="phonePart2"
        @input="handleInput(2, $event)"
        @keydown="handleKeydown(2, $event)"
        type="text"
        maxlength="4"
        placeholder="1234"
        class="flex-1 min-w-0 px-3 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-500 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30 transition-all text-center"
      />
      <span class="text-gray-400 text-sm w-3 text-center flex-shrink-0">-</span>
      <input
        ref="phonePart3Ref"
        :model-value="phonePart3"
        @input="handleInput(3, $event)"
        @keydown="handleKeydown(3, $event)"
        type="text"
        maxlength="4"
        placeholder="5678"
        class="flex-1 min-w-0 px-3 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-500 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30 transition-all text-center"
      />
    </div>
    <div class="h-6 mt-1">
      <p
        v-if="v$Phone.$error"
        class="flex items-center gap-1.5 text-sm text-red-500 dark:text-red-400 font-medium"
      >
        <svg
          class="w-4 h-4 shrink-0"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from "vue";
import { usePhoneNumber } from "@/composables/signup/usePhoneNumber";

const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  v$Phone: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue"]);

const phonePart1Ref = ref(null);
const phonePart2Ref = ref(null);
const phonePart3Ref = ref(null);

const {
  phonePart1,
  phonePart2,
  phonePart3,
  handleInput: handleInputFn,
  handleKeydown: handleKeydownFn,
} = usePhoneNumber(
  props.modelValue,
  (value) => emit("update:modelValue", value),
  () => props.v$Phone.$touch()
);

const handleInput = (part, event) => {
  handleInputFn(part, event, {
    phonePart1Ref,
    phonePart2Ref,
    phonePart3Ref,
  });
};

const handleKeydown = (part, event) => {
  handleKeydownFn(part, event, {
    phonePart1Ref,
    phonePart2Ref,
    phonePart3Ref,
  });
};
</script>
