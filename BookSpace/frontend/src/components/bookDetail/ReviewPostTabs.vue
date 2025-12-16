<template>
  <Tabs2
    v-model="activeTab"
    :tabs="tabItems"
    class="w-full"
    list-class="bg-muted"
    panel-class="mt-6"
    :active-classes="activeClasses"
    :inactive-classes="inactiveClasses"
    instance-id="review-post-tabs"
  >
    <!-- 리뷰 탭 내용 -->
    <template #review>
      <slot name="review" />
    </template>

    <!-- 토론 탭 내용 -->
    <template #post>
      <slot name="post" />
    </template>
  </Tabs2>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import Tabs2 from "@/components/ui/Tabs2.vue";

const props = defineProps({
  // 필요하면 부모에서 v-model로 현재 탭 제어 가능
  modelValue: { type: String, default: "review" },
  reviewCount: { type: Number, default: 0 },
  postCount: { type: Number, default: 0 },
});

const emit = defineEmits(["update:modelValue"]);

const activeTab = ref(props.modelValue);

// 버튼에 들어갈 텍스트
const tabItems = computed(() => [
  { id: "review", label: `리뷰` },
  { id: "post", label: `커뮤니티` },
]);

// 선택된 탭 스타일
const activeClasses = "bg-background text-foreground shadow-sm font-semibold";
const inactiveClasses = "text-foreground";

// v-model 동기화
watch(
  () => props.modelValue,
  (v) => {
    if (v && v !== activeTab.value) activeTab.value = v;
  }
);

watch(activeTab, (v) => {
  emit("update:modelValue", v);
});
</script>
