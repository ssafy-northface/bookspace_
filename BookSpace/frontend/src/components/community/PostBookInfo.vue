<!-- 게시글 작성 / 수정 뷰에서 책 정보 간략하게 보여주는 ui -->
<template>
  <div class="space-y-3">
    <Separator />
    <div class="flex items-start gap-4 px-1">
      <div class="h-40 overflow-hidden rounded-md w-28 bg-muted">
        <img
          :src="displayImage"
          :alt="title || 'book cover'"
          class="object-cover w-full h-full"
        />
      </div>
      <div class="flex-1 space-y-1">
        <p class="text-lg font-semibold text-foreground">
          {{ title || "도서 정보 없음" }}
        </p>
        <p class="text-sm text-muted-foreground">
          {{ author || "작성자가 도서 정보를 추가하지 않았어요." }}
        </p>
        <p v-if="isbn" class="text-xs text-muted-foreground">ISBN {{ isbn }}</p>
      </div>
    </div>
    <Separator />
  </div>
</template>

<script setup>
import { computed } from "vue";
import Separator from "@/components/ui/Separator.vue";

const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  author: {
    type: String,
    default: "",
  },
  isbn: {
    type: String,
    default: "",
  },
  imageUrl: {
    type: String,
    default: "",
  },
  defaultImage: {
    type: String,
    default: "/default-book.png",
  },
});

const displayImage = computed(() => props.imageUrl || props.defaultImage);
</script>
