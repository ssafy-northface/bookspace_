<template>
  <div class="min-h-screen">
    <!-- Hero Section -->
    <section class="relative py-20 overflow-hidden">
      <!-- Background decoration -->
      <div class="absolute inset-0 bg-gradient-to-b from-primary/5 via-transparent to-transparent"></div>
      <div class="absolute top-20 left-1/4 w-72 h-72 bg-primary/10 rounded-full blur-3xl"></div>
      <div class="absolute top-40 right-1/4 w-96 h-96 bg-chart-4/10 rounded-full blur-3xl"></div>

      <div class="container relative z-10">
        <div class="flex flex-col items-center text-center max-w-5xl mx-auto">
          <!-- Main Title -->
          <h1 class="text-5xl md:text-6xl font-bold leading-relaxed mb-6">
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-[#818cf8] via-[#6366f1] to-[#a78bfa]">{{ randomTitle.split(',')[0] }},</span>
            <br class="mb-2" />
            <span class="text-foreground mt-3 inline-block">{{ randomTitle.split(',')[1]?.trim() }}</span>
          </h1>

          <!-- Subtitle -->
          <p class="text-muted-foreground text-lg mb-10 max-w-xl">
            AI가 당신의 감정을 분석하고 가장 필요한 책을 추천해드립니다.
          </p>

          <!-- Search Box -->
          <div class="w-full max-w-3xl">
            <div class="relative group">
              <div class="absolute -inset-0.5 bg-gradient-to-r from-primary/50 to-chart-4/50 rounded-full blur opacity-30 group-hover:opacity-50 transition duration-300"></div>
              <div class="relative bg-card border border-border rounded-full px-6 py-3.5 shadow-lg">
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
                  <Button @click="goToAiRecommend" class="px-6 !rounded-full">
                    AI 추천 받기
                  </Button>
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
      <div class="text-center mb-10">
        <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-2">인기 도서</h2>
        <p class="text-muted-foreground">독자들이 가장 사랑하는 책들을 만나보세요</p>
      </div>

      <!-- Books Grid -->
      <div v-if="isLoadingBooks" class="flex justify-center py-12">
        <Spinner />
      </div>
      <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-6">
        <BookCard v-for="book in popularBooks" :key="book.isbn13" :book="book" />
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

    <!-- Community Section -->
    <section class="py-16 container">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h2 class="text-2xl md:text-3xl font-bold text-foreground mb-2">커뮤니티 토론</h2>
          <p class="text-muted-foreground">독서에서 얻은 인사이트를 공유하세요</p>
        </div>
        <RouterLink :to="{ name: 'community' }">
          <Button variant="outline" class="gap-2">
            전체보기
            <ArrowRight class="w-4 h-4" />
          </Button>
        </RouterLink>
      </div>

      <!-- Posts Grid -->
      <div v-if="isLoadingPosts" class="flex justify-center py-12">
        <Spinner />
      </div>
      <div v-else-if="recentPosts.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div
          v-for="post in recentPosts"
          :key="post.postId"
          @click="goToPost(post.postId)"
          class="group p-5 rounded-xl border border-border bg-card hover:shadow-md hover:border-primary/20 cursor-pointer transition-all duration-200"
        >
          <div class="flex gap-4">
            <div class="flex-1 min-w-0">
              <!-- Category Badge -->
              <Badge variant="secondary" class="mb-2 text-xs">
                {{ post.bookTitle || '자유 토론' }}
              </Badge>
              <!-- Title -->
              <h3 class="font-semibold text-foreground group-hover:text-primary transition-colors line-clamp-1 mb-1">
                {{ post.postTitle }}
              </h3>
              <!-- Content Preview -->
              <p class="text-sm text-muted-foreground line-clamp-2 mb-3">
                {{ post.postContent }}
              </p>
              <!-- Meta -->
              <div class="flex items-center gap-4 text-xs text-muted-foreground">
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
            <!-- Book Cover -->
            <div v-if="post.bookImageUrl" class="flex-shrink-0">
              <img
                :src="post.bookImageUrl"
                :alt="post.bookTitle"
                class="w-16 h-24 object-cover rounded-lg shadow-sm"
              />
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-12 text-muted-foreground">
        아직 게시글이 없습니다. 첫 번째 게시글을 작성해보세요!
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
            <Button size="lg" class="gap-2 px-8">
              <Sparkles class="w-5 h-5" />
              AI 추천 시작하기
            </Button>
          </RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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
  BookOpen
} from 'lucide-vue-next'

const router = useRouter()
const searchQuery = ref('')

// 랜덤 타이틀 멘트
const titleOptions = [
  '오늘의 마음에 맞춘 책 한 권, 같이 고를까요?',
  '지금 기분에 어울리는 책, 같이 찾아볼까요?',
  '지금의 당신에게 필요한 책, 같이 선택해볼까요?',
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
  select: (data) => data?.slice(0, 6) || [],
  staleTime: 1000 * 60 * 5 // 5 minutes
})

// Fetch recent posts
const { data: postsData, isLoading: isLoadingPosts } = useQuery({
  queryKey: ['posts', 'home'],
  queryFn: () => fetchPosts({ pageParam: 0, size: 4 }),
  select: (data) => data?.posts || [],
  staleTime: 1000 * 60 * 2 // 2 minutes
})

const recentPosts = ref([])

onMounted(() => {
  // Watch for posts data
})

// Update recentPosts when postsData changes
import { watch } from 'vue'
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
</style>
