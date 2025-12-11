<!-- 
# AppLogo 컴포넌트
[사용]
 <AppLogo size="md" />
size prop:
 - sm: 작음
 - md: 중간 (기본값)
 - lg: 큼
-->
<template>
  <RouterLink :to="to" class="inline-flex items-center gap-2">
    <BookOpen :class="iconSizeClass" class="transition-colors" />
    <span :class="textSizeClass" class="font-semibold transition-colors">
      Bookspace
    </span>
  </RouterLink>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterLink } from "vue-router"; // ⭐ 필수
import { BookOpen } from "lucide-vue-next"; // ⭐ 중복 없이 1번만 import

const props = defineProps({
  to: { type: String, default: "/" },
  size: {
    type: String,
    default: "md",
    validator: (v: string) => ["sm", "md", "lg"].includes(v),
  },
});

// 아이콘 크기
const iconSizeClass = computed(() => {
  return {
    sm: "h-5 w-5",
    md: "h-7 w-7",
    lg: "h-10 w-10",
  }[props.size];
});

// 텍스트 크기
const textSizeClass = computed(() => {
  return {
    sm: "text-lg",
    md: "text-2xl",
    lg: "text-3xl",
  }[props.size];
});
</script>
