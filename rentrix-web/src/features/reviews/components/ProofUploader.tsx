import { useRef, useState } from "react"
import { CheckCircle2, FileText, Loader2, Upload, X } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { useDeleteProof, useUploadProof } from "../hooks/useProof"
import {toast} from "sonner";

interface Props {
  reviewId: number
  hasProof: boolean
  disabled?: boolean
}

const MAX_SIZE_MB = 20
const ACCEPTED = ".pdf,.jpg,.jpeg,.png,.webp"

export function ProofUploader({ reviewId, hasProof, disabled }: Props) {
  const upload = useUploadProof(reviewId)
  const remove = useDeleteProof(reviewId)
  const inputRef = useRef<HTMLInputElement>(null)
  const [dragging, setDragging] = useState(false)

  const busy = upload.isPending || remove.isPending

  const handleFiles = (files: FileList | null) => {
    if (!files || files.length === 0) return
    const file = files[0]
    if (file.size > MAX_SIZE_MB * 1024 * 1024) {
      toast.error(`File too large (max ${MAX_SIZE_MB} MB)`)
      return
    }
    upload.mutate(file)
    if (inputRef.current) inputRef.current.value = ""
  }

  // Already uploaded
  if (hasProof) {
    return (
      <div className="flex items-center justify-between gap-3 rounded-lg border border-green-500/30 bg-green-50 p-3 dark:bg-green-950/20">
        <div className="flex items-center gap-2 text-sm">
          <CheckCircle2 className="h-4 w-4 text-green-600 dark:text-green-400" />
          <span className="font-medium text-green-800 dark:text-green-300">Proof uploaded</span>
          <span className="text-xs text-muted-foreground">— will be reviewed by a moderator</span>
        </div>
        <Button
          type="button"
          variant="ghost"
          size="sm"
          disabled={busy || disabled}
          onClick={() => remove.mutate()}
          className="text-destructive hover:text-destructive"
        >
          {remove.isPending ? (
            <Loader2 className="h-3 w-3 animate-spin" />
          ) : (
            <>
              <X className="mr-1 h-3 w-3" /> Remove
            </>
          )}
        </Button>
      </div>
    )
  }

  // Not uploaded — show uploader
  return (
    <div
      onDragOver={(e) => {
        e.preventDefault()
        setDragging(true)
      }}
      onDragLeave={() => setDragging(false)}
      onDrop={(e) => {
        e.preventDefault()
        setDragging(false)
        if (disabled) return
        handleFiles(e.dataTransfer.files)
      }}
      onClick={() => !disabled && inputRef.current?.click()}
      className={cn(
        "flex cursor-pointer flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed p-6 text-center transition-colors",
        dragging
          ? "border-primary bg-primary/5"
          : "border-muted-foreground/30 hover:border-primary/50 hover:bg-accent/50",
        (busy || disabled) && "pointer-events-none opacity-50",
      )}
    >
      <input
        ref={inputRef}
        type="file"
        accept={ACCEPTED}
        className="hidden"
        onChange={(e) => handleFiles(e.target.files)}
      />
      {upload.isPending ? (
        <>
          <Loader2 className="h-7 w-7 animate-spin text-primary" />
          <p className="text-sm text-muted-foreground">Uploading…</p>
        </>
      ) : (
        <>
          <Upload className="h-7 w-7 text-muted-foreground" />
          <div className="space-y-0.5">
            <p className="text-sm font-medium">Upload proof you lived here</p>
            <p className="text-xs text-muted-foreground">
              Rent agreement, utility bill, or bank statement
              <br />
              PDF / JPG / PNG · max {MAX_SIZE_MB} MB
            </p>
          </div>
          <p className="mt-1 text-xs text-muted-foreground">
            <FileText className="mr-1 inline h-3 w-3" />
            Reviews with proof get a "Verified stay" badge
          </p>
        </>
      )}
    </div>
  )
}