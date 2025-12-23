<template>
  <div 
    class="chat-message"
    :class="message.type === 'user' ? 'user-message' : 'ai-message'"
  >
    <!-- AI Message Layout (Left-aligned with avatar) -->
    <div v-if="message.type === 'ai'" class="flex items-start gap-3 max-w-[85%]">
      <!-- Avatar -->
      <div class="flex-shrink-0">
        <ChaekBotProfile size="sm" />
      </div>
      
      <!-- Message Content -->
      <div class="flex-1">
        <div class="bg-white border border-gray-200 rounded-2xl rounded-tl-sm px-4 py-3 shadow-sm">
          <p class="text-sm text-gray-800 leading-relaxed whitespace-pre-wrap">{{ message.content }}</p>
          
          <!-- Book Recommendations List (if exists) -->
          <div v-if="message.bookRecommendations && message.bookRecommendations.length > 0" class="mt-4 space-y-3">
            <div 
              v-for="(book, index) in message.bookRecommendations" 
              :key="index"
              class="flex gap-4 cursor-pointer hover:bg-gray-50 p-3 rounded-lg transition"
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
                  {{ book.publisher || '문학동네' }} • {{ book.metadata || '소설' }}
                </p>
                
                <!-- Rating -->
                <div class="flex items-center gap-1 text-xs text-gray-500">
                  <svg class="w-3 h-3 text-yellow-400 fill-current" viewBox="0 0 20 20">
                    <path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/>
                  </svg>
                  <span>{{ book.rating || '4.0' }}</span>
                  <span class="mx-1">•</span>
                  <span>{{ book.reviews || '독자(2)' }}</span>
                  <span class="mx-1">•</span>
                  <span>{{ book.genre || '문학' }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Feedback Buttons -->
          <div v-if="message.bookRecommendations && message.bookRecommendations.length > 0" class="mt-4 pt-3 border-t border-gray-100 flex gap-2">
            <button 
              @click="handleFeedback('like')"
              class="p-2 hover:bg-gray-100 rounded-full transition"
              :class="{ 'bg-gray-100': feedback === 'like' }"
            >
              <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/>
              </svg>
            </button>
            <button 
              @click="handleFeedback('dislike')"
              class="p-2 hover:bg-gray-100 rounded-full transition"
              :class="{ 'bg-gray-100': feedback === 'dislike' }"
            >
              <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                <path d="M10 15v4a3 3 0 0 0 3 3l4-9V2H5.72a2 2 0 0 0-2 1.7l-1.38 9a2 2 0 0 0 2 2.3zm7-13h2.67A2.31 2.31 0 0 1 22 4v7a2.31 2.31 0 0 1-2.33 2H17"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="text-xs text-gray-400 mt-1 ml-1">{{ formatTime(message.timestamp) }}</div>
      </div>
    </div>

    <!-- User Message Layout (Right-aligned) -->
    <div v-else class="flex justify-end">
      <div class="max-w-[85%]">
        <div class="bg-yellow-400 rounded-2xl rounded-tr-sm px-4 py-3 shadow-sm">
          <p class="text-sm text-gray-900 leading-relaxed whitespace-pre-wrap">{{ message.content }}</p>
        </div>
        <div class="text-xs text-gray-400 mt-1 mr-1 text-right">{{ formatTime(message.timestamp) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ChaekBotProfile from './ChaekBotProfile.vue'

const props = defineProps({
  message: {
    type: Object,
    required: true,
    validator: (value) => {
      return ['user', 'ai'].includes(value.type) && 
             typeof value.content === 'string' &&
             value.timestamp instanceof Date
    }
  }
})

const feedback = ref(null)

const handleFeedback = (type) => {
  if (feedback.value === type) {
    feedback.value = null
  } else {
    feedback.value = type
  }
  // TODO: Send feedback to backend
  console.log('Feedback:', type)
}

const formatTime = (date) => {
  const hours = date.getHours()
  const minutes = date.getMinutes()
  const period = hours >= 12 ? '오후' : '오전'
  const displayHours = hours % 12 || 12
  return `${period} ${displayHours}:${minutes.toString().padStart(2, '0')}`
}
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

/* Hide scrollbar but keep functionality */
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
