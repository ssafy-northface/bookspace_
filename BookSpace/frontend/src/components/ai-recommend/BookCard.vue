<template>
  <div class="book-card" @click="$emit('select', book)">
    <div class="relative">
      <!-- Book Cover -->
      <img 
        :src="book.cover" 
        :alt="book.title"
        class="w-full h-40 object-cover rounded-lg shadow-md"
      />
      
      <!-- Category Badge (if exists) -->
      <div v-if="book.category" class="absolute top-2 left-2">
        <span class="px-2 py-1 bg-purple-500 text-white text-xs font-medium rounded-full">
          {{ book.category }}
        </span>
      </div>
    </div>
    
    <!-- Book Info -->
    <div class="mt-3">
      <h3 class="text-sm font-bold text-gray-900 line-clamp-2 mb-1">
        {{ book.title }}
      </h3>
      <p class="text-xs text-gray-600 mb-2">{{ book.author }}</p>
      
      <!-- Rating and Metadata -->
      <div class="flex items-center gap-2 text-xs text-gray-500">
        <div class="flex items-center gap-1">
          <svg class="w-3 h-3 text-yellow-400 fill-current" viewBox="0 0 20 20">
            <path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/>
          </svg>
          <span>{{ book.rating || '4.0' }}</span>
        </div>
        <span>•</span>
        <span>{{ book.metadata || '소설' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  book: {
    type: Object,
    required: true
  }
})

defineEmits(['select'])
</script>

<style scoped>
.book-card {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.book-card:hover {
  transform: translateY(-2px);
}
</style>
