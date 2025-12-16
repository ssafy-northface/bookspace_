<template>
  <div
    class="overflow-hidden rounded-full bg-muted text-foreground flex items-center justify-center"
    :class="[sizeClass, className]"
    aria-hidden="true"
  >
    <img
      v-if="src"
      :src="src"
      :alt="alt"
      class="object-cover w-full h-full"
      @error="onError"
    />
    <span v-else class="text-sm font-semibold">{{ initials }}</span>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";

const props = defineProps({
  src: {
    type: String,
    default: "",
  },
  name: {
    type: String,
    default: "",
  },
  alt: {
    type: String,
    default: "user profile",
  },
  sizeClass: {
    type: String,
    default: "w-10 h-10",
  },
  className: {
    type: String,
    default: "",
  },
});

const hasError = ref(false);

const onError = () => {
  hasError.value = true;
};

const initials = computed(() => {
  if (hasError.value || !props.name) return "유저";
  return props.name.trim().slice(0, 2) || "유저";
});
</script>
