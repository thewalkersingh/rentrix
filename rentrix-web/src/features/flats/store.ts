import { create } from "zustand"
import type { FlatFilters } from "@/types"

interface FiltersState {
  filters: FlatFilters
  setFilter: <K extends keyof FlatFilters>(key: K, value: FlatFilters[K]) => void
  setFilters: (patch: Partial<FlatFilters>) => void
  resetFilters: () => void
}

const DEFAULT_FILTERS: FlatFilters = {
  page: 0,
  size: 9,
  sort: "rent,asc",
}

export const useFiltersStore = create<FiltersState>((set) => ({
  filters: { ...DEFAULT_FILTERS },

  setFilter: (key, value) =>
    set((state) => ({
      // Reset to page 0 whenever a non-page filter changes
      filters: {
        ...state.filters,
        [key]: value,
        ...(key !== "page" ? { page: 0 } : {}),
      },
    })),

  setFilters: (patch) => set((state) => ({ filters: { ...state.filters, ...patch,page:0 } })),

  resetFilters: () => set({ filters: { ...DEFAULT_FILTERS } }),
}))