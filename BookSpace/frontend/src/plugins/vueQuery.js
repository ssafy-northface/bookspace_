import { QueryClient } from "@tanstack/vue-query";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 1000 * 60 * 1, // 1 min
      cacheTime: 1000 * 60 * 5, // 5mins
    },
  },
});
