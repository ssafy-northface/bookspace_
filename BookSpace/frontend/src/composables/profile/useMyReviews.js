import { computed } from "vue";
import { useQuery } from "@tanstack/vue-query";
import { fetchMyReviewsApi } from "@/api/reviewApi";
import { useAuthStore } from "@/stores/authStore";

export function useMyReviews() {
  const authStore = useAuthStore();

  return useQuery({
    queryKey: computed(() => ["my-reviews"]),
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
}
