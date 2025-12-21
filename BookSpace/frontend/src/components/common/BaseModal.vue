<template>
  <Teleport to="body">
    <div
      v-if="visible"
      class="fixed inset-0 z-50 flex items-start justify-center px-4 py-10 sm:py-14 bg-black/40 backdrop-blur-sm"
      @keydown.esc="emitClose"
      tabindex="-1"
      ref="rootEl"
    >
      <div class="absolute inset-0" @click="emitClose"></div>
      <div
        class="relative w-full max-h-[90vh] overflow-y-auto rounded-xl border border-border bg-background shadow-2xl"
        :class="maxWidth"
      >
        <button
          type="button"
          class="absolute top-3 right-3 inline-flex h-9 w-9 items-center justify-center rounded-full text-muted-foreground hover:bg-muted hover:text-foreground transition"
          aria-label="모달 닫기"
          @click="emitClose"
        >
          ✕
        </button>

        <div class="p-4 sm:p-6 lg:p-8">
          <slot />
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  maxWidth: {
    type: String,
    default: "max-w-5xl",
  },
});

const emit = defineEmits(["close"]);
const rootEl = ref(null);

const handleKeydown = (e) => {
  if (e.key === "Escape") emitClose();
};

const emitClose = () => emit("close");

onMounted(() => {
  window.addEventListener("keydown", handleKeydown);
});

onBeforeUnmount(() => {
  window.removeEventListener("keydown", handleKeydown);
});
</script>
