<template>
  <div class="space-y-8">
    <ViewHeader
      title="마이페이지"
      description="나의 활동과 찜한 도서를 한눈에 확인하세요."
      size="lg"
    />

    <ProfileSection
      v-if="me"
      :userId="userId"
      :nickname="nickname"
      @open-settings="openSettingsModal"
    />

    <section
      class="p-4 border shadow-sm rounded-2xl border-border bg-card md:p-6"
    >
      <MyPageTabs
        v-model="activeTab"
        :wish-count="safeWishItems.length"
        :review-count="safeReviewItems.length"
        :post-count="safePostItems.length"
      >
        <template #wish>
          <MyWishList
            :items="safeWishItems"
            :loading="loadingWishes"
            @open-book="openBookModal"
          />
        </template>

        <template #review>
          <MyReviewList
            :reviews="safeReviewItems"
            :loading="loadingReviews"
            @open-review="openReviewModal"
          />
        </template>

        <template #post>
          <MyPostList
            :posts="safePostItems"
            :loading="loadingPosts"
            @open-post="openPostModal"
          />
        </template>
      </MyPageTabs>
    </section>

    <!-- 게시글 상세 모달 -->
    <BaseModal
      :visible="!!selectedPostId"
      maxWidth="max-w-5xl"
      @close="closePostModal"
    >
      <PostDetailView
        v-if="selectedPostId"
        :post-id="selectedPostId"
        :show-back-button="false"
        @close="closePostModal"
      />
    </BaseModal>

    <!-- 리뷰 상세 모달 -->
    <BaseModal
      :visible="!!selectedReview"
      maxWidth="max-w-3xl"
      @close="closeReviewModal"
    >
      <ReviewDetailModal
        v-if="selectedReview"
        :review="selectedReview"
        @update="handleUpdateReview"
        @delete="handleDeleteReview"
      />
    </BaseModal>

    <!-- 도서 상세 모달 -->
    <BaseModal
      :visible="!!selectedBookIsbn"
      maxWidth="max-w-5xl"
      @close="closeBookModal"
    >
      <BookDetailView
        v-if="selectedBookIsbn"
        :isbn="selectedBookIsbn"
        @wish-updated="handleWishUpdated"
      />
    </BaseModal>

    <!-- 비밀번호 확인 모달 -->
    <PasswordConfirmModal
      :visible="showPasswordModal"
      :password="password"
      :error-message="passwordError"
      :loading="verifyingPassword"
      @update:password="(val) => (password = val)"
      @submit="verifyPassword"
      @close="closePasswordModal"
    />
    <ScrollTopButton />
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { useMyWishes } from "@/composables/profile/useMyWishes";
import { useMyReviews } from "@/composables/profile/useMyReviews";
import { useMyPosts } from "@/composables/profile/useMyPosts";
import ViewHeader from "@/components/common/ViewHeader.vue";
import MyPageTabs from "@/components/Profile/MyPageTabs.vue";
import ProfileSection from "@/components/Profile/ProfileSection.vue";
import MyWishList from "@/components/Profile/MyWishList.vue";
import MyReviewList from "@/components/Profile/MyReviewList.vue";
import MyPostList from "@/components/Profile/MyPostList.vue";
import BaseModal from "@/components/common/BaseModal.vue";
import PostDetailView from "./PostDetailView.vue";
import ReviewDetailModal from "@/components/review/ReviewDetailModal.vue";
import ScrollTopButton from "@/components/common/ScrollTopButton";
import BookDetailView from "./BookDetailView.vue";
import PasswordConfirmModal from "@/components/Profile/PasswordConfirmModal.vue";
import { loginApi } from "@/api/authApi";

const userStore = useUserStore();
const { me } = storeToRefs(userStore);
const router = useRouter();

const userId = computed(() => me.value?.userId);
const nickname = computed(() => me.value?.userNickname);

const activeTab = ref("wish");

// vue-query
const {
  data: wishItems,
  isLoading: loadingWishes,
  refetch: refetchWishes,
} = useMyWishes(userId);
const {
  data: reviewItems,
  isLoading: loadingReviews,
  refetch: refetchReviews,
  updateReview,
  deleteReview,
} = useMyReviews();
const { data: postItems, isLoading: loadingPosts } = useMyPosts();

const safeWishItems = computed(() => wishItems.value ?? []);
const safeReviewItems = computed(() => reviewItems.value ?? []);
const safePostItems = computed(() => postItems.value ?? []);

const selectedPostId = ref(null);
const selectedReview = ref(null);
const selectedBookIsbn = ref(null);
const showPasswordModal = ref(false);
const password = ref("");
const passwordError = ref("");
const verifyingPassword = ref(false);

// 리뷰 모달 open & close
const openReviewModal = (review) => {
  selectedReview.value = review ?? null;
};

const closeReviewModal = () => {
  selectedReview.value = null;
};

// 리뷰 핸들러

const handleUpdateReview = async ({ reviewId, rating, content }) => {
  try {
    const updated = await updateReview({
      reviewId,
      data: { reviewRating: rating, reviewContent: content },
    });
    alert("수정되었습니다.");
    closeReviewModal();
  } catch (e) {
    alert(e.response?.data?.message || "수정 실패");
  }
};

const handleDeleteReview = async (review) => {
  if (!review?.reviewId || !confirm("리뷰를 삭제할까요?")) return;

  try {
    await deleteReview(review.reviewId);
    alert("삭제되었습니다.");
    closeReviewModal();
  } catch (e) {
    alert(e.response?.data?.message || "삭제 실패");
  }
};

// 포스트 모달 open & close
const openPostModal = (post) => {
  selectedPostId.value = post?.postId ?? post?.id ?? null;
};

const closePostModal = () => {
  selectedPostId.value = null;
};

// 도서 상세 모달 open & close
const openBookModal = (book) => {
  const isbn = book?.bookIsbn ?? book?.isbn ?? book?.isbn13;
  selectedBookIsbn.value = isbn ? String(isbn) : null;
};

const closeBookModal = () => {
  selectedBookIsbn.value = null;
};

const handleWishUpdated = async () => {
  await refetchWishes();
};

const openSettingsModal = () => {
  password.value = "";
  passwordError.value = "";
  showPasswordModal.value = true;
};

const closePasswordModal = () => {
  showPasswordModal.value = false;
  password.value = "";
  passwordError.value = "";
};

const verifyPassword = async () => {
  if (verifyingPassword.value) return;
  passwordError.value = "";
  const loginId =
    me.value?.userLoginId ?? me.value?.loginId ?? me.value?.username;
  if (!loginId) {
    passwordError.value = "로그인 정보를 확인할 수 없습니다.";
    return;
  }
  if (!password.value) {
    passwordError.value = "비밀번호를 입력해주세요.";
    return;
  }
  try {
    verifyingPassword.value = true;
    await loginApi({ userLoginId: loginId, userPw: password.value });
    closePasswordModal();
    router.push({ name: "profileEdit" });
  } catch (e) {
    passwordError.value = "비밀번호가 올바르지 않습니다.";
  } finally {
    verifyingPassword.value = false;
  }
};
</script>
