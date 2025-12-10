<template>
  <teleport to="body">
    <transition name="fade">
      <div
        v-if="open"
        class="fixed inset-0 z-40 bg-black/50"
        data-slot="sheet-overlay"
        @click="open = false"
      />
    </transition>

    <transition :name="`slide-${side}`">
      <aside
        v-if="open"
        data-slot="sheet-content"
        class="bg-background text-foreground fixed z-50 flex max-h-screen flex-col gap-4 shadow-lg transition"
        :class="[panelPosition, props.customClass]"
        @click.stop
      >
        <header class="flex items-start justify-between gap-2 p-4">
          <div class="space-y-1">
            <h2 v-if="title" class="text-lg font-semibold">
              {{ title }}
            </h2>
            <p v-if="description" class="text-sm text-muted-foreground">
              {{ description }}
            </p>
          </div>
          <FwbButton
            v-if="closeButton"
            color="light"
            size="xs"
            aria-label="Close"
            class="shrink-0"
            @click="open = false"
          >
            ✕
          </FwbButton>
        </header>

        <div class="flex-1 overflow-auto p-4">
          <slot />
        </div>

        <footer class="p-4">
          <slot name="footer" />
        </footer>
      </aside>
    </transition>
  </teleport>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-right-enter-active,
.slide-right-leave-active,
.slide-left-enter-active,
.slide-left-leave-active,
.slide-top-enter-active,
.slide-top-leave-active,
.slide-bottom-enter-active,
.slide-bottom-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-right-enter-from,
.slide-right-leave-to {
  transform: translateX(100%);
  opacity: 0.8;
}
.slide-left-enter-from,
.slide-left-leave-to {
  transform: translateX(-100%);
  opacity: 0.8;
}
.slide-top-enter-from,
.slide-top-leave-to {
  transform: translateY(-100%);
  opacity: 0.8;
}
.slide-bottom-enter-from,
.slide-bottom-leave-to {
  transform: translateY(100%);
  opacity: 0.8;
}
</style>

<script setup>
import { computed } from "vue";
import { FwbButton } from "flowbite-vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  side: {
    type: String,
    default: "right",
    validator: (value) => ["left", "right", "top", "bottom"].includes(value),
  },
  title: {
    type: String,
    default: "",
  },
  description: {
    type: String,
    default: "",
  },
  closeButton: {
    type: Boolean,
    default: true,
  },
  class: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue"]);

const open = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emit("update:modelValue", val);
  },
});

const panelPosition = computed(() => {
  switch (props.side) {
    case "left":
      return "left-0 top-0 h-full w-3/4 max-w-sm border-r";
    case "right":
      return "right-0 top-0 h-full w-3/4 max-w-sm border-l";
    case "top":
      return "top-0 left-0 w-full max-h-[85vh] border-b";
    case "bottom":
      return "bottom-0 left-0 w-full max-h-[85vh] border-t";
    default:
      return "";
  }
});
</script>
