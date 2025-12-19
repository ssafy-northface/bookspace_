<!-- 이미 작성된 리뷰 목록을 보이도록 하기 위해 리뷰를 작성하지 않는 경우 간단한 화면이 보임
 리뷰를 작성하기 위해 해당 card를 클릭하면 리뷰 등록 화면이 펼쳐지는 구조 -->
<template>
  <div class="rounded-xl border border-input bg-card p-6 shadow-sm">
    <!-- 평점-->
    <div>
      <p class="text-base font-normal mb-2">평점</p>

      <!-- 접힌 상태에서도 별점 선택 가능 -->
      <div class="review-interactive" @click="blockIfNotLoggedIn" @mousedown="blockIfNotLoggedIn">
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
          @click="handleTextareaClick"
          @focus="handleTextareaClick"
        ></textarea>

        <!-- 접힌 상태 버튼
             - 기본 회색
             - 별점 선택 시 primary
             - 클릭 시 바로 등록(별점만 등록) -->
        <button
          type="button"
          class="h-10 shrink-0 rounded-md px-5 text-sm font-semibold text-white disabled:opacity-60"
          :class="canSubmit ? 'bg-primary hover:bg-primary/90' : 'bg-gray-400'"
          :disabled="reviewStore.submitting"
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
          @click="blockIfNotLoggedIn"
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
import { useReviewStore } from "@/stores/reviewStore";
import { useAuthStore } from "@/stores/authStore";
import { useBookStore } from "@/stores/bookStore";
import StarRating from "../ui/StarRating.vue";

const authStore = useAuthStore();
const bookStore = useBookStore();
const isLoggedIn = computed(()=> authStore.isLoggedIn);

function blockIfNotLoggedIn(e) {
  if (isLoggedIn.value) return;

  // 리뷰 작성 인터렉션 영역에서 막기
  const isInInteractive = e?.target?.closest?.(".review-interactive");
  if (!isInInteractive) return;

  // 포커스/클릭/드래그 등 입력 자체를 원천 차단
  e.preventDefault?.();
  e.stopPropagation?.();

  alert("로그인 후 이용 가능한 서비스입니다");
}


const props = defineProps({
  bookId: { type: Number, required: true },
  isbn: { type: String, required: true },
  maxLength: { type: Number, default: 300 },
});

const reviewStore = useReviewStore();

const rating = ref(0);
const content = ref("");
const isExpanded = ref(false);

const canSubmit = computed(() => rating.value > 0);

const expand = async () => {
  if (isExpanded.value) return;
  isExpanded.value = true;
  await nextTick();
};

// textarea 클릭 핸들러
const handleTextareaClick = (e) => {
  if (!isLoggedIn.value) {
    e.preventDefault();
    e.stopPropagation();
    alert("로그인 후 이용 가능한 서비스입니다");
    e.target.blur(); // 포커스 제거
    return;
  }
  expand();
};

// 공통 제출 로직
async function submitReview(payloadContent) {
  if (reviewStore.submitting) return;

  // 별점 선택 안했을시
  if (rating.value === 0) {
    alert("별점을 선택해주세요");
    return;
  }

  try {
    await reviewStore.createReview(
      {
        isbn: props.isbn,
        reviewRating: rating.value, // 0.5 단위
        reviewContent: payloadContent, // null 또는 string
      },
      {
        refreshBookId: props.bookId, // 작성 후 목록 갱신
        sort: "latest",
      }
    );

    // 책 상세 정보 갱신 (평점, 리뷰 개수 업데이트)
    await bookStore.loadBookDetail(props.isbn);

    alert("리뷰가 등록되었습니다.");

    // 공통 초기화
    content.value = "";
    rating.value = 0;
  } catch (e) {
    const status = e?.response?.status;

    if (status === 401) {
      alert("로그인 후 이용 가능한 서비스입니다");
    } else if (status === 403) {
      alert("작성자만 수정/삭제할 수 있습니다");
    } else {
      alert(e?.response?.data?.message ?? "리뷰 등록에 실패했습니다");
    }
  }
}

// 접힌 상태: 별점만 등록
function onSubmitCollapsed() {
  submitReview(null);
}

// 펼친 상태: 별점 + 내용 등록 후 다시 접힘
async function onSubmitExpanded() {
  await submitReview(content.value.trim() || null);

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
