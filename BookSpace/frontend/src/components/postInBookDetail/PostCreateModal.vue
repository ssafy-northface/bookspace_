<template>
  <Teleport to="body">
    <!-- overlay -->
    <div class="fixed inset-0 z-50 flex items-center justify-center" role="dialog" aria-modal="true" @keydown.esc="close" tabindex="-1" ref="root">
      <!-- dim -->
      <div class="absolute inset-0 bg-black/40" @click="close"></div>

      <!-- panel -->
      <div class="relative z-10 w-[560px] max-w-[92vw] rounded-xl bg-background border shadow-xl" @click.stop>
        <!-- header -->
        <div class="flex items-center justify-between px-6 py-4">
          <h3 class="text-lg font-semibold text-foreground">토론 게시글 작성</h3>

          <button type="button" class="rounded-md p-2 text-muted-foreground hover:text-foreground hover:bg-muted transition" aria-label="닫기" @click="close">✕</button>
        </div>

        <!-- body -->
        <div class="px-6 pb-6 space-y-4">
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">제목</label>
            <input
              v-model.trim="title"
              type="text"
              placeholder="게시글 제목을 입력하세요"
              class="w-full h-10 rounded-md border border-input bg-background px-3 text-sm outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50 focus-visible:border-ring"
              maxlength="60"
            />
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground">내용</label>
            <textarea
              v-model.trim="content"
              placeholder="내용을 입력하세요"
              class="w-full min-h-[180px] resize-none rounded-md border border-input bg-background p-3 text-sm outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50 focus-visible:border-ring"
              maxlength="2000"
            />
          </div>

          <button
            type="button"
            class="w-full h-11 rounded-md font-semibold text-white transition disabled:cursor-not-allowed disabled:opacity-50 bg-muted-foreground/60"
            :class="canSubmit ? 'bg-primary hover:bg-primary/90' : ''"
            :disabled="!canSubmit"
            @click="submit"
          >
            게시글 등록
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from "vue";

const emit = defineEmits(["close", "submit"]);

const title = ref("");
const content = ref("");

const canSubmit = computed(() => title.value.length > 0 && content.value.length > 0);

const close = () => emit("close");

const submit = () => {
  if (!canSubmit.value) return;
  emit("submit", { title: title.value, content: content.value });
};

// 모달 뜰 때 키보드 이벤트(ESC) 잘 먹게 포커스
const root = ref(null);
onMounted(async () => {
  await nextTick();
  root.value?.focus?.();
});
</script>
