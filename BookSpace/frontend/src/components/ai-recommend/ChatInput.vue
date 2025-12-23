<template>
  <div class="chat-input-container">
    <div class="flex items-end gap-2 p-4 bg-background border-t border-border">
      <!-- Input Field -->
      <div class="flex-1 relative">
        <textarea
          ref="inputRef"
          v-model="inputText"
          @keydown="handleKeydown"
          @input="adjustHeight"
          @focus="handleFocus"
          @blur="handleBlur"
          :placeholder="placeholder"
          :disabled="disabled"
          rows="1"
          class="w-full resize-none rounded-3xl border border-input bg-muted px-5 py-3 pr-12 text-sm text-foreground outline-none transition focus:border-yellow-400 focus:ring-2 focus:ring-yellow-400/20 disabled:opacity-50 disabled:cursor-not-allowed placeholder:text-muted-foreground max-h-32 overflow-y-auto"
        ></textarea>
        
        <!-- Send Button (inside input) -->
        <button
          @click="handleSend"
          :disabled="!canSend"
          class="absolute right-2 bottom-2 w-9 h-9 rounded-full bg-yellow-400 hover:bg-yellow-500 disabled:bg-gray-300 disabled:cursor-not-allowed flex items-center justify-center transition shadow-sm"
        >
          <svg 
            class="w-5 h-5 text-gray-800" 
            :class="{ 'text-gray-500': !canSend }"
            fill="none" 
            stroke="currentColor" 
            stroke-width="2" 
            viewBox="0 0 24 24"
          >
            <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'

const props = defineProps({
  placeholder: {
    type: String,
    default: '메시지를 입력하세요...'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send', 'input-active', 'input-inactive'])

const inputRef = ref(null)
const inputText = ref('')

const canSend = computed(() => {
  return inputText.value.trim().length > 0 && !props.disabled
})

const handleSend = () => {
  if (!canSend.value) return
  
  const message = inputText.value.trim()
  emit('send', message)
  inputText.value = ''
  
  // Reset textarea height
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.style.height = 'auto'
    }
  })
}

const handleKeydown = (event) => {
  // Enter without Shift = send message
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleSend()
  }
  // Shift + Enter = new line (default behavior)
}

const adjustHeight = () => {
  const textarea = inputRef.value
  if (!textarea) return
  
  textarea.style.height = 'auto'
  textarea.style.height = `${Math.min(textarea.scrollHeight, 128)}px`
}

const handleFocus = () => {
  emit('input-active')
}

const handleBlur = () => {
  emit('input-inactive')
}

// Expose method to set input text (for suggested questions)
const setInputText = (text) => {
  inputText.value = text
  nextTick(() => adjustHeight())
}

defineExpose({
  setInputText
})
</script>

<style scoped>
.chat-input-container {
  backdrop-filter: blur(10px);
}
</style>
