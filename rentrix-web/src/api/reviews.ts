import type {
  CreateReviewRequest,
  Page,
  PresignedUploadResponse,
  Review,
  ReviewProof,
  ReviewStatus,
} from "@/types"
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

  // ── Proofs of living ─────────────────────────────────────────────────
  listProofs: (reviewId: number): Promise<ReviewProof[]> =>
    api.get(`/reviews/${reviewId}/proofs`).then((r) => r.data),

  // Legacy multipart upload
  uploadProof: (reviewId: number, file: File): Promise<void> => {
    const formData = new FormData()
    formData.append("file", file)
    return api
      .post(`/reviews/${reviewId}/proofs`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then(() => undefined)
  },

  // Presigned flow
  presignProofUpload: (
    reviewId: number,
    filename: string,
    contentType: string,
    sizeBytes: number,
  ): Promise<PresignedUploadResponse> =>
    api
      .post(`/reviews/${reviewId}/proofs/presigned-url`, {
        filename,
        contentType,
        sizeBytes,
      })
      .then((r) => r.data),

  confirmProofUpload: (
    reviewId: number,
    payload: { key: string; sizeBytes: number; contentType: string; originalFilename: string },
  ): Promise<ReviewProof> =>
    api.post(`/reviews/${reviewId}/proofs/confirm`, payload).then((r) => r.data),

  getProofUrl: (reviewId: number, proofId: number): Promise<{ url: string }> =>
    api.get(`/reviews/${reviewId}/proofs/${proofId}/url`).then((r) => r.data),

  deleteProof: (reviewId: number, proofId: number): Promise<void> =>
    api.delete(`/reviews/${reviewId}/proofs/${proofId}`).then(() => undefined),
}