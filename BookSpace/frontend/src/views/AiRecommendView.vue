<template>
  <div class="ai-recommend-view">
    <!-- Header -->
    <header class="fixed top-16 left-0 right-0 z-10">
      <div class="flex items-center justify-between px-4 py-3 max-w-2xl mx-auto">
        <BackButton />
        <h1 class="text-lg font-semibold text-foreground">AI 도서 추천</h1>
        <div class="w-9"></div> <!-- Spacer for centering -->
      </div>
    </header>

    <!-- Chat Container -->
    <main class="pt-32 pb-32 overflow-y-auto scrollbar-hide" style="height: calc(100vh - 64px);">
      <div class="max-w-2xl mx-auto px-4 py-6 space-y-4">
        <!-- Welcome Message -->
        <div v-if="messages.length === 0" 
             :class="[
               'transition-all duration-500 ease-in-out py-12',
               isInputActive ? 'flex items-start gap-3' : 'text-center'
             ]">
          <!-- Robot Profile -->
          <div :class="[
            'transition-all duration-500 ease-in-out',
            isInputActive ? 'flex-shrink-0' : 'inline-block'
          ]">
            <ChaekBotProfile :size="isInputActive ? 'md' : 'xl'" />
          </div>
          
          <!-- Welcome Text / Chat Bubble -->
          <div :class="[
            'transition-all duration-500 ease-in-out',
            isInputActive ? 'flex-1 opacity-100 translate-x-0' : 'opacity-100 translate-x-0'
          ]">
            <!-- Chat Bubble (when typing) -->
            <div v-if="isInputActive" 
                 class="bg-muted rounded-2xl rounded-tl-sm px-4 py-3 shadow-sm animate-fade-in">
              <p class="text-sm text-foreground leading-relaxed">
                안녕하세요! 👋<br>
                책봇이 당신의 현재 감정에 맞는 책을 추천해드립니다.<br>
                어떤 책을 찾고 계신가요?
              </p>
            </div>
            
            <!-- Centered Welcome (default) -->
            <div v-else class="animate-fade-in">
              <h2 class="text-xl font-bold foreground mb-2">안녕하세요! 👋</h2>
              <p class="text-sm foreground mb-6">
                책봇이 당신의 현재 감정에 맞는 책을 추천해드립니다.<br>
                어떤 책을 찾고 계신가요?
              </p>
            </div>
          </div>
        </div>

        <!-- Messages -->
        <ChatMessage
          v-for="(message, index) in messages"
          :key="index"
          :message="message"
        />

        <!-- Typing Indicator -->
        <TypingIndicator v-if="isTyping" />

        <!-- Scroll anchor -->
        <div ref="scrollAnchor"></div>
      </div>
    </main>

    <!-- Bottom Input Area -->
    <footer class="fixed bottom-0 left-0 right-0">
      <div class="max-w-2xl mx-auto">
        <!-- Suggested Questions -->
        <div class="px-4 pt-3">
          <SuggestedQuestions
            :visible="showSuggestions"
            :questions="suggestedQuestions"
            @select="handleSuggestedQuestion"
          />
        </div>

        <!-- Chat Input -->
        <ChatInput
          ref="chatInputRef"
          :disabled="isTyping"
          @send="handleSendMessage"
          @input-active="isInputActive = true"
          @input-inactive="isInputActive = false"
        />
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import ChatMessage from '@/components/ai-recommend/ChatMessage.vue'
import ChatInput from '@/components/ai-recommend/ChatInput.vue'
import SuggestedQuestions from '@/components/ai-recommend/SuggestedQuestions.vue'
import TypingIndicator from '@/components/ai-recommend/TypingIndicator.vue'
import BackButton from '@/components/common/BackButton.vue'
import ChaekBotProfile from '@/components/ai-recommend/ChaekBotProfile.vue'

// State
const messages = ref([])
const isTyping = ref(false)
const showSuggestions = ref(true)
const scrollAnchor = ref(null)
const isInputActive = ref(false)

const suggestedQuestions = [
  '오늘 기분에 맞는 책을 추천해줘',
  '최근 베스트셀러 알려줘',
  '자기계발서 추천해줘',
  '감동적인 소설 찾아줘'
]

// Methods

const scrollToBottom = () => {
  nextTick(() => {
    scrollAnchor.value?.scrollIntoView({ behavior: 'smooth' })
  })
}

const handleSendMessage = async (content) => {
  if (!content.trim()) return

  // Hide suggestions after first message
  showSuggestions.value = false

  // Add user message
  messages.value.push({
    type: 'user',
    content: content,
    timestamp: new Date()
  })

  scrollToBottom()

  // Show typing indicator
  isTyping.value = true

  // Simulate AI response (replace with actual API call)
  setTimeout(() => {
    const aiResponse = generateMockResponse(content)
    messages.value.push({
      type: 'ai',
      content: aiResponse.content,
      timestamp: new Date(),
      bookRecommendations: aiResponse.bookRecommendations
    })

    isTyping.value = false
    scrollToBottom()
  }, 1500)
}

const handleSuggestedQuestion = (question) => {
  handleSendMessage(question)
}

