<template>
  <section
    class="relative p-6 overflow-hidden border shadow-sm rounded-2xl border-border/80 bg-gradient-to-br from-blue-50 via-white to-purple-50 md:p-8 dark:from-slate-900/60 dark:via-slate-900 dark:to-slate-800/60"
  >
    <div
      class="absolute top-0 w-32 h-32 rounded-full pointer-events-none -left-10 bg-primary/15 blur-3xl"
    />
    <div
      class="absolute right-0 rounded-full pointer-events-none -bottom-10 h-36 w-36 bg-sky-400/15 blur-3xl"
    />

    <div
      class="relative flex flex-col gap-6 md:flex-row md:items-center md:justify-between"
    >
      <div class="flex items-center gap-4">
        <UserProfileImg :name="displayName" size-class="w-16 h-16" />
        <div>
          <p class="text-sm text-muted-foreground">{{ subtitle }}</p>
          <h2 class="text-2xl font-bold text-foreground md:text-3xl">
            {{ displayName }}
          </h2>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from "vue";
import UserProfileImg from "@/components/common/UserProfileImg.vue";

const props = defineProps({
  userId: {
    type: Number,
    required: true,
  },
  nickname: {
    type: String,
    required: true,
  },
});

const displayName = computed(() => {
  return props.nickname || "user";
});

const subtitle = computed(() => {
  return "오늘도 좋은 책과 함께 해요";
});
</script>
