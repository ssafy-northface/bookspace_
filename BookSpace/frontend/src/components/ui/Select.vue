<!-- 
# 셀렉트 태그

[사용]
<Select
  v-model="selectedCategory"              // 양방향 바인딩
  label="카테고리"                        // 셀렉트 상단 라벨
  :options="categoryOptions"              // 옵션 배열 [{ label: '보여질 이름', value: '내부 값' }]
  placeholder="카테고리를 선택하세요"     // placeholder 
  :disabled="false"                       // 비활성화 여부 (선택)
  class="max-w-sm"                        // Tailwind 추가 스타일 (선택)
/>

@Props
- v-model: 선택된 값 (String | Number | Object)
- options: [{ label: string, value: any }]
- label: 상단 텍스트 라벨
- placeholder: 선택되지 않았을 때 표시할 문구
- disabled: 비활성화 여부
- class: 추가 TailwindCSS 스타일 지정 가능

-->
<template>
  <div class="w-full space-y-1">
    <label v-if="label" class="text-sm font-medium text-foreground">
      {{ label }}
    </label>

    <Select
      v-model="valueProxy"
      :options="options"
      optionLabel="label"
      optionValue="value"
      :placeholder="placeholder"
      :disabled="disabled"
      unstyled
      :pt="{
        root: { class: [baseClasses, props.class] },
        trigger: { class: triggerClasses },
        label: { class: labelClasses },
        dropdownIcon: { class: dropdownIconClasses },
        panel: { class: panelClasses },
        option: { class: optionClasses },
      }"
    />
  </div>
</template>

<script setup>
import { computed } from "vue";
import Select from "primevue/select";

const props = defineProps({
  modelValue: [String, Number, Object],
  options: { type: Array, default: () => [] },
  label: { type: String, default: "" },
  placeholder: { type: String, default: "선택하세요" },
  disabled: { type: Boolean, default: false },
  class: { type: String, default: "" },
});

const emit = defineEmits(["update:modelValue", "change"]);

const valueProxy = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emit("update:modelValue", val);
    emit("change", val);
  },
});

const baseClasses =
  "relative w-full rounded-md border border-input bg-background text-foreground shadow-xs outline-none " +
  "focus-visible:ring-ring/50 focus-visible:ring-[3px] focus-visible:border-ring " +
  "disabled:opacity-50 disabled:cursor-not-allowed transition-[color,box-shadow]";

const triggerClasses =
  "flex flex-row items-center w-full gap-2 px-3 py-2 cursor-pointer rounded-md text-sm " +
  "bg-transparent hover:bg-input/50 dark:bg-input/30 dark:hover:bg-input/50 relative justify-start flex-nowrap";

const labelClasses =
  "truncate text-sm text-foreground data-[placeholder]:text-muted-foreground pr-6";

const dropdownIconClasses =
  "w-4 h-4 text-muted-foreground opacity-50 shrink-0 pointer-events-none absolute right-3 top-1/2 -translate-y-1/2";

const panelClasses =
  "bg-background text-foreground border border-input rounded-md shadow-md mt-1";

const optionClasses =
  "cursor-pointer px-3 py-2 text-sm flex items-center gap-2 rounded-sm " +
  "hover:bg-accent hover:text-accent-foreground transition-colors";
</script>
