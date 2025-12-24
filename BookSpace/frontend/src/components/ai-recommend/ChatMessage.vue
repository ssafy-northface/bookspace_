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
          class="bg-white border border-gray-200 rounded-2xl rounded-tl-sm px-5 py-4 shadow-sm"
        >
          <p class="text-sm text-gray-800 leading-relaxed whitespace-pre-wrap">
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
              class="flex gap-4 cursor-pointer hover:bg-gray-50 p-3 rounded-lg transition"
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
                <h3 class="text-sm font-bold text-gray-900 line-clamp-2 mb-1">
                  {{ book.title }}
                </h3>
                <p class="text-xs text-gray-600 mb-2">{{ book.author }}</p>

                <!-- Publisher and Genre -->
                <p class="text-xs text-gray-500 mb-2">
                  {{ book.publisher || "문학동네" }} •
                  {{ book.metadata || "소설" }}
                </p>

                <!-- Rating -->
                <div class="flex items-center gap-1 text-xs text-gray-500">
                  <svg
                    class="w-3 h-3 text-yellow-400 fill-current"
                    viewBox="0 0 20 20"
                  >
                    <path
                      d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"
                    />
                  </svg>
                  <span>{{ book.rating || "4.0" }}</span>
                  <span class="mx-1">•</span>
                  <span>{{ book.reviews || "독자(2)" }}</span>
                  <span class="mx-1">•</span>
                  <span>{{ book.genre || "문학" }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="text-xs text-gray-400 mt-1 ml-1">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>
    </div>

    <!-- User Message Layout (Right-aligned) -->
    <div v-else class="flex justify-end">
      <div class="max-w-[85%]">
        <div
          class="bg-yellow-400 rounded-2xl rounded-tr-sm px-5 py-4 shadow-sm"
        >
          <p class="text-sm text-gray-900 leading-relaxed whitespace-pre-wrap">
            {{ message.content }}
          </p>
        </div>
        <div class="text-xs text-gray-400 mt-1 mr-1 text-right">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import ChaekBotProfile from "./ChaekBotProfile.vue";

const router = useRouter();

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

const handleBookClick = (book) => {
  // isbn13 또는 bookId로 도서 상세 페이지로 이동
  if (book.isbn13) {
    router.push(`/books/${book.isbn13}`);
  } else if (book.bookId) {
    // bookId만 있는 경우는 나중에 처리 가능
    console.log("Book clicked:", book);
  }
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
