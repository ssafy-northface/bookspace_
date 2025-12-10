<template>
  <!-- 실제로 <style> 태그 하나를 DOM에 뿌려주는 컴포넌트 -->
  <component
    :is="'style'"
    v-if="computedStyles"
    v-html="computedStyles"
  />
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  // config: { series1: { theme: { light: '...', dark: '...' } } ... }
  config: {
    type: Object,
    required: true,
  },
});

// light는 기본, dark는 .dark 프리픽스
const THEMES = {
  light: "",
  dark: ".dark",
};

const computedStyles = computed(() => {
  // theme 또는 color 값을 가진 항목만 추출
  const colorConfig = Object.entries(props.config).filter(
    ([, cfg]) => cfg && (cfg.theme || cfg.color)
  );

  if (!colorConfig.length) return "";

  return Object.entries(THEMES)
    .map(([theme, prefix]) => {
      const lines = colorConfig
        .map(([key, itemConfig]) => {
          const color =
            itemConfig.theme?.[theme] ?? itemConfig.color;
          return color ? `  --color-${key}: ${color};` : "";
        })
        .filter(Boolean) // 빈 문자열 제거
        .join("\n");

      if (!lines) return "";

      // data-chart="id" 선택자에 변수 주입
      return `
${prefix} [data-chart="${props.id}"] {
${lines}
}`;
    })
    .filter(Boolean)
    .join("\n");
});
</script>
