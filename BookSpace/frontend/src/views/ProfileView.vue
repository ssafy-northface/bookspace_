<template>
  <div class="space-y-8">
    <ViewHeader
      title="마이페이지"
      description="나의 활동과 찜한 도서를 한눈에 확인하세요."
      size="lg"
    />

    <ProfileSection v-if="me" :userId="userId" :nickname="nickname" />

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
      <ReviewDetailModal v-if="selectedReview" :review="selectedReview" />
    </BaseModal>

    <!-- 도서 상세 모달 -->
    <BaseModal
      :visible="!!selectedBookIsbn"
      maxWidth="max-w-5xl"
      @close="closeBookModal"
    >
      <BookDetailView v-if="selectedBookIsbn" :isbn="selectedBookIsbn" />
    </BaseModal>
    <ScrollTopButton />
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { storeToRefs } from "pinia";
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

const userStore = useUserStore();
const { me } = storeToRefs(userStore);

const userId = computed(() => me.value?.userId);
const nickname = computed(() => me.value?.userNickname);

const activeTab = ref("wish");

// vue-query
const { data: wishItems, isLoading: loadingWishes } = useMyWishes(userId);
const { data: reviewItems, isLoading: loadingReviews } = useMyReviews();
const { data: postItems, isLoading: loadingPosts } = useMyPosts();

const safeWishItems = computed(() => wishItems.value ?? []);
const safeReviewItems = computed(() => reviewItems.value ?? []);
const safePostItems = computed(() => postItems.value ?? []);

const selectedPostId = ref(null);
const selectedReview = ref(null);
const selectedBookIsbn = ref(null);

// 리뷰 모달 open & close
const openReviewModal = (review) => {
  selectedReview.value = review ?? null;
};

const closeReviewModal = () => {
  selectedReview.value = null;
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
</script>
