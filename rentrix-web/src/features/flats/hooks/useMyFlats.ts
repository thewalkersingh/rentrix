import { useQuery } from "@tanstack/react-query"
import { flatsApi } from "@/api/flats"
import { queryKeys } from "@/api/queryKeys"
import { useAuthStore } from "@/features/auth/store"

export function useMyFlats(page = 0, size = 20) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)

  return useQuery({
    queryKey: queryKeys.flats.mine(page, size),
    queryFn: () => flatsApi.listMine(page, size),
    enabled: isAuthenticated,
    staleTime: 1000 * 30,
  })
}