<template>
  <div class="flex items-center gap-1">
    <button v-for="i in max" :key="i" type="button" class="relative transition-transform hover:scale-110" @click="(e) => onClickStar(e, i)">
      <!-- 회색 별 -->
      <Star class="h-6 w-6 text-gray-300" :stroke-width="1.5" />

      <!-- 노란 별 (덮기) -->
      <div class="absolute left-0 top-0 overflow-hidden" :style="{ width: fillWidth(i) }">
        <Star class="h-6 w-6 text-yellow-400" :stroke-width="1.5" fill="currentColor" />
      </div>
    </button>

    <span class="ml-2 text-lg text-muted-foreground font-semibold">
      {{ displayValue.toFixed(1) }}
    </span>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { Star } from "lucide-vue-next";

const props = defineProps({
  modelValue: { type: Number, default: 0 },
  max: { type: Number, default: 5 },
  step: { type: Number, default: 0.5 }, // 0.5 or 1
});

const emit = defineEmits(["update:modelValue"]);

const displayValue = computed(() => props.modelValue);

function onClickStar(e, index) {
  if (props.step !== 0.5) {
    emit("update:modelValue", index);
    return;
  }

  const rect = e.currentTarget.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const half = x < rect.width / 2 ? 0.5 : 1;

  emit("update:modelValue", index - 1 + half);
}

function fillWidth(index) {
  const v = displayValue.value;
  const start = index - 1;

  if (v <= start) return "0%";
  if (v >= index) return "100%";

  return `${(v - start) * 100}%`; // 50%
}
</script>
