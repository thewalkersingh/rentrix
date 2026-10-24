import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { toast } from "sonner"
import type { AxiosError } from "axios"
import { reviewsApi } from "@/api/reviews"
import { queryKeys } from "@/api/queryKeys"
import { useAuthStore } from "@/features/auth/store"
import {usePresignedUpload} from "@/hooks/usePresignedUpload.ts";

export function useReviewProofs(reviewId: number) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)

  return useQuery({
    queryKey: queryKeys.reviews.proof(reviewId),
    queryFn: () => reviewsApi.listProofs(reviewId),
    enabled: isAuthenticated && reviewId > 0,
    staleTime: 1000 * 30,
  })
}

export function useUploadProof(reviewId: number) {
  const qc = useQueryClient()

  const flow = usePresignedUpload({
    presign: (filename, contentType, sizeBytes) =>
      reviewsApi.presignProofUpload(reviewId, filename, contentType, sizeBytes),
    confirm: (payload) => reviewsApi.confirmProofUpload(reviewId, payload),
    onSuccess: async () => {
      toast.success("Proof uploaded")
      await Promise.all([
        qc.invalidateQueries({ queryKey: queryKeys.reviews.proof(reviewId) }),
        qc.invalidateQueries({ queryKey: queryKeys.reviews.mine }),
        qc.invalidateQueries({ queryKey: queryKeys.reviews.pending }),
      ])
    },
    onError: (err) => {
      toast.error(err.message || "Upload failed")
    },
  })

  return {
    upload: flow.upload,
    isUploading: flow.isUploading,
    progress: flow.progress,
    error: flow.error,
    reset: flow.reset,
  }
}

export function useDeleteProof(reviewId: number) {
  const qc = useQueryClient()

  return useMutation({
    mutationFn: (proofId: number) => reviewsApi.deleteProof(reviewId, proofId),
    onSuccess: async () => {
      toast.success("Proof removed")
      await Promise.all([
        qc.invalidateQueries({ queryKey: queryKeys.reviews.proof(reviewId) }),
        qc.invalidateQueries({ queryKey: queryKeys.reviews.mine }),
        qc.invalidateQueries({ queryKey: queryKeys.reviews.pending }),
      ])
    },
    onError: (err: AxiosError<{ message?: string }>) => {
      toast.error(err.response?.data?.message ?? "Failed to delete proof")
    },
  })
}