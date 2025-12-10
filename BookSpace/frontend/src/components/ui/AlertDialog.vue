<template>
  <div v-if="open" data-slot="alert-dialog-portal">
    <div
      data-slot="alert-dialog-overlay"
      class="data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 fixed inset-0 z-50 bg-overlay"
      @click="onCancel"
    />
    <div
      data-slot="alert-dialog-content"
      class="bg-card text-card-foreground border-border data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 fixed top-[50%] left-[50%] z-50 grid w-full max-w-[calc(100%-2rem)] translate-x-[-50%] translate-y-[-50%] gap-4 rounded-lg border p-6 shadow-lg duration-200 sm:max-w-lg"
    >
      <div data-slot="alert-dialog-header" class="flex flex-col gap-2 text-center sm:text-left">
        <slot name="title" />
        <slot name="description" />
      </div>
      <div data-slot="alert-dialog-footer" class="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
        <button
          type="button"
          class="btn btn-outline text-secondary-foreground bg-secondary"
          @click="onCancel"
          data-slot="alert-dialog-cancel"
        >
          <slot name="cancel">취소</slot>
        </button>
        <button
          type="button"
          class="btn text-primary-foreground bg-primary"
          @click="onAction"
          data-slot="alert-dialog-action"
        >
          <slot name="action">확인</slot>
        </button>
      </div>
    </div>
  </div>
  <span v-else>
    <slot name="trigger" :open="open" :show="showDialog" />
  </span>
</template>

<script>
export default {
  name: "AlertDialog",
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
  },
  emits: ["update:modelValue", "action", "cancel"],
  data() {
    return {
      open: this.modelValue,
    };
  },
  watch: {
    modelValue(val) {
      this.open = val;
    },
    open(val) {
      this.$emit("update:modelValue", val);
    },
  },
  methods: {
    showDialog() {
      this.open = true;
    },
    onCancel() {
      this.open = false;
      this.$emit("cancel");
    },
    onAction() {
      this.$emit("action");
      this.open = false;
    },
  },
};
</script>

<style scoped>
/* 기본 스타일 */
.bg-overlay {
  background-color: rgba(0, 0, 0, 0.5);
}

.bg-card {
  background-color: var(--card);
}

.text-card-foreground {
  color: var(--card-foreground);
}

.border-border {
  border-color: var(--border);
}

.bg-primary {
  background-color: var(--primary);
}

.text-primary-foreground {
  color: var(--primary-foreground);
}

.bg-secondary {
  background-color: var(--secondary);
}

.text-secondary-foreground {
  color: var(--secondary-foreground);
}

/* 다크 모드 스타일 */
.dark .bg-card {
  background-color: var(--card);
}

.dark .text-card-foreground {
  color: var(--card-foreground);
}

.dark .border-border {
  border-color: var(--border);
}

.dark .bg-primary {
  background-color: var(--primary);
}

.dark .text-primary-foreground {
  color: var(--primary-foreground);
}

.dark .bg-secondary {
  background-color: var(--secondary);
}

.dark .text-secondary-foreground {
  color: var(--secondary-foreground);
}
</style>