<template>
  <!-- AI 추천 플로팅 버튼 (드래그 가능) -->
  <div
    ref="floatingBtn"
    class="fixed z-50 group"
    :style="{ bottom: position.bottom + 'px', right: position.right + 'px' }"
    @mousedown="startDrag"
    @touchstart="startDrag"
  >
    <!-- 호버 시 말풍선 -->
    <div 
      v-show="isHovered && !isDragging"
      class="absolute right-full mr-4 top-1/2 -translate-y-1/2"
    >
      <div class="relative bg-white dark:bg-slate-800 text-gray-800 dark:text-gray-100 text-sm font-medium px-4 py-2.5 rounded-2xl shadow-lg whitespace-nowrap animate-fade-in">
        도움이 필요하신가요? 📚
        <!-- 말풍선 꼬리 -->
        <div class="absolute top-1/2 -translate-y-1/2 -right-2 w-0 h-0 border-t-8 border-t-transparent border-b-8 border-b-transparent border-l-8 border-l-white dark:border-l-slate-800"></div>
      </div>
    </div>

    <!-- 버튼 -->
    <RouterLink
      v-if="!isDragging"
      to="/ai-recommend"
      class="w-14 h-14 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 shadow-lg hover:shadow-xl hover:scale-110 transition-all duration-300 flex items-center justify-center overflow-hidden cursor-pointer"
      aria-label="AI 도서 추천"
      @mouseenter="isHovered = true"
      @mouseleave="isHovered = false"
    >
      <img 
        src="/images/chaekbot.gif" 
        alt="AI 추천"
        class="w-[85%] h-[85%] object-cover rounded-full pointer-events-none"
      />
    </RouterLink>

    <!-- 드래그 중일 때는 링크 대신 div -->
    <div
      v-else
      class="w-14 h-14 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 shadow-xl scale-110 flex items-center justify-center overflow-hidden cursor-grabbing"
    >
      <img 
        src="/images/chaekbot.gif" 
        alt="AI 추천"
        class="w-[85%] h-[85%] object-cover rounded-full pointer-events-none"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';

// 상태
const isHovered = ref(false);
const isDragging = ref(false);
const floatingBtn = ref(null);

// 위치 (localStorage에서 복원)
const position = reactive({
  bottom: 96, // 기본값: bottom-24 (24 * 4 = 96px)
  right: 24,  // 기본값: right-6 (6 * 4 = 24px)
});

// 드래그 시작 위치
let startX = 0;
let startY = 0;
let startRight = 0;
let startBottom = 0;

// 드래그 시작
const startDrag = (e) => {
  // 터치 이벤트 처리
  const clientX = e.touches ? e.touches[0].clientX : e.clientX;
  const clientY = e.touches ? e.touches[0].clientY : e.clientY;

  startX = clientX;
  startY = clientY;
  startRight = position.right;
  startBottom = position.bottom;

  // 드래그 이벤트 리스너 등록
  document.addEventListener('mousemove', onDrag);
  document.addEventListener('mouseup', endDrag);
  document.addEventListener('touchmove', onDrag);
  document.addEventListener('touchend', endDrag);
};

// 드래그 중
const onDrag = (e) => {
  const clientX = e.touches ? e.touches[0].clientX : e.clientX;
  const clientY = e.touches ? e.touches[0].clientY : e.clientY;

  const deltaX = startX - clientX;
  const deltaY = startY - clientY;

  // 일정 거리 이상 움직이면 드래그 모드 활성화
  if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
    isDragging.value = true;
  }

  if (isDragging.value) {
    // 새 위치 계산
    let newRight = startRight + deltaX;
    let newBottom = startBottom + deltaY;

    // 화면 경계 제한
    const btnSize = 56; // 버튼 크기 (w-14 = 56px)
    const maxRight = window.innerWidth - btnSize - 10;
    const maxBottom = window.innerHeight - btnSize - 10;

    newRight = Math.max(10, Math.min(newRight, maxRight));
    newBottom = Math.max(10, Math.min(newBottom, maxBottom));

    position.right = newRight;
    position.bottom = newBottom;
  }
};

// 드래그 종료
const endDrag = () => {
  document.removeEventListener('mousemove', onDrag);
  document.removeEventListener('mouseup', endDrag);
  document.removeEventListener('touchmove', onDrag);
  document.removeEventListener('touchend', endDrag);

  // 위치 저장
  if (isDragging.value) {
    localStorage.setItem('aiFloatingBtnPosition', JSON.stringify(position));
  }

  // 약간의 딜레이 후 드래그 모드 해제 (클릭 방지)
  setTimeout(() => {
    isDragging.value = false;
  }, 100);
};

// 저장된 위치 복원
onMounted(() => {
  const saved = localStorage.getItem('aiFloatingBtnPosition');
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      position.right = parsed.right;
      position.bottom = parsed.bottom;
    } catch (e) {
      // 파싱 실패 시 기본값 유지
    }
  }
});

// 클린업
onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag);
  document.removeEventListener('mouseup', endDrag);
  document.removeEventListener('touchmove', onDrag);
  document.removeEventListener('touchend', endDrag);
});
</script>

<style scoped>
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateX(10px) translateY(-50%);
  }
  to {
    opacity: 1;
    transform: translateX(0) translateY(-50%);
  }
}

.animate-fade-in {
  animation: fade-in 0.2s ease-out;
}
</style>

