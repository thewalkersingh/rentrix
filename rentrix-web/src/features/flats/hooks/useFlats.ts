import { useQuery } from "@tanstack/react-query"
import { useEffect, useState } from "react"
import { flatsApi } from "@/api/flats"
import { queryKeys } from "@/api/queryKeys"
import { useDebouncedValue } from "@/hooks/useDebouncedValue"
import { useFiltersStore } from "../store"

export function useFlats() {
  const filters = useFiltersStore((s) => s.filters)
  const debouncedFilters = useDebouncedValue(filters, 300)
  const [isSlow, setIsSlow] = useState(false)

  const query = useQuery({
    queryKey: queryKeys.flats.list(debouncedFilters),
    queryFn: () => flatsApi.list(debouncedFilters),
    staleTime: 1000 * 30,
    placeholderData: (prev) => prev,
  })

  useEffect(() => {
    if (!query.isFetching) return

    const t = setTimeout(() => setIsSlow(true), 5000)
    return () => {
      clearTimeout(t)
      setIsSlow(false) // ← cleanup handles the reset
    }
  }, [query.isFetching])

  // Only surface isSlow while actually fetching
  return { ...query, isSlow: isSlow && query.isFetching }
}