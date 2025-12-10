<script setup>
import { ref } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Keyboard } from 'swiper/modules'

// Swiper CSS
import 'swiper/css'

// Props
const props = defineProps({
  // 슬라이드에 렌더링할 데이터 배열
  items: {
    type: Array,
    required: true,
  },
  // 가로 / 세로
  orientation: {
    type: String,
    default: 'horizontal', // 'horizontal' | 'vertical'
  },
  // 무한 루프
  loop: {
    type: Boolean,
    default: false,
  },
  // 슬라이드 사이 간격(px)
  spaceBetween: {
    type: Number,
    default: 16,
  },
  // 한 화면에 보이는 슬라이드 개수
  slidesPerView: {
    type: [Number, String],
    default: 1,
  },
  // 좌우 화살표 버튼 표시 여부
  controls: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['swiperInit', 'slideChange'])

const swiper = ref(null)

const onSwiper = (instance) => {
  swiper.value = instance
  emit('swiperInit', instance)
}

const onSlideChange = (instance) => {
  emit('slideChange', instance)
}

// 커스텀 화살표에서 호출할 함수
const slidePrev = () => {
  swiper.value && swiper.value.slidePrev()
}

const slideNext = () => {
  swiper.value && swiper.value.slideNext()
}
</script>

<template>
  <div
    class="relative"
    role="region"
    aria-roledescription="carousel"
    data-slot="carousel"
  >
    <!-- 실제 슬라이더 -->
    <Swiper
      :modules="[Keyboard]"
      :direction="orientation === 'vertical' ? 'vertical' : 'horizontal'"
      :loop="loop"
      :space-between="spaceBetween"
      :slides-per-view="1"
      :keyboard="{ enabled: true }"
      class="overflow-hidden px-10 md:px-16"
      @swiper="onSwiper"
      @slideChange="onSlideChange"
    >
      <!-- 각 슬라이드 (CarouselItem 역할) -->
      <SwiperSlide
        v-for="(item, index) in items"
        :key="index"
        role="group"
        aria-roledescription="slide"
        data-slot="carousel-item"
        class="min-w-0 shrink-0 grow-0 basis-full"
      >
        <div class="h-full w-full">
          <!-- 사용자 정의 슬롯: item, index 넘겨줌 -->
          <slot name="item" :item="item" :index="index">
            <!-- 기본 UI (슬롯 안 쓰면 이게 보임) -->
            <div class="p-4 rounded-xl bg-card text-card-foreground shadow">
              {{ item }}
            </div>
          </slot>
        </div>
      </SwiperSlide>
    </Swiper>

    <!-- 커스텀 Prev / Next 버튼 (텍스트 화살표 버전) -->
<template v-if="controls">
  <!-- Prev -->
  <button
    type="button"
    class="absolute left-4 top-1/2 -translate-y-1/2 z-10 text-3xl font-bold text-foreground hover:text-muted-foreground"
    @click="slidePrev"
  >
    <span class="sr-only">Previous slide</span>
    ‹
  </button>

  <!-- Next -->
  <button
    type="button"
    class="absolute right-4 top-1/2 -translate-y-1/2 z-10 text-3xl font-bold text-foreground hover:text-muted-foreground"
    @click="slideNext"
  >
    <span class="sr-only">Next slide</span>
    ›
  </button>
</template>

  </div>
</template>
