import { useMutation, useQueryClient } from "@tanstack/react-query"
import { toast } from "sonner"
import type { AxiosError } from "axios"
import { reviewsApi } from "@/api/reviews"
import { queryKeys } from "@/api/queryKeys"

export function useUploadProof(reviewId: number) {
  const qc = useQueryClient()

  return useMutation({
    mutationFn: (file: File) => reviewsApi.uploadProof(reviewId, file),
    onSuccess: async () => {
      toast.success("Proof uploaded — pending review")
      await Promise.all([
        qc.invalidateQueries({ queryKey: queryKeys.reviews.mine }),
        qc.invalidateQueries({ queryKey: queryKeys.reviews.pending }),
      ])
    },
    onError: (err: AxiosError<{ message?: string }>) => {
      toast.error(err.response?.data?.message ?? "Upload failed")
    },
  })
}

export function useDeleteProof(reviewId: number) {
  const qc = useQueryClient()

  return useMutation({
    mutationFn: () => reviewsApi.deleteProof(reviewId),
    onSuccess: async () => {
      toast.success("Proof removed")
      await Promise.all([
        qc.invalidateQueries({ queryKey: queryKeys.reviews.mine }),
        qc.invalidateQueries({ queryKey: queryKeys.reviews.pending }),
      ])
    },
    onError: (err: AxiosError<{ message?: string }>) => {
      toast.error(err.response?.data?.message ?? "Failed to delete proof")
    },
  })
}