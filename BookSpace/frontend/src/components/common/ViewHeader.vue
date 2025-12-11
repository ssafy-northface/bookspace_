<!-- 
# ViewHeader component
페이지 상단에 헤더 형태로 페이지 타이틀과 설명을 표시

[사용]
<ViewHeader 
  title="페이지 타이틀" 
  description="페이지에 대한 간단한 설명을 여기에 작성합니다." 
  align="center" 
  size="lg"
-->
<template>
  <header :class="wrapperClass">
    <div>
      <!-- 메인 타이틀 -->
      <h1 :class="titleClass">
        {{ title }}
      </h1>

      <!-- 서브 텍스트 (옵션)-->
      <p v-if="description" class="mt-2 text-sm text-muted-foreground">
        {{ description }}
      </p>
    </div>
  </header>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    default: "",
  },
  align: {
    type: String,
    default: "left", // "left" | "center"
  },
  size: {
    type: String,
    default: "lg", // "md" | "lg"
  },
});

const wrapperClass = computed(() => {
  return ["mb-8", props.align === "center" ? "text-center" : "text-left"];
});

const titleClass = computed(() => {
  const sizeClass =
    props.size === "lg" ? "text-3xl md:text-4xl" : "text-2xl md:text-3xl";

  return ["font-bold tracking-tight", sizeClass];
});
</script>
