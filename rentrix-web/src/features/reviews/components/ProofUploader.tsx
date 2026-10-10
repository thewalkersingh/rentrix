import { useDeleteProof, useReviewProofs, useUploadProof } from "../hooks/useProof"
import {
  ImageGridUploader,
  type GridUploadItem,
} from "@/features/flats/components/ImageGridUploader"

interface Props {
  reviewId: number
  disabled?: boolean
}

const MAX_PROOFS = 3
const MAX_SIZE_MB = 20

export function ProofUploader({ reviewId, disabled }: Props) {
  const { data: proofs = [], isLoading } = useReviewProofs(reviewId)
  const upload = useUploadProof(reviewId)
  const remove = useDeleteProof(reviewId)

  const items: GridUploadItem[] = proofs.map((p) => ({
    id: p.id,
    label: p.originalFilename ?? "Document",
    contentType: p.contentType,
  }))

  if (isLoading) {
    return (
      <div className="grid grid-cols-3 gap-2">
        {Array.from({ length: 3 }).map((_, i) => (
          <div key={i} className="aspect-square animate-pulse rounded-lg bg-muted" />
        ))}
      </div>
    )
  }

  return (
    <div className="space-y-2">
      <ImageGridUploader
        items={items}
        maxItems={MAX_PROOFS}
        columns={3}
        accept=".pdf,.jpg,.jpeg,.png,.webp"
        maxSizeMB={MAX_SIZE_MB}
        uploading={upload.isPending}
        disabled={disabled || remove.isPending}
        emptyLabel="Add proof"
        emptyHint="PDF, JPG, PNG"
        onUpload={(file) => upload.mutate(file)}
        onDelete={(id) => remove.mutate(id)}
        // no setPrimary for proofs
      />
      <p className="text-xs text-muted-foreground">
        Upload rent agreement, utility bill, or bank statement. Reviews with proof get a "Verified
        stay" badge once approved.
      </p>
    </div>
  )
}