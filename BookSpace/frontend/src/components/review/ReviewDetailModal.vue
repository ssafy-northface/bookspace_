<template>
  <div class="space-y-4">
    <div class="flex gap-4">
      <div class="w-24 h-32 overflow-hidden rounded-md bg-muted shrink-0">
        <img
          :src="review.bookImageUrl"
          :alt="review.bookTitle || '도서 이미지'"
          class="object-cover w-full h-full"
        />
      </div>
      <div class="flex-1 space-y-1">
        <p class="text-sm text-muted-foreground">내 리뷰</p>
        <h2 class="text-lg font-semibold text-foreground">
          {{ review.bookTitle || "제목 없음" }}
        </h2>
        <p class="text-sm text-muted-foreground">
          {{ review.bookAuthor || "작가 미상" }}
        </p>
        <p class="text-xs text-muted-foreground">
          {{ formattedDate }}
        </p>
        <div
          class="inline-flex items-center gap-2 rounded-full bg-amber-50 px-3 py-1 text-amber-700"
        >
          <span class="text-base leading-none">★</span>
          <span class="text-base font-semibold">
            {{ displayRating }}
          </span>
        </div>
      </div>
    </div>

    <div
      v-if="isEditing"
      class="space-y-3 rounded-xl border border-border bg-card p-4 text-sm text-foreground"
    >
      <div class="flex flex-col gap-2">
        <span class="text-sm font-medium text-foreground">평점</span>
        <StarRating v-model="draftRating" :step="0.5" :showValue="true" />
      </div>
      <textarea
        v-model="draftContent"
        rows="5"
        class="w-full rounded-lg border border-input bg-background p-3 text-sm"
        placeholder="리뷰 내용을 입력하세요"
      />
    </div>
    <div
      v-else
      class="rounded-xl border border-border bg-card p-4 text-sm whitespace-pre-line text-foreground min-h-[120px]"
    >
      {{ review.reviewContent || "내용 없음" }}
    </div>

    <div class="flex justify-end gap-2 pt-2">
      <template v-if="isEditing">
        <button
          type="button"
          class="h-9 rounded-md px-4 text-sm font-semibold border border-input bg-background hover:bg-muted"
          @click="cancelEdit"
        >
          취소
        </button>
        <button
          type="button"
          class="h-9 rounded-md px-4 text-sm font-semibold text-white bg-primary hover:bg-primary/90"
          @click="submitEdit"
        >
          저장
        </button>
      </template>
      <template v-else>
        <button
          type="button"
          class="h-9 rounded-md px-4 text-sm font-semibold border border-input bg-background hover:bg-muted"
          @click="$emit('delete', review)"
        >
          삭제
        </button>
        <button
          type="button"
          class="h-9 rounded-md px-4 text-sm font-semibold text-white bg-primary hover:bg-primary/90"
          @click="startEdit"
        >
          수정
        </button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useFormattedDate } from "@/composables/useFormattedDate";
import StarRating from "@/components/ui/StarRating.vue";

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["update", "delete"]);

const formattedDate = useFormattedDate(props.review.reviewDate, {
  dateStyle: "medium",
});

const isEditing = ref(false);
const draftRating = ref(props.review.rating ?? props.review.reviewRating ?? 0);
const draftContent = ref(props.review.reviewContent ?? "");

const displayRating = computed(
  () => props.review.rating ?? props.review.reviewRating ?? 0
);

watch(
  () => props.review,
  (next) => {
    if (!next) return;
    draftRating.value = next.rating ?? next.reviewRating ?? 0;
    draftContent.value = next.reviewContent ?? "";
    isEditing.value = false;
  }
);

const startEdit = () => {
  isEditing.value = true;
};

const cancelEdit = () => {
  isEditing.value = false;
  draftRating.value = props.review.rating ?? props.review.reviewRating ?? 0;
  draftContent.value = props.review.reviewContent ?? "";
};

const submitEdit = () => {
  const rating = Number(draftRating.value);
  emit("update", {
    reviewId: props.review.reviewId,
    rating: Number.isNaN(rating) ? 0 : rating,
    content: draftContent.value ?? "",
  });
};
</script>
