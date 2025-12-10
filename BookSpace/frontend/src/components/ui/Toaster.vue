<!-- src/components/ui/Toaster.vue -->
<script setup>
import { useToast } from "@/composables/useToast";
const { toasts, dismiss } = useToast();
</script>

<template>
  <!-- ToastViewport 역할: 화면 우하단에 토스트 스택 고정 -->
  <div
    class="fixed inset-x-0 bottom-0 z-50 flex flex-col items-end gap-2 p-4 sm:bottom-4 sm:right-4 sm:left-auto"
    aria-live="assertive"
  >
    <!-- 각 Toast -->
    <div
      v-for="toast in toasts"
      :key="toast.id"
      class="w-full sm:w-80 rounded-md border border-border bg-background text-foreground shadow-lg pointer-events-auto"
      :data-variant="toast.variant"
    >
      <div class="flex items-start gap-3 px-4 py-3">
        <!-- Title + Description 영역 (원본 TSX의 grid gap-1 유지) -->
        <div class="flex-1 grid gap-1">
          <p v-if="toast.title" class="text-sm font-medium leading-none">
            {{ toast.title }}
          </p>

          <p v-if="toast.description" class="text-sm text-muted-foreground">
            {{ toast.description }}
          </p>
        </div>

        <!-- action 영역 (버튼 등 커스텀 컴포넌트) -->
        <div v-if="toast.action" class="ml-2">
          <!-- toast.action이 컴포넌트일 경우 동적으로 랜더링 -->
          <component :is="toast.action" />
        </div>

        <!-- 닫기 버튼 (ToastClose 역할) -->
        <button
          type="button"
          class="ml-2 text-xs text-muted-foreground hover:text-foreground"
          @click="dismiss(toast.id)"
        >
          ✕
        </button>
      </div>
    </div>
  </div>
</template>