const generateMockResponse = (userMessage) => {
  // Mock AI responses based on keywords
  const responses = {
    default: {
      content: '좋은 질문이네요! 당신의 취향을 더 잘 파악하기 위해 몇 가지 질문을 드려도 될까요? 어떤 장르를 선호하시나요?',
      bookRecommendations: null
    },
    베스트셀러: {
      content: '최근 베스트셀러 중에서 추천드립니다. 이 책들은 많은 독자들에게 사랑받고 있어요!',
      bookRecommendations: [
        {
          title: '트렌드 코리아 2025',
          author: '김난도 외',
          cover: 'https://image.aladin.co.kr/product/34561/23/cover500/k012939638_1.jpg',
          rating: '4.5',
          metadata: '경제/경영',
          publisher: '미래의창',
          reviews: '독자(125)',
          genre: '경제'
        },
        {
          title: '역행자',
          author: '자청',
          cover: 'https://image.aladin.co.kr/product/29066/58/cover500/k332838812_1.jpg',
          rating: '4.3',
          metadata: '자기계발',
          publisher: '웅진지식하우스',
          reviews: '독자(89)',
          genre: '자기계발'
        },
        {
          title: '불편한 편의점',
          author: '김호연',
          cover: 'https://image.aladin.co.kr/product/26909/5/cover500/k702636467_1.jpg',
          rating: '4.7',
          metadata: '소설',
          publisher: '나무옆의자',
          reviews: '독자(203)',
          genre: '문학'
        }
      ]
    },
    자기계발: {
      content: '자기계발서를 찾으시는군요! 이 책들은 실용적인 조언과 함께 삶의 방향을 제시해줍니다.',
      bookRecommendations: [
        {
          title: '아주 작은 습관의 힘',
          author: '제임스 클리어',
          cover: 'https://image.aladin.co.kr/product/18847/5/cover500/k392635564_1.jpg',
          rating: '4.6',
          metadata: '자기계발',
          publisher: '비즈니스북스',
          reviews: '독자(312)',
          genre: '자기계발'
        },
        {
          title: '데일 카네기 인간관계론',
          author: '데일 카네기',
          cover: 'https://image.aladin.co.kr/product/1/53/cover500/8935610348_1.jpg',
          rating: '4.5',
          metadata: '자기계발',
          publisher: '현대지성',
          reviews: '독자(178)',
          genre: '자기계발'
        }
      ]
    },
    소설: {
      content: '감동적인 소설을 추천드립니다. 이 작품들은 인간의 감정을 섬세하게 그려낸 수작입니다.',
      bookRecommendations: [
        {
          title: '달러구트 꿈 백화점',
          author: '이미예',
          cover: 'https://image.aladin.co.kr/product/24389/4/cover500/k702636467_1.jpg',
          rating: '4.8',
          metadata: '소설',
          publisher: '팩토리나인',
          reviews: '독자(456)',
          genre: '문학'
        },
        {
          title: '미드나잇 라이브러리',
          author: '매트 헤이그',
          cover: 'https://image.aladin.co.kr/product/26909/5/cover500/k702636467_1.jpg',
          rating: '4.6',
          metadata: '소설',
          publisher: '인플루엔셜',
          reviews: '독자(234)',
          genre: '문학'
        }
      ]
    },
    기분: {
      content: '차가운 겨울, 따뜻한 경제 지혜로 하루를 돌아볼 수 있는 책들을 추천드려요.',
      bookRecommendations: [
        {
          title: '나대의 사랑법',
          author: '이진우',
          cover: 'https://image.aladin.co.kr/product/34561/23/cover500/k012939638_1.jpg',
          rating: '4.0',
          metadata: '에세이',
          publisher: '우미노스',
          reviews: '독자(45)',
          genre: '문학'
        },
        {
          title: '나는 왜 나를 힘들게 하는가',
          author: '프리드리히 니체',
          cover: 'https://image.aladin.co.kr/product/29066/58/cover500/k332838812_1.jpg',
          rating: '4.2',
          metadata: '철학',
          publisher: '스리체어스',
          reviews: '독자(67)',
          genre: '인문'
        },
        {
          title: '혼자일 수 없다면 누구와도 수 없다',
          author: '프리드리히 니체',
          cover: 'https://image.aladin.co.kr/product/26909/5/cover500/k702636467_1.jpg',
          rating: '5.0',
          metadata: '철학',
          publisher: '문학동네',
          reviews: '독자(92)',
          genre: '인문'
        },
        {
          title: '나혜, 내 문장을 사랑하는 법',
          author: '수경담',
          cover: 'https://image.aladin.co.kr/product/18847/5/cover500/k392635564_1.jpg',
          rating: '5.0',
          metadata: '에세이',
          publisher: '위즈덤하우스',
          reviews: '독자(123)',
          genre: '문학'
        },
        {
          title: '위버멘쉬',
          author: '프리드리히 니체',
          cover: 'https://image.aladin.co.kr/product/1/53/cover500/8935610348_1.jpg',
          rating: '3.0',
          metadata: '철학',
          publisher: '민음사',
          reviews: '독자(34)',
          genre: '인문'
        }
      ]
    }
  }

  // Find matching response
  for (const [keyword, response] of Object.entries(responses)) {
    if (userMessage.includes(keyword)) {
      return response
    }
  }

  return responses.default
}

// Lifecycle
onMounted(() => {
  // Optional: Add initial AI greeting
  // messages.value.push({
  //   type: 'ai',
  //   content: '안녕하세요! 어떤 책을 찾고 계신가요?',
  //   timestamp: new Date()
  // })
})
</script>

<style scoped>
.ai-recommend-view {
  min-height: 100vh;
}

/* Hide scrollbar but keep functionality */
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

/* Fade-in animation */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
</style>