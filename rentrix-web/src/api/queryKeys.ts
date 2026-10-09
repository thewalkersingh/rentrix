import type { FlatFilters } from "@/types"

export const queryKeys = {
  auth: {
    me: ["auth", "me"] as const,
  },
  flats: {
    all: ["flats"] as const,
    list: (filters: FlatFilters) => ["flats", "list", filters] as const,
    mine: (page = 0, size = 20) => ["flats", "mine", { page, size }] as const,
    detail: (id: number) => ["flats", "detail", id] as const,
    images: (id: number) => ["flats", "images", id] as const,
  },
  reviews: {
    byFlat: (flatId: number, page = 0, size = 10) =>
      ["reviews", "flat", flatId, { page, size }] as const,
    mine: ["reviews", "mine"] as const,
    pending: ["reviews", "pending"] as const,
    proof: (reviewId: number) => ["reviews", "proof", reviewId] as const,
  },
}