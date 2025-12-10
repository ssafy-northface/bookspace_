<template>
  <div
    :class="alertClass"
    data-slot="alert"
    role="alert"
  >
    <!-- 아이콘 -->
    <slot name="icon" />

    <!-- 타이틀 -->
    <div
      v-if="$slots.title"
      data-slot="alert-title"
      class="col-start-2 line-clamp-1 min-h-4 font-medium tracking-tight"
    >
      <slot name="title" />
    </div>

    <!-- 설명 -->
    <div
      v-if="$slots.description"
      data-slot="alert-description"
      class="text-muted-foreground col-start-2 grid justify-items-start gap-1 text-sm [&_p]:leading-relaxed"
    >
      <slot name="description" />
    </div>

    <!-- 나머지 슬롯 -->
    <slot />
  </div>
</template>

<script>
export default {
  name: "Alert",
  props: {
    variant: {
      type: String,
      default: "default",
      validator: (v) => ["default", "destructive"].includes(v),
    },
    className: {
      type: String,
      default: "",
    },
  },
  computed: {
    alertClass() {
      const base =
        "relative w-full rounded-lg border px-4 py-3 text-sm grid has-[>svg]:grid-cols-[calc(var(--spacing)*4)_1fr] grid-cols-[0_1fr] has-[>svg]:gap-x-3 gap-y-0.5 items-start [&>svg]:size-4 [&>svg]:translate-y-0.5 [&>svg]:text-current";

      const variants = {
        // 라이트/다크 모드 모두 global.css + tailwind color 토큰 사용
        default: "bg-card text-card-foreground border-border",
        destructive:
          "bg-card text-destructive border-destructive [&>svg]:text-destructive",
      };

      return [base, variants[this.variant], this.className].join(" ");
    },
  },
};
</script>

<style scoped>
/* 여기서는 레이아웃 관련 추가 스타일만 두고,
   색 관련(.bg-*, .text-*, .border-*)은 전부 global.css + Tailwind에 맡기는 게 베스트 */
</style>
