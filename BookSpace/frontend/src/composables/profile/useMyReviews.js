import { computed } from "vue";
import { useQuery, useMutation, useQueryClient } from "@tanstack/vue-query";
import {
  fetchMyReviewsApi,
  updateReviewApi,
  deleteReviewApi,
} from "@/api/reviewApi";
import { useAuthStore } from "@/stores/authStore";

export function useMyReviews() {
  const authStore = useAuthStore();
  const queryClient = useQueryClient();

  // 1. 조회
  const query = useQuery({
    queryKey: ["my-reviews"],
    enabled: computed(() => authStore.isLoggedIn),
    queryFn: async () => {
      const res = await fetchMyReviewsApi();
      const raw = res?.data ?? res;
      if (Array.isArray(raw)) return raw;
      if (Array.isArray(raw?.content)) return raw.content;
      if (Array.isArray(raw?.reviews)) return raw.reviews;
      return [];
    },
    staleTime: 1000 * 30,
  });

  // 2. 삭제 Mutation
  const deleteMutation = useMutation({
    mutationFn: (reviewId) => deleteReviewApi(reviewId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["my-reviews"] });
    },
  });

  // 3. 수정 Mutation
  const updateMutation = useMutation({
    mutationFn: ({ reviewId, data }) => updateReviewApi(reviewId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["my-reviews"],
        refetchType: "active",
      });
    },
  });

  return {
    ...query,
    deleteReview: deleteMutation.mutateAsync,
    updateReview: updateMutation.mutateAsync,
  };
}
