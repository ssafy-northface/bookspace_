<template>
  <div class="space-y-1">
    <div class="flex gap-2 items-center w-full">
      <input
        ref="birthYearRef"
        :model-value="birthYear"
        @input="handleInput('year', $event)"
        @keydown="handleKeydown('year', $event)"
        type="text"
        maxlength="4"
        placeholder="0000"
        class="w-20 px-3 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-500 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30 transition-all text-center flex-shrink-0"
      />
      <span class="text-gray-400 text-sm w-3 text-center flex-shrink-0"
        >년</span
      >
      <input
        ref="birthMonthRef"
        :model-value="birthMonth"
        @input="handleInput('month', $event)"
        @keydown="handleKeydown('month', $event)"
        type="text"
        maxlength="2"
        placeholder="00"
        class="flex-1 min-w-0 px-3 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-500 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30 transition-all text-center"
      />
      <span class="text-gray-400 text-sm w-3 text-center flex-shrink-0"
        >월</span
      >
      <input
        ref="birthDayRef"
        :model-value="birthDay"
        @input="handleInput('day', $event)"
        @keydown="handleKeydown('day', $event)"
        type="text"
        maxlength="2"
        placeholder="00"
        class="flex-1 min-w-0 px-3 py-3 text-sm border-2 border-gray-200 dark:border-slate-600 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-500 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:focus:ring-blue-900/30 transition-all text-center"
      />
      <span class="text-gray-400 text-sm w-3 text-center flex-shrink-0"
        >일</span
      >
    </div>
    <div class="h-6 mt-1">
      <p
        v-if="v$Birth.$error"
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
        {{ v$Birth.required?.$message || "생년월일을 입력해주세요." }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useBirthDate } from "@/composables/signup/useBirthDate";

const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  v$Birth: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue"]);

const birthYearRef = ref(null);
const birthMonthRef = ref(null);
const birthDayRef = ref(null);

const {
  birthYear,
  birthMonth,
  birthDay,
  handleInput: handleInputFn,
  handleKeydown: handleKeydownFn,
} = useBirthDate(
  props.modelValue,
  (value) => emit("update:modelValue", value),
  () => props.v$Birth.$touch()
);

const handleInput = (type, event) => {
  handleInputFn(type, event, {
    birthYearRef,
    birthMonthRef,
    birthDayRef,
  });
};

const handleKeydown = (type, event) => {
  handleKeydownFn(type, event, {
    birthYearRef,
    birthMonthRef,
    birthDayRef,
  });
};
</script>
