import { computed } from "vue";
import { useQuery } from "@tanstack/vue-query";
import { useAuthStore } from "@/stores/authStore";
import wishApi from "@/api/wishApi";

export function useMyWishes() {
  const authStore = useAuthStore();
  return useQuery({
    queryKey: computed(() => ["my-wishes"]),
    enabled: authStore.isLoggedIn,
    queryFn: async () => {
      const res = await wishApi.getMyWishes();
      const raw = res?.data ?? res;
      if (Array.isArray(raw)) return raw;
      if (Array.isArray(raw?.content)) return raw.content;
      if (Array.isArray(raw?.content?.wishes)) return raw.content.wishes;
      if (Array.isArray(raw?.wishes)) return raw.wishes;
      return [];
    },
    staleTime: 1000 * 30,
  });
}
