<template>
  <div class="ai-recommend-view">
    <!-- Header -->
    <header class="bg-background border-b border-border">
      <div
        class="flex items-center justify-between px-4 h-full max-w-2xl mx-auto"
      >
        <BackButton />
        <h1 class="text-lg font-semibold text-foreground">AI 책봇</h1>
        <div class="w-9"></div>
      </div>
    </header>

    <!-- Chat Container -->
    <main class="chat-container" :style="{ bottom: `${footerHeight}px` }">
      <div class="max-w-2xl mx-auto px-4 py-6 space-y-4">
        <!-- 인사말 메세지 -->
        <div
          v-if="messages.length === 0"
          :class="[
            'transition-all duration-500 ease-in-out py-12',
            isInputActive ? 'flex items-start gap-3' : 'text-center',
          ]"
        >
          <!-- 책봇 프로필 -->
          <div
            :class="[
              'transition-all duration-500 ease-in-out',
              isInputActive ? 'flex-shrink-0' : 'inline-block',
            ]"
          >
            <ChaekBotProfile :size="isInputActive ? 'md' : 'xl'" />
          </div>

          <!-- 인사말 메세지 -->
          <div
            :class="[
              'transition-all duration-500 ease-in-out',
              isInputActive
                ? 'flex-1 opacity-100 translate-x-0'
                : 'opacity-100 translate-x-0',
            ]"
          >
            <div
              v-if="isInputActive"
              class="bg-muted rounded-2xl rounded-tl-sm px-5 py-4 shadow-sm animate-fade-in"
            >
              <p class="text-sm text-foreground leading-relaxed">
                안녕하세요! 👋<br />
                책봇이 당신의 현재 감정에 맞는 책을 추천해드립니다.<br />
                어떤 책을 찾고 계신가요?
              </p>
            </div>

            <!-- 채팅 시작 메세지 -->
            <div v-else class="animate-fade-in">
              <h2 class="text-xl font-bold foreground mb-2">안녕하세요! 👋</h2>
              <p class="text-sm foreground mb-6">
                책봇이 당신의 현재 감정에 맞는 책을 추천해드립니다.<br />
                어떤 책을 찾고 계신가요?
              </p>
            </div>
          </div>
        </div>

        <!-- 채팅 메세지 -->
        <ChatMessage
          v-for="(message, index) in messages"
          :key="index"
          :message="message"
        />

        <!-- 타이핑 indicator -->
        <TypingIndicator v-if="isTyping" />

        <!-- 스크롤 앵커 -->
        <div ref="scrollAnchor"></div>
      </div>
    </main>

    <!-- 하단 입력 영역 -->
    <footer ref="footerRef" class="bg-background border-t border-border">
      <div class="max-w-2xl mx-auto">
        <!-- 추천 질문 -->
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
import { ref, nextTick, onMounted, onUpdated, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import ChatMessage from "@/components/ai-recommend/ChatMessage.vue";
import ChatInput from "@/components/ai-recommend/ChatInput.vue";
import SuggestedQuestions from "@/components/ai-recommend/SuggestedQuestions.vue";
import TypingIndicator from "@/components/ai-recommend/TypingIndicator.vue";
import BackButton from "@/components/common/BackButton.vue";
import ChaekBotProfile from "@/components/ai-recommend/ChaekBotProfile.vue";
import { getAiRecommendation } from "@/api/aiRecommendationApi";
import { useToast } from "@/composables/useToast";

const router = useRouter();
const route = useRoute();
const { toast } = useToast();
const chatInputRef = ref(null);

// State
const messages = ref([]);
const isTyping = ref(false);
const showSuggestions = ref(true);
const scrollAnchor = ref(null);
const isInputActive = ref(false);
const footerRef = ref(null);
const footerHeight = ref(120); // 기본 높이 (px)

const suggestedQuestions = [
  "지금 우울한 기분이야. 우울한 기분을 좀 다루는 책 추천해줘",
  "힘들 때 읽을 만한 힘이 되는 책 추천해줘",
  "감동적인 소설 찾아줘",
];

// Methods

const scrollToBottom = () => {
  nextTick(() => {
    scrollAnchor.value?.scrollIntoView({ behavior: "smooth" });
  });
};

/**
 * Backend 응답을 ChatMessage 형식으로 변환
 */
const transformApiResponse = (apiResponse) => {
  let content = "";

  // 감정 분석 메시지와 위로 메시지 조합
  // if (apiResponse.userEmotion) {
  //   content += `${apiResponse.userEmotion}\n\n`;
  // }

  if (apiResponse.comfortMessage) {
    content += apiResponse.comfortMessage;
  }

  // 책 추천이 없으면 에러 메시지만 표시
  if (
    !apiResponse.valid ||
    !apiResponse.recommendations ||
    apiResponse.recommendations.length === 0
  ) {
    if (apiResponse.comfortMessage) {
      content = apiResponse.comfortMessage;
    } else {
      content =
        "도서 추천을 받으려면 지금 감정이나 상황을 한 문장으로 적어주세요 🙂";
    }
    return {
      content,
      bookRecommendations: null,
    };
  }

  // 책 추천 목록을 ChatMessage 형식으로 변환
  const bookRecommendations = apiResponse.recommendations.map((book) => ({
    bookId: book.bookId,
    isbn13: book.isbn13,
    title: book.title,
    author: book.author || "저자 정보 없음",
    cover: book.cover,
    rating: book.averageRating ? book.averageRating.toFixed(1) : "0.0",
    publisher: book.publisher || "",
    metadata: book.reason || "", // reason을 metadata로 사용
    reviews: "독자", // Backend에서 제공하지 않으므로 기본값
    genre: "", // Backend에서 제공하지 않으므로 빈 값
  }));

  return {
    content,
    bookRecommendations,
  };
};

const handleSendMessage = async (content) => {
  if (!content.trim()) return;

  // Hide suggestions after first message
  showSuggestions.value = false;

  // Add user message
  messages.value.push({
    type: "user",
    content: content,
    timestamp: new Date(),
  });

  scrollToBottom();

  // Show typing indicator
  isTyping.value = true;

  try {
    // API 호출
    const apiResponse = await getAiRecommendation(content);

    // 응답을 ChatMessage 형식으로 변환
    const transformedResponse = transformApiResponse(apiResponse);

    // AI 메시지 추가
    messages.value.push({
      type: "ai",
      content: transformedResponse.content,
      timestamp: new Date(),
      bookRecommendations: transformedResponse.bookRecommendations,
    });

    scrollToBottom();
  } catch (error) {
    console.error("AI 추천 요청 실패:", error);

    // 에러 메시지 표시
    const errorMessage =
      error.response?.data?.message ||
      error.message ||
      "도서 추천을 받는 중 오류가 발생했습니다. 다시 시도해주세요.";

    messages.value.push({
      type: "ai",
      content: errorMessage,
      timestamp: new Date(),
      bookRecommendations: null,
    });

    toast({
      title: "오류",
      description: errorMessage,
      variant: "destructive",
    });
    scrollToBottom();
  } finally {
    isTyping.value = false;
  }
};

const handleSuggestedQuestion = (question) => {
  handleSendMessage(question);
};

// Footer 높이 계산 함수
const updateFooterHeight = () => {
  if (footerRef.value) {
    footerHeight.value = footerRef.value.offsetHeight;
  }
};

// showSuggestions 변경 시 footer 높이 재계산
watch(showSuggestions, () => {
  nextTick(() => {
    updateFooterHeight();
  });
});

// Lifecycle
onMounted(() => {
  // Footer 높이 초기 계산
  nextTick(() => {
    updateFooterHeight();
  });

  // URL에서 prompt query 파라미터가 있으면 자동으로 메시지 전송
  const promptFromQuery = route.query.prompt;
  if (promptFromQuery && typeof promptFromQuery === 'string' && promptFromQuery.trim()) {
    // 컴포넌트가 완전히 준비된 후 메시지 전송 (100ms 딜레이)
    setTimeout(() => {
      isInputActive.value = true;
      handleSendMessage(promptFromQuery.trim());
    }, 100);
  }
});

onUpdated(() => {
  // 컴포넌트 업데이트 시 footer 높이 재계산
  updateFooterHeight();
});
</script>

<style scoped>
.ai-recommend-view {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* AppHeader는 64px (h-16), 로컬 헤더는 56px */
header {
  position: fixed;
  top: 64px; /* AppHeader 높이 */
  left: 0;
  right: 0;
  height: 56px;
  z-index: 10;
}

/* 채팅 컨테이너: 헤더 아래부터 인풋 위까지 스크롤 가능 영역 */
.chat-container {
  position: fixed;
  top: 120px; /* AppHeader(64px) + 로컬 헤더(56px) */
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

/* Footer는 fixed로 배치되며 높이는 동적으로 계산됨 */
footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 10;
}

/* Hide scrollbar but keep functionality */
.chat-container {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.chat-container::-webkit-scrollbar {
  display: none;
}

/* Fade-in */
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
