<!-- 
 Toast 컴포넌트 (단일 토스트 카드)
@params
- title: String // 토스트 제목
- description: String // 토스트 설명
- variant: String ('default' | 'destructive') // 토스트 스타일
- class: String // 추가 커스텀 클래스
-->

<template>
  <div :class="rootClasses" :data-variant="variant">
    <!-- 왼쪽: 제목 + 설명 + 내용 슬롯 -->
    <div class="grid gap-1">
      <p v-if="title" class="text-sm font-semibold">
        {{ title }}
      </p>

      <p v-if="description" class="text-sm opacity-90 text-muted-foreground">
        {{ description }}
      </p>

      <!-- 필요하면 추가 콘텐츠를 기본 슬롯으로 렌더링 -->
      <slot />
    </div>

    <!-- 오른쪽: action 영역 + 닫기 버튼 -->
    <div class="flex items-center gap-2">
      <!-- 액션 버튼 슬롯 (예: 되돌리기 / 다시 시도) -->
      <slot name="action" />

      <!-- 닫기 버튼 (ToastClose 역할) -->
      <button
        type="button"
        class="rounded-md p-1 text-foreground/50 opacity-0 transition-opacity hover:text-foreground focus:opacity-100 focus:outline-none focus:ring-2 group-hover:opacity-100 group-[.destructive]:text-red-300 group-[.destructive]:hover:text-red-50 group-[.destructive]:focus:ring-red-400 group-[.destructive]:focus:ring-offset-red-600"
        @click="emit('close')"
      >
        <X class="h-4 w-4" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { X } from "lucide-vue-next";

const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  description: {
    type: String,
    default: "",
  },
  // "default" | "destructive"
  variant: {
    type: String,
    default: "default",
  },
  class: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["close"]);

const baseClasses =
  "group pointer-events-auto relative flex w-full items-center justify-between space-x-4 overflow-hidden rounded-md border p-6 pr-8 shadow-lg transition-all bg-background text-foreground";

const variantClasses = {
  default: "border bg-background text-foreground",
  destructive:
    "destructive group border-destructive bg-destructive text-destructive-foreground",
};

const rootClasses = computed(() => {
  const variantClass = variantClasses[props.variant] ?? variantClasses.default;

  return [baseClasses, variantClass, props.class].join(" ");
});
</script>
