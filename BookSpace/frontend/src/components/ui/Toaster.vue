<!-- src/components/ui/Toaster.vue -->
<script setup>
import { TransitionGroup } from "vue";
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
    <TransitionGroup
      name="toast"
      tag="div"
      class="flex flex-col items-end gap-2 w-full sm:w-auto"
    >
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="w-full sm:w-80 rounded-lg border border-border bg-background text-foreground shadow-lg pointer-events-auto toast-item"
        :data-variant="toast.variant"
        :class="{
          'border-red-500/50 bg-red-50 dark:bg-red-950/20':
            toast.variant === 'destructive',
          'border-blue-500/50 bg-blue-50 dark:bg-blue-950/20':
            toast.variant === 'default',
        }"
      >
        <div class="flex items-start gap-4 px-5 py-4">
          <!-- 알림 이모티콘 -->
          <div class="flex-shrink-0 text-xl">
            <span v-if="toast.variant === 'destructive'">⚠️</span>
            <span v-else class="text-2xl">🍞</span>
          </div>

          <!-- Title + Description 영역 -->
          <div class="flex-1 grid gap-1 min-w-0">
            <p v-if="toast.title" class="text-sm font-semibold leading-tight">
              {{ toast.title }}
            </p>

            <p
              v-if="toast.description"
              class="text-sm text-muted-foreground leading-relaxed"
            >
              {{ toast.description }}
            </p>
          </div>

          <!-- action 영역 (버튼 등 커스텀 컴포넌트) -->
          <div v-if="toast.action" class="ml-2 flex-shrink-0">
            <!-- toast.action이 컴포넌트일 경우 동적으로 랜더링 -->
            <component :is="toast.action" />
          </div>

          <!-- 닫기 버튼 (ToastClose 역할) -->
          <button
            type="button"
            class="ml-2 flex-shrink-0 text-base text-muted-foreground hover:text-foreground transition-colors"
            @click="dismiss(toast.id)"
            aria-label="닫기"
          >
            ✕
          </button>
        </div>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
/* 토스트 애니메이션 - 아래에서 위로 튀어올라오는 효과 */
.toast-enter-active {
  animation: toast-slide-up 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-leave-active {
  animation: toast-slide-down 0.3s ease-in;
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(100px) scale(0.8);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}

/* 토스트가 나타날 때 바운스 효과 */
@keyframes toast-slide-up {
  0% {
    opacity: 0;
    transform: translateY(100px) scale(0.8);
  }
  50% {
    transform: translateY(-10px) scale(1.05);
  }
  70% {
    transform: translateY(5px) scale(0.98);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 토스트가 사라질 때 */
@keyframes toast-slide-down {
  0% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
}

/* 토스트 아이템에 약간의 그림자 효과 추가 */
.toast-item {
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1),
    0 8px 10px -6px rgba(0, 0, 0, 0.1);
}
</style>
