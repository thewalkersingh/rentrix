import type { CreateReviewRequest, Page, Review, ReviewStatus } from "@/types"
import { api } from "./client"

export const reviewsApi = {
  listByFlat: (flatId: number, page = 0, size = 10): Promise<Page<Review>> =>
    api.get(`/flats/${flatId}/reviews`, { params: { page, size } }).then((r) => r.data),

  create: (flatId: number, req: CreateReviewRequest): Promise<Review> =>
    api.post(`/flats/${flatId}/reviews`, req).then((r) => r.data),

  mine: (): Promise<Page<Review>> => api.get("/users/me/reviews").then((r) => r.data),

  pending: (): Promise<Page<Review>> =>
    api.get("/admin/reviews", { params: { status: "PENDING" } }).then((r) => r.data),

  moderate: (id: number, status: Exclude<ReviewStatus, "PENDING">): Promise<Review> =>
    api.patch(`/admin/reviews/${id}`, { status }).then((r) => r.data),

  // ── Proof of living ─────────────────────────────────────────────────
  uploadProof: (reviewId: number, file: File): Promise<void> => {
    const formData = new FormData()
    formData.append("file", file)
    return api
      .post(`/reviews/${reviewId}/proof`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then(() => undefined)
  },

  getProofUrl: (reviewId: number): Promise<{ url: string }> =>
    api.get(`/reviews/${reviewId}/proof-url`).then((r) => r.data),

  deleteProof: (reviewId: number): Promise<void> =>
    api.delete(`/reviews/${reviewId}/proof`).then(() => undefined),
}