<template>
  <div class="accordion-item" data-slot="accordion-item">
    <button
      class="accordion-trigger"
      @click="toggle"
      :aria-expanded="isOpen"
    >
      <slot name="header" />
      <ChevronDownIcon
        class="text-muted-foreground pointer-events-none size-4 shrink-0 translate-y-0.5 transition-transform duration-200"
        :class="{ 'rotate-180': isOpen }"
      />
    </button>
    <div
      v-show="isOpen"
      class="accordion-content"
      data-slot="accordion-content"
    >
      <slot name="content" />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ChevronDownIcon } from "lucide-vue-next";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue"]);

const isOpen = ref(props.modelValue);

const toggle = () => {
  isOpen.value = !isOpen.value;
  emit("update:modelValue", isOpen.value);
};
</script>

<style scoped>
.accordion-item {
  border-bottom: 1px solid var(--border);
}

.accordion-trigger {
  cursor: pointer;
  background: none;
  border: none;
  padding: 1rem;
  width: 100%;
  text-align: left;
  font-size: 1rem;
  font-weight: bold;
  color: var(--foreground);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.accordion-content {
  padding: 1rem;
  background-color: var(--card);
  font-size: 0.9rem;
  color: var(--card-foreground);
  transition: max-height 0.3s ease-out;
  overflow: hidden;
}
</style>