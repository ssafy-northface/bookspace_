<template>
  <Tabs2
    v-model="activeTab"
    :tabs="tabItems"
    class="w-full"
    list-class="bg-muted"
    panel-class="mt-6"
    :active-classes="activeClasses"
    :inactive-classes="inactiveClasses"
    instance-id="my-page-tabs"
  >
    <template #wish>
      <slot name="wish" />
    </template>
    <template #review>
      <slot name="review" />
    </template>
    <template #post>
      <slot name="post" />
    </template>
  </Tabs2>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import Tabs2 from "@/components/ui/Tabs2.vue";

const props = defineProps({
  modelValue: { type: String, default: "wish" },
  wishCount: { type: Number, default: 0 },
  reviewCount: { type: Number, default: 0 },
  postCount: { type: Number, default: 0 },
});

const emit = defineEmits(["update:modelValue"]);
const activeTab = ref(props.modelValue);

const formatLabel = (label, count) => {
  if (!count) return label;
  return `${label} (${count})`;
};

const tabItems = computed(() => [
  { id: "wish", label: formatLabel("찜 목록", props.wishCount) },
  { id: "review", label: formatLabel("내 리뷰", props.reviewCount) },
  { id: "post", label: formatLabel("내 게시글", props.postCount) },
]);

const activeClasses = "bg-background text-foreground shadow-sm font-semibold";
const inactiveClasses = "text-foreground";

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
