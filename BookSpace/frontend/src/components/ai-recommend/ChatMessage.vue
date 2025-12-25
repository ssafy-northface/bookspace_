<template>
  <div
    class="chat-message"
    :class="message.type === 'user' ? 'user-message' : 'ai-message'"
  >
    <!-- AI Message Layout (Left-aligned with avatar) -->
    <div
      v-if="message.type === 'ai'"
      class="flex items-start gap-3 max-w-[85%]"
    >
      <!-- Avatar -->
      <div class="flex-shrink-0">
        <ChaekBotProfile size="sm" />
      </div>

      <!-- Message Content -->
      <div class="flex-1">
        <div
          class="relative bg-gradient-to-br from-blue-50 to-purple-50 dark:from-slate-800 dark:to-slate-700 border border-blue-200 dark:border-blue-500/30 rounded-2xl rounded-tl-sm px-5 py-4 shadow-sm"
        >
          <!-- AI 배지 -->
          <span
            class="absolute -top-2 left-3 px-3 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white text-xs font-medium rounded-full"
          >
            책봇
          </span>
          <p
            class="text-sm text-card-foreground leading-relaxed whitespace-pre-wrap mt-1"
          >
            {{ message.content }}
          </p>

          <!-- Book Recommendations List (if exists) -->
          <div
            v-if="
              message.bookRecommendations &&
              message.bookRecommendations.length > 0
            "
            class="mt-4 space-y-3"
          >
            <div
              v-for="(book, index) in message.bookRecommendations"
              :key="index"
              class="flex gap-4 cursor-pointer hover:bg-muted p-3 rounded-lg transition"
              @click="handleBookClick(book)"
            >
              <!-- Book Cover -->
              <img
                :src="book.cover"
                :alt="book.title"
                class="w-24 h-36 object-cover rounded shadow-md flex-shrink-0"
              />

              <!-- Book Info -->
              <div class="flex-1 min-w-0">
                <h3
                  class="text-sm font-bold text-card-foreground line-clamp-2 mb-1"
                >
                  {{ book.title }}
                </h3>
                <p class="text-xs text-muted-foreground mb-2">
                  {{ book.author }}
                </p>

                <!-- Publisher and Genre -->
                <p class="text-xs text-muted-foreground mb-2">
                  {{ book.publisher || "문학동네" }} •
                  {{ book.metadata || "소설" }}
                </p>

                <!-- Rating -->
                <div
                  class="flex items-center gap-1 text-xs text-muted-foreground"
                >
                  <svg
                    class="w-3 h-3 text-yellow-400 fill-current"
                    viewBox="0 0 20 20"
                  >
                    <path
                      d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"
                    />
                  </svg>
                  <span>{{ book.rating || "4.0" }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="text-xs text-muted-foreground mt-1 ml-1">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>
    </div>

    <!-- User Message Layout (Right-aligned) -->
    <div v-else class="flex justify-end">
      <div class="max-w-[85%]">
        <div
          class="bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl rounded-tr-sm px-5 py-4 shadow-sm"
        >
          <p class="text-sm text-white leading-relaxed whitespace-pre-wrap">
            {{ message.content }}
          </p>
        </div>
        <div class="text-xs text-muted-foreground mt-1 mr-1 text-right">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import ChaekBotProfile from "./ChaekBotProfile.vue";

const props = defineProps({
  message: {
    type: Object,
    required: true,
    validator: (value) => {
      return (
        ["user", "ai"].includes(value.type) &&
        typeof value.content === "string" &&
        value.timestamp instanceof Date
      );
    },
  },
});

const emit = defineEmits(["book-click"]);

const handleBookClick = (book) => {
  // 부모 컴포넌트(AiRecommendView)로 이벤트 전달
  emit("book-click", book);
};

const formatTime = (date) => {
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const period = hours >= 12 ? "오후" : "오전";
  const displayHours = hours % 12 || 12;
  return `${period} ${displayHours}:${minutes.toString().padStart(2, "0")}`;
};
</script>

<style scoped>
.chat-message {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 스크롤바 스타일은 이제 global.css에서 전역으로 처리됩니다 */
</style>
