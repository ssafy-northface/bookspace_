<template>
  <div class="min-h-screen">
    <!-- SVG gradient definition -->
    <svg class="absolute w-0 h-0">
      <defs>
        <linearGradient id="send-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" style="stop-color:#818cf8;stop-opacity:1" />
          <stop offset="50%" style="stop-color:#6366f1;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#a78bfa;stop-opacity:1" />
        </linearGradient>
      </defs>
    </svg>
    <!-- Hero Section -->
    <section class="relative py-20 overflow-hidden">
      <!-- Background decoration -->
      <div class="absolute inset-0 bg-gradient-to-b from-primary/5 via-transparent to-transparent"></div>
      <div class="absolute top-20 left-1/4 w-72 h-72 bg-primary/10 rounded-full blur-3xl"></div>
      <div class="absolute top-40 right-1/4 w-96 h-96 bg-chart-4/10 rounded-full blur-3xl"></div>

      <div class="container relative z-10">
        <div class="flex flex-col items-center text-center max-w-5xl mx-auto">
          <!-- Main Title -->
          <h1 class="text-4xl md:text-5xl font-bold mb-6">
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-[#818cf8] via-[#6366f1] to-[#a78bfa] whitespace-nowrap block">{{ randomTitle.split(',')[0] }},</span>
            <span class="text-foreground mt-2 md:mt-3 block">{{ randomTitle.split(',')[1]?.trim() }}</span>
          </h1>

          <!-- Subtitle -->
          <p class="text-muted-foreground text-lg mb-10 max-w-xl">
            AI가 당신의 감정을 분석하고<span class="hidden sm:inline"> </span><br class="sm:hidden" /> 가장 필요한 책을 추천해드립니다.
          </p>

          <!-- Search Box -->
          <div class="w-full max-w-3xl">
            <div class="relative group">
              <div class="absolute -inset-0.5 bg-gradient-to-r from-primary/50 to-chart-4/50 rounded-full blur opacity-30 group-hover:opacity-50 transition duration-300"></div>
              <div class="relative bg-gradient-to-r from-[#818cf8] via-[#6366f1] to-[#a78bfa] p-[2px] rounded-full shadow-lg">
                <div class="bg-card rounded-full px-6 py-3.5">
                  <div class="flex items-center gap-3">
                    <Search class="w-5 h-5 text-muted-foreground flex-shrink-0" />
                    <input
                      v-model="searchQuery"
                      type="text"
                      placeholder="지금 기분이 어떠신가요?"
                      class="flex-1 bg-transparent border-none outline-none text-foreground placeholder:text-muted-foreground text-sm"
                      @focus="handleInputFocus"
                      @keyup.enter="goToAiRecommend"
                    />
                    <button @click="goToAiRecommend" class="rounded-full w-10 h-10 p-0 flex items-center justify-center bg-transparent hover:bg-transparent border-none outline-none cursor-pointer send-icon-gradient">
                      <Send class="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Emotion Tags -->
            <div class="flex flex-wrap justify-center gap-3 mt-4">
              <button
                v-for="tag in emotionTags"
                :key="tag.label"
                @click="selectEmotionTag(tag)"
                class="px-6 py-2.5 text-sm rounded-full border border-border bg-card/50 text-muted-foreground hover:border-[#FEE500] hover:text-[#3C1E1E] hover:bg-[#FEE500] transition-all duration-200"
              >
                {{ tag.label }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Popular Books Section -->
    <section class="py-16 container">
      <div class="text-center mb-4">
        <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-4">인기 도서</h2>
        <p class="text-muted-foreground">독자들이 가장 사랑하는 책들을 만나보세요</p>
      </div>

      <!-- More Link -->
      <div class="text-right mb-6">
        <RouterLink 
          :to="{ name: 'books' }" 
          @click="() => sessionStorage.setItem('booksViewIsDirectNavigation', 'true')"
          class="inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-primary transition-colors"
        >
          더보기
          <ArrowRight class="w-4 h-4" />
        </RouterLink>
      </div>

      <!-- Books Carousel -->
      <div v-if="isLoadingBooks" class="flex justify-center py-12">
        <Spinner />
      </div>
      <div 
        v-else-if="popularBooks && popularBooks.length > 0"
        class="relative overflow-hidden"
        @mouseenter="stopBookAutoSlide"
        @mouseleave="startBookAutoSlide"
      >
        <!-- Carousel Container - Grid처럼 보이도록 -->
        <div 
          ref="bookCarouselRef"
          class="flex transition-transform duration-500 ease-in-out gap-6"
          :style="{ transform: `translateX(-${bookCurrentIndex * (bookCardWidth + 24)}px)` }"
        >
          <div
            v-for="(book, index) in displayedBooks"
            :key="`${book.isbn13}-${index}`"
            class="flex-shrink-0"
            :style="{ width: `${bookCardWidth}px` }"
          >
            <BookCard :book="book" />
          </div>
        </div>
      </div>
    </section>

    <!-- Community Section -->
    <section class="py-16 container">
      <div class="text-center mb-4">
        <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-4">커뮤니티</h2>
        <p class="text-muted-foreground">독서에서 얻은 인사이트를 공유하세요</p>
      </div>

      <!-- More Link -->
      <div class="text-right mb-6">
        <RouterLink :to="{ name: 'community' }" class="inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-primary transition-colors">
          더보기
          <ArrowRight class="w-4 h-4" />
        </RouterLink>
      </div>

      <!-- Posts Carousel -->
      <div v-if="isLoadingPosts" class="flex justify-center py-12">
        <Spinner />
      </div>
      <div 
        v-else-if="recentPosts.length > 0" 
        class="relative overflow-hidden"
        @mouseenter="stopPostAutoSlide"
        @mouseleave="startPostAutoSlide"
      >
        <div 
          ref="postCarouselRef"
          class="flex transition-transform duration-500 ease-in-out gap-4"
          :style="{ transform: `translateX(-${postCurrentIndex * (postCardWidth + 16)}px)` }"
        >
          <div
            v-for="(post, index) in displayedPosts"
            :key="`${post.postId}-${index}`"
            @click="goToPost(post.postId)"
            class="flex-shrink-0 group p-5 rounded-xl border border-border bg-card hover:shadow-md hover:border-primary/20 cursor-pointer transition-all duration-200 flex flex-col"
            :style="{ width: `${postCardWidth}px`, minHeight: '200px' }"
          >
            <div class="flex gap-4 flex-1 min-h-0">
              <div class="flex-1 min-w-0 flex flex-col">
                <!-- Title -->
                <h3 class="font-semibold text-foreground group-hover:text-primary transition-colors line-clamp-1 mb-1">
                  {{ post.postTitle }}
                </h3>
                <!-- Content Preview -->
                <p class="text-sm text-muted-foreground line-clamp-3 flex-1 mb-3">
                  {{ post.postContent }}
                </p>
              </div>
              <!-- Book Cover -->
              <div v-if="post.bookImageUrl" class="flex-shrink-0">
                <img
                  :src="post.bookImageUrl"
                  :alt="post.bookTitle"
                  class="w-16 h-24 object-cover rounded-lg shadow-sm"
                />
              </div>
            </div>
            <!-- Meta - 하단 고정 -->
            <div class="flex items-center gap-4 text-xs text-muted-foreground mt-auto pt-2">
              <span class="flex items-center gap-1">
                <Heart class="w-3.5 h-3.5" />
                {{ post.likeCount || 0 }}
              </span>
              <span class="flex items-center gap-1">
                <MessageSquare class="w-3.5 h-3.5" />
                {{ post.commentCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-12 text-muted-foreground">
        아직 게시글이 없습니다. 첫 번째 게시글을 작성해보세요!
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-16 bg-muted/30">
      <div class="container">
        <div class="text-center mb-12">
          <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-2">왜 Bookspace인가요?</h2>
          <p class="text-muted-foreground">독서 경험을 한 단계 끌어올리는 특별한 기능들</p>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div
            v-for="feature in features"
            :key="feature.title"
            class="group p-6 rounded-2xl bg-card border border-border hover:border-primary/30 hover:shadow-lg transition-all duration-300"
          >
            <div class="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center mb-4 group-hover:bg-primary/20 transition-colors">
              <component :is="feature.icon" class="w-6 h-6 text-primary" />
            </div>
            <h3 class="text-lg font-semibold text-foreground mb-2">{{ feature.title }}</h3>
            <p class="text-sm text-muted-foreground leading-relaxed">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-20 bg-gradient-to-b from-muted/50 to-background">
      <div class="container">
        <div class="text-center max-w-2xl mx-auto">
          <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-4">
            지금 바로 AI 도서 추천을 받아보세요
          </h2>
          <p class="text-muted-foreground mb-8">
            당신만을 위한 맞춤 도서 추천, Bookspace와 함께 시작하세요
          </p>
          <RouterLink :to="{ name: 'aiRecommend' }">
            <button 
              @click="goToAiRecommend"
              class="inline-flex items-center justify-center gap-2 px-8 h-12 rounded-full text-base font-medium text-white bg-gradient-to-r from-[#818cf8] via-[#6366f1] to-[#a78bfa] hover:opacity-90 transition-all duration-200 shadow-lg hover:shadow-xl"
            >
              <Sparkles class="w-5 h-5" />
              AI 도서 추천받기
            </button>
          </RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { fetchDefaultBooks } from '@/api/bookApi'
import { fetchPosts } from '@/api/postApi'
import { useRequireAuth } from '@/composables/useRequireAuth'
import { useToast } from '@/composables/useToast'

// Components
import Badge from '@/components/ui/Badge.vue'
import Button from '@/components/ui/Button.vue'
import Spinner from '@/components/ui/Spinner.vue'
import BookCard from '@/components/book/BookCard.vue'

// Icons
import {
  Sparkles,
  Search,
  ArrowRight,
  Heart,
  MessageSquare,
  Brain,
  TrendingUp,
  Users,
  BookOpen,
  Send,
  ChevronLeft,
  ChevronRight
} from 'lucide-vue-next'

const router = useRouter()
const searchQuery = ref('')

// 랜덤 타이틀 멘트
const titleOptions = [
  '오늘의 마음에 맞춘 책 한 권, 같이 고를까요?',
  '지금 기분에 어울리는 책, 같이 찾아볼까요?',
  '지금 당신에게 필요한 책, 같이 선택해볼까요?',
  '오늘의 기분을 닮은 책, 같이 찾아볼까요?'
]
const randomTitle = ref(titleOptions[Math.floor(Math.random() * titleOptions.length)])

// 로그인 필요 기능 체크
const { requireAuth, isLoggedIn, redirectToLogin } = useRequireAuth()
const { toast } = useToast()

// Input focus 시 로그인 체크
const handleInputFocus = (event) => {
  if (!isLoggedIn.value) {
    event.target.blur()
    toast({
      title: '로그인 후 이용가능한 서비스입니다',
      description: '로그인 후 이용해주세요.'
    })
    redirectToLogin()
  }
}

// Emotion tags for quick selection
const emotionTags = [
  { label: '행복', query: '기분이 좋을 때 읽을 책' },
  { label: '슬픔', query: '슬플 때 위로가 되는 책' },
  { label: '지적 호기심', query: '지식을 넓힐 수 있는 책' },
  { label: '재미있는 시간', query: '재미있게 시간 보낼 수 있는 책' },
  { label: '철학적 사고', query: '깊이 생각하게 만드는 철학책' }
]

// Features data
const features = [
  {
    icon: Brain,
    title: 'AI 감성 분석',
    description: '당신의 현재 감정과 상황을 분석하여 가장 적합한 책을 추천합니다.'
  },
  {
    icon: TrendingUp,
    title: '실시간 랭킹',
    description: '실시간 독자들의 평가와 반응을 바탕으로 인기 도서를 선별합니다.'
  },
  {
    icon: Users,
    title: '활발한 커뮤니티',
    description: '같은 책을 읽은 독자들과 생각을 나누고 토론할 수 있습니다.'
  },
  {
    icon: BookOpen,
    title: '맞춤형 큐레이션',
    description: '당신의 독서 취향과 이력을 분석하여 개인화된 추천을 제공합니다.'
  }
]

// Fetch popular books
const { data: popularBooks, isLoading: isLoadingBooks } = useQuery({
  queryKey: ['books', 'bestseller'],
  queryFn: () => fetchDefaultBooks({ type: 'bestseller' }),
  select: (data) => data?.slice(0, 10) || [], // 10개
  staleTime: 1000 * 60 * 5 // 5 minutes
})

// 인기 도서 캐러셀 관련 상태
const bookCarouselRef = ref(null)
const bookCurrentIndex = ref(0)
const bookCardWidth = ref(180) // 기본 카드 너비
let bookAutoSlideInterval = null

// 무한 루프를 위한 도서 목록 (앞뒤로 복제)
const displayedBooks = computed(() => {
  if (!popularBooks.value || popularBooks.value.length === 0) return []
  const books = popularBooks.value
  // 무한 루프를 위해 충분히 복제
  return [...books, ...books, ...books]
})

// 화면 크기에 따른 카드 너비 계산
const calculateBookCardWidth = () => {
  if (!bookCarouselRef.value) {
    bookCardWidth.value = 180
    return
  }
  
  const container = bookCarouselRef.value.parentElement
  if (!container) {
    bookCardWidth.value = 180
    return
  }
  
  const containerWidth = container.offsetWidth || window.innerWidth
  const gap = 24 // gap-6 = 24px
  
  // 반응형: 모바일 2개, sm 3개, md 4개, lg 6개
  let cardsPerView = 2
  if (window.innerWidth >= 1024) cardsPerView = 6
  else if (window.innerWidth >= 768) cardsPerView = 4
  else if (window.innerWidth >= 640) cardsPerView = 3
  
  const calculatedWidth = (containerWidth - (gap * (cardsPerView - 1))) / cardsPerView
  bookCardWidth.value = calculatedWidth > 0 ? calculatedWidth : 180
}


// 인기 도서 자동 슬라이딩 시작
const startBookAutoSlide = () => {
  if (!popularBooks.value || popularBooks.value.length === 0) return
  
  bookAutoSlideInterval = setInterval(() => {
    bookCurrentIndex.value++
    // 원본 배열 길이만큼 이동했으면 처음으로 리셋
    if (bookCurrentIndex.value >= popularBooks.value.length) {
      const carousel = bookCarouselRef.value
      if (carousel) {
        carousel.style.transition = 'none'
        bookCurrentIndex.value = 0
        requestAnimationFrame(() => {
          requestAnimationFrame(() => {
            if (carousel) {
              carousel.style.transition = ''
            }
          })
        })
      }
    }
  }, 5000) // 5초마다 슬라이드
}

// 인기 도서 자동 슬라이딩 중지
const stopBookAutoSlide = () => {
  if (bookAutoSlideInterval) {
    clearInterval(bookAutoSlideInterval)
    bookAutoSlideInterval = null
  }
}

// Fetch recent posts (더 많은 게시글 가져오기)
const { data: postsData, isLoading: isLoadingPosts } = useQuery({
  queryKey: ['posts', 'home'],
  queryFn: () => fetchPosts({ pageParam: 0, size: 12 }), // 12개로 증가
  select: (data) => data?.posts || [],
  staleTime: 1000 * 60 * 2 // 2 minutes
})

const recentPosts = ref([])

// 커뮤니티 캐러셀 관련 상태
const postCarouselRef = ref(null)
const postCurrentIndex = ref(0)
const postCardWidth = ref(500) // 기본 카드 너비
let postAutoSlideInterval = null

// 무한 루프를 위한 게시글 목록 (앞뒤로 복제)
const displayedPosts = computed(() => {
  if (!recentPosts.value || recentPosts.value.length === 0) return []
  const posts = recentPosts.value
  return [...posts, ...posts, ...posts]
})

// 화면 크기에 따른 게시글 카드 너비 계산
const calculatePostCardWidth = () => {
  if (!postCarouselRef.value) {
    postCardWidth.value = 500
    return
  }
  
  const container = postCarouselRef.value.parentElement
  if (!container) {
    postCardWidth.value = 500
    return
  }
  
  const containerWidth = container.offsetWidth || window.innerWidth
  const gap = 16 // gap-4 = 16px
  
  // 반응형: 모바일 1개, md 2개
  let cardsPerView = 1
  if (window.innerWidth >= 768) cardsPerView = 2
  
  const calculatedWidth = (containerWidth - (gap * (cardsPerView - 1))) / cardsPerView
  postCardWidth.value = calculatedWidth > 0 ? calculatedWidth : 500
}

// 커뮤니티 자동 슬라이딩 시작
const startPostAutoSlide = () => {
  if (!recentPosts.value || recentPosts.value.length === 0) return
  
  postAutoSlideInterval = setInterval(() => {
    postCurrentIndex.value++
    if (postCurrentIndex.value >= recentPosts.value.length) {
      const carousel = postCarouselRef.value
      if (carousel) {
        carousel.style.transition = 'none'
        postCurrentIndex.value = 0
        requestAnimationFrame(() => {
          requestAnimationFrame(() => {
            if (carousel) {
              carousel.style.transition = ''
            }
          })
        })
      }
    }
  }, 4000) // 4초마다 슬라이드
}

// 커뮤니티 자동 슬라이딩 중지
const stopPostAutoSlide = () => {
  if (postAutoSlideInterval) {
    clearInterval(postAutoSlideInterval)
    postAutoSlideInterval = null
  }
}

onMounted(async () => {
  await nextTick()
  calculatePostCardWidth()
  calculateBookCardWidth()
  
  window.addEventListener('resize', () => {
    calculatePostCardWidth()
    calculateBookCardWidth()
  })
  
  // 인기 도서 자동 슬라이딩 시작
  watch(popularBooks, (books) => {
    if (books && books.length > 0) {
      nextTick(() => {
        calculateBookCardWidth()
        startBookAutoSlide()
      })
    }
  }, { immediate: true })
  
  // 커뮤니티 게시글 자동 슬라이딩 시작
  watch(recentPosts, (posts) => {
    if (posts && posts.length > 0) {
      nextTick(() => {
        calculatePostCardWidth()
        startPostAutoSlide()
      })
    }
  }, { immediate: true })
})

onUnmounted(() => {
  stopBookAutoSlide()
  stopPostAutoSlide()
  window.removeEventListener('resize', calculatePostCardWidth)
  window.removeEventListener('resize', calculateBookCardWidth)
})

// Update recentPosts when postsData changes
watch(
  () => postsData.value,
  (newData) => {
    recentPosts.value = newData || []
  },
  { immediate: true }
)

// Navigation methods
const goToAiRecommend = requireAuth(() => {
  if (searchQuery.value.trim()) {
    router.push({
      name: 'aiRecommend',
      query: { prompt: searchQuery.value }
    })
  } else {
    router.push({ name: 'aiRecommend' })
  }
}, { loginMessage: '로그인 후 이용가능한 서비스입니다' })

const selectEmotionTag = requireAuth((tag) => {
  searchQuery.value = tag.query
  if (searchQuery.value.trim()) {
    router.push({
      name: 'aiRecommend',
      query: { prompt: searchQuery.value }
    })
  } else {
    router.push({ name: 'aiRecommend' })
  }
}, { loginMessage: '로그인 후 이용가능한 서비스입니다' })

const goToPost = (postId) => {
  router.push({ name: 'postDetail', params: { postId } })
}
</script>

<style scoped>
/* Smooth animations */
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

/* Send icon gradient - using CSS custom properties for gradient effect */
.send-icon-gradient {
  position: relative;
}

.send-icon-gradient :deep(svg) {
  stroke: #818cf8;
  stroke-width: 2;
  transition: stroke 0.3s ease;
}

.send-icon-gradient:hover :deep(svg) {
  stroke: #6366f1;
}
</style>
