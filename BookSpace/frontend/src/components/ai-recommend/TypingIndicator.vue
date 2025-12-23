<template>
  <div class="flex items-start gap-3 max-w-[85%]">
    <!-- Avatar -->
    <div class="flex-shrink-0 w-9 h-9 rounded-full bg-gradient-to-br from-yellow-400 to-yellow-500 flex items-center justify-center shadow-sm">
      <svg class="w-5 h-5 text-gray-800" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
        <path d="M12 6.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0ZM8.5 6.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0ZM15.5 6.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0Z"/>
        <path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2Z"/>
        <path d="M8 14s1.5 2 4 2 4-2 4-2"/>
      </svg>
    </div>
    
    <!-- Typing Animation -->
    <div class="flex-1">
      <div class="text-xs text-gray-500 mb-1 font-medium">AI 도서 추천</div>
      <div class="bg-white border border-gray-200 rounded-2xl rounded-tl-sm px-4 py-3 shadow-sm inline-block">
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
</script>

<style scoped>
.dot {
  width: 8px;
  height: 8px;
  background-color: #9CA3AF;
  border-radius: 50%;
  animation: pulse 1.4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 60%, 100% {
    opacity: 0.3;
    transform: scale(0.8);
  }
  30% {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
