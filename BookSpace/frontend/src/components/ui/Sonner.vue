<template>
  <div :class="containerClasses">
    <FwbToast
      v-for="toast in items"
      :key="toast.id"
      :type="toast.type || 'default'"
      class="pointer-events-auto min-w-[260px]"
    >
      <div class="flex w-full items-start justify-between gap-2">
        <div>
          <p v-if="toast.title" class="font-semibold text-sm">
            {{ toast.title }}
          </p>
          <p class="text-sm text-muted-foreground">
            {{ toast.description || toast.message }}
          </p>
        </div>
        <button
          type="button"
          class="text-sm text-muted-foreground hover:text-foreground"
          @click="onClose(toast.id)"
        >
          ✕
        </button>
      </div>
    </FwbToast>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { FwbToast } from "flowbite-vue";

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  },
  position: {
    type: String,
    default: "bottom-right",
    validator: (value) =>
      [
        "top-left",
        "top-right",
        "bottom-left",
        "bottom-right",
        "top-center",
        "bottom-center",
      ].includes(value),
  },
  class: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["close"]);

const containerClasses = computed(() => {
  const base = "fixed z-50 flex flex-col gap-3 p-4 pointer-events-none";
  const map = {
    "top-left": "top-0 left-0 items-start",
    "top-right": "top-0 right-0 items-end",
    "bottom-left": "bottom-0 left-0 items-start",
    "bottom-right": "bottom-0 right-0 items-end",
    "top-center": "top-0 left-1/2 -translate-x-1/2 items-center",
    "bottom-center": "bottom-0 left-1/2 -translate-x-1/2 items-center",
  };
  return [base, map[props.position], props.class].join(" ");
});

function onClose(id) {
  emit("close", id);
}
</script>
