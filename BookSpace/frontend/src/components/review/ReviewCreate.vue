<!-- 이미 작성된 리뷰 목록을 보이도록 하기 위해 리뷰를 작성하지 않는 경우 간단한 화면이 보임
 리뷰를 작성하기 위해 해당 card를 클릭하면 리뷰 등록 화면이 펼쳐지는 구조 -->
<template>
  <div class="rounded-xl border border-input bg-card p-6 shadow-sm">
    <!-- 평점-->
    <div>
      <p class="text-base font-normal mb-2">평점</p>

      <!-- 접힌 상태에서도 별점 선택 가능 -->
      <div>
        <StarRating v-model="rating" :step="0.5" :showValue="true" />
      </div>
    </div>

    <!-- 내용 -->
    <div class="mt-6">
      <p class="text-base font-normal">내용(선택사항)</p>

      <!-- 접힌 상태 textarea -->
      <div v-if="!isExpanded" class="mt-3 flex items-start gap-3">
        <textarea
          v-model="content"
          :maxlength="maxLength"
          rows="1"
          class="flex-1 h-10 overflow-hidden rounded-md border border-input bg-background px-4 py-2 text-sm outline-none placeholder:text-muted-foreground resize-none transition-all duration-200 ease-out"
          placeholder="이 책에 대한 생각을 자유롭게 적어주세요"
          @click="expand"
        ></textarea>

        <!-- 접힌 상태 버튼
             - 기본 회색
             - 별점 선택 시 primary
             - 클릭 시 바로 등록(별점만 등록) -->
        <button
          type="button"
          class="h-10 shrink-0 rounded-md px-5 text-sm font-semibold text-white disabled:opacity-60"
          :class="canSubmit ? 'bg-primary hover:bg-primary/90' : 'bg-gray-400'"
          :disabled="props.isSubmitting"
          @click="onSubmitCollapsed"
        >
          등록
        </button>
      </div>

      <!-- 펼친 상태 -->
      <div v-else>
        <textarea
          v-model="content"
          :maxlength="maxLength"
          class="mt-3 w-full h-[160px] rounded-md border border-input bg-background p-4 text-sm outline-none focus-visible:ring-[3px] focus-visible:border-primary focus-visible:ring-ring/50 placeholder:text-muted-foreground resize-none"
          placeholder="이 책에 대한 생각을 자유롭게 적어주세요 (최대 300자)"
        ></textarea>

        <div class="mt-2 flex justify-between">
          <span class="text-sm text-muted-foreground">{{ content.length }}/{{ maxLength }}</span>
        </div>

        <button
          type="button"
          class="mt-6 h-10 w-full rounded-md bg-gray-400 text-base font-semibold text-white disabled:opacity-60"
          :class="canSubmit ? 'bg-primary hover:bg-primary/90' : ''"
          :disabled="props.isSubmitting"
          @click="onSubmitExpanded"
        >
          {{ props.isSubmitting ? "등록 중..." : "등록하기" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, ref } from "vue";
import StarRating from "../ui/StarRating.vue";

const props = defineProps({
  maxLength: { type: Number, default: 300 },
  isSubmitting: { type: Boolean, default: false },
});

const emit = defineEmits(["submit"]);

const rating = ref(0);
const content = ref("");
const isExpanded = ref(false);

const canSubmit = computed(() => rating.value > 0);

const expand = async () => {
  if (isExpanded.value) return;
  isExpanded.value = true;
  await nextTick();
};

// 리뷰 작성 API(emit) 호출 로직은 하나로 통일
function submitReview(payloadContent) {
  if (props.isSubmitting) return;

  // 별점 선택 안했을시
  if (rating.value === 0) {
    alert("별점을 선택해주세요");
    return;
  }

  emit("submit", {
    rating: rating.value,
    content: payloadContent, // null 또는 string
  });

  // 공통 초기화
  content.value = "";
  rating.value = 0;
}

// 접힌 상태: 별점만 등록
function onSubmitCollapsed() {
  submitReview(null);
}

// 펼친 상태: 별점 + 내용 등록 후 다시 접힘
function onSubmitExpanded() {
  submitReview(content.value.trim() || null);

  // 펼친 상태에서 등록 후 다시 접힘
  isExpanded.value = false;
}
</script>

<style scoped>
:deep(.p-rating-icon) {
  font-size: 1.875rem; /* 30px */
  color: #e5e7eb; /* gray-200 */
}

:deep(.p-rating-item.p-rating-item-active .p-rating-icon) {
  color: #facc15; /* yellow-400 */
}
</style>
