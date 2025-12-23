<template>
  <div class="flex items-start gap-3 max-w-[85%]">
    <!-- Avatar -->
    <div class="flex-shrink-0">
      <ChaekBotProfile size="sm" />
    </div>

    <!-- Typing Animation -->
    <div class="flex-1">
      <div
        class="bg-white border border-gray-200 rounded-2xl rounded-tl-sm px-5 py-4 shadow-sm inline-block"
      >
        <div class="flex gap-1">
          <div class="dot"></div>
          <div class="dot" style="animation-delay: 0.2s"></div>
          <div class="dot" style="animation-delay: 0.4s"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import ChaekBotProfile from "./ChaekBotProfile.vue";
</script>

<style scoped>
.dot {
  width: 8px;
  height: 8px;
  background-color: #9ca3af;
  border-radius: 50%;
  animation: pulse 1.4s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  60%,
  100% {
    opacity: 0.3;
    transform: scale(0.8);
  }
  30% {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
