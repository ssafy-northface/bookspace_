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

      <!-- 펼친 상태 (create/edit 공용)-->
      <div v-else>
        <textarea
          ref="expandedTextarea"
          v-model="content"
          :maxlength="maxLength"
          class="mt-3 w-full h-[160px] rounded-md border border-input bg-background p-4 text-sm outline-none focus-visible:ring-[3px] focus-visible:border-primary focus-visible:ring-ring/50 placeholder:text-muted-foreground resize-none"
          placeholder="이 책에 대한 생각을 자유롭게 적어주세요 (최대 300자)"
          @click="blockIfNotLoggedIn"
        ></textarea>

        <div class="mt-2 flex justify-between">
          <span class="text-sm text-muted-foreground">{{ content.length }}/{{ maxLength }}</span>
        </div>

        <!-- edit 모드일 때 -->
        <div class="mt-6 flex gap-2">
          <button
            v-if="isEditMode"
            type="button"
            class="h-10 w-1/3 rounded-md border border-input bg-background text-base font-semibold text-foreground disabled:opacity-60"
            :disabled="reviewStore.submitting"
            @click="onCancelEdit"
          >
            취소
          </button>

          <button
            type="button"
            class="h-10 w-full rounded-md bg-gray-400 text-base font-semibold text-white disabled:opacity-60"
            :class="canSubmit ? 'bg-primary hover:bg-primary/90' : ''"
            :disabled="reviewStore.submitting"
            @click="onSubmitExpanded"
          >
            {{ reviewStore.submitting ? (isEditMode ? "수정 중..." : "등록 중...") : (isEditMode ? "수정하기" : "등록하기") }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from "vue";
import { useReviewStore } from "@/stores/reviewStore";
import { useAuthStore } from "@/stores/authStore";
import { useBookStore } from "@/stores/bookStore";
import StarRating from "../ui/StarRating.vue";

const authStore = useAuthStore();
const reviewStore = useReviewStore();
const bookStore = useBookStore();

const isLoggedIn = computed(()=> authStore.isLoggedIn);

// edit/create 공용으로 쓰기 위해 props 확장
/**
 * - mode: 'create' | 'edit'
 * - initialRating / initialContent: edit 시작 시 폼에 채워 넣을 값
 */
const props = defineProps({
  bookId: { type: Number, required: true },
  isbn: { type: String, required: true },
  maxLength: { type: Number, default: 300 },
  mode: { type: String, default: "create" }, // 'create' | 'edit'
  initialRating: { type: Number, default: 0 },
  initialContent: { type: String, default: "" },
  reviewId: { type: Number, default: null },

});

// ReviewSection과 통신 
const emit = defineEmits(["saved", "cancel-edit"]);

const isEditMode = computed(() => props.mode === "edit");

const rating = ref(0);
const content = ref("");
const isExpanded = ref(false);

const canSubmit = computed(() => rating.value > 0);

// 비로그인 상태 : 입력/인터렉션 차단
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

const expandedTextarea = ref(null);

const expand = async () => {
  if (isExpanded.value) return;
  isExpanded.value = true;
  await nextTick();

  // 펼쳐진 textarea에 포커스
  if (expandedTextarea.value) {
    expandedTextarea.value.focus();
  }
};

// textarea 클릭 핸들러
const handleTextareaClick = async (e) => {
  if (!isLoggedIn.value) {
    e.preventDefault();
    e.stopPropagation();
    alert("로그인 후 이용 가능한 서비스입니다");
    e.target.blur();
    return;
  }
  await expand();
};

/**
 * edit 모드 상태:
 * - rating/content를 초기값으로 세팅
 * - 폼은 바로 펼친 상태로 보여주기(수정 UX)
 */
watch(
  () => [props.mode, props.reviewId, props.initialRating, props.initialContent],
  async () => {
    if (isEditMode.value) {
      rating.value = props.initialRating ?? 0;
      content.value = props.initialContent ?? "";
      await expand();
    } else {
      // create 모드로 돌아오면 초기화 (원하면 유지해도 됨)
      rating.value = 0;
      content.value = "";
      isExpanded.value = false;
    }
  },
  { immediate: true }
);

// 공통 제출 로직 (creat/edit 분기)
async function submitReview(payloadContent) {
  if (reviewStore.submitting) return;

  // 별점 선택 안했을시
  if (rating.value === 0) {
    alert("별점을 선택해주세요");
    return;
  }

  try {
    // 리뷰 수정(PUT) - reviewId 필수
    if (isEditMode.value) {
      if (!props.reviewId) {
        alert("수정할 리뷰 정보를 찾을 수 없습니다.");
        return;
      }

      await reviewStore.updateReview(
        props.reviewId,
        {
          reviewRating: rating.value,
          reviewContent: payloadContent,
        },
        {
          refreshBookId: props.bookId,
          sort: "latest",
        }
      );

      // 책 상세 정보 갱신 (평점, 리뷰 개수 업데이트)
      // await bookStore.loadBookDetail(props.isbn);

      alert("리뷰가 수정되었습니다.");

      // 부모에게 “저장 완료” 알림 (부모가 edit 모드 종료 + 목록 재조회)
      emit("saved");
      return;
    }


    // 리뷰 등록 (POST)
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

    // 공통 초기화
    content.value = "";
    rating.value = 0;

    // 부모에게 “저장 완료” 알림
    emit("saved");

    await nextTick();
    
    alert("리뷰가 등록되었습니다.");

  

  } catch (e) {
    const status = e?.response?.status;
    const msg = e?.response?.data?.message;

    if (status === 401) {
      alert("로그인 후 이용 가능한 서비스입니다");
    } else if (status === 403) {
      alert("작성자만 수정/삭제할 수 있습니다");
    } else if (status === 400 && msg === "DUPLICATE_REVIEW") {
      // 중복 리뷰인 경우
      alert("이미 이 도서에 작성한 리뷰가 있습니다. 작성한 리뷰를 확인해주세요.");
      emit("saved"); // 목록 갱신 트리거 (부모에서 fetch)
    } else {
      alert(msg ?? (isEditMode.value ? "리뷰 수정에 실패했습니다" : "리뷰 등록에 실패했습니다"));
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
  if (!isEditMode.value) {
    isExpanded.value = false;
  }
}

// 수정 취소
function onCancelEdit() {
  emit("cancel-edit");
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
