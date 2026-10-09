import { useRef, useState } from "react"
import { ImagePlus, Loader2, Star, Trash2, Upload } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import {
  useDeleteFlatImage,
  useFlatImages,
  useSetPrimaryImage,
  useUploadFlatImage,
} from "../hooks/useFlatImages"
import type { FlatImage } from "@/types"

interface Props {
  flatId: number
}

const MAX_IMAGES = 10
const MAX_SIZE_MB = 10

export function FlatImageUploader({ flatId }: Props) {
  const { data: images = [], isLoading } = useFlatImages(flatId)
  const uploadImage = useUploadFlatImage(flatId)
  const deleteImage = useDeleteFlatImage(flatId)
  const setPrimary = useSetPrimaryImage(flatId)
  const inputRef = useRef<HTMLInputElement>(null)
  const [dragging, setDragging] = useState(false)

  const canUploadMore = images.length < MAX_IMAGES
  const busy = uploadImage.isPending || deleteImage.isPending || setPrimary.isPending

  const handleFiles = (files: FileList | null) => {
    if (!files || files.length === 0) return
    const file = files[0]
    if (file.size > MAX_SIZE_MB * 1024 * 1024) return
    uploadImage.mutate(file)
    if (inputRef.current) inputRef.current.value = ""
  }

  const onDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault()
    setDragging(false)
    handleFiles(e.dataTransfer.files)
  }

  return (
    <div className="space-y-4">
      {/* Upload area */}
      {canUploadMore && (
        <div
          onDragOver={(e) => {
            e.preventDefault()
            setDragging(true)
          }}
          onDragLeave={() => setDragging(false)}
          onDrop={onDrop}
          onClick={() => inputRef.current?.click()}
          className={cn(
            "flex cursor-pointer flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed p-8 text-center transition-colors",
            dragging
              ? "border-primary bg-primary/5"
              : "border-muted-foreground/30 hover:border-primary/50 hover:bg-accent/50",
            busy && "pointer-events-none opacity-50",
          )}
        >
          <input
            ref={inputRef}
            type="file"
            accept="image/jpeg,image/png,image/webp"
            className="hidden"
            onChange={(e) => handleFiles(e.target.files)}
          />
          {uploadImage.isPending ? (
            <>
              <Loader2 className="h-8 w-8 animate-spin text-primary" />
              <p className="text-sm text-muted-foreground">Uploading…</p>
            </>
          ) : (
            <>
              <Upload className="h-8 w-8 text-muted-foreground" />
              <div className="space-y-0.5">
                <p className="text-sm font-medium">Drop an image, or click to upload</p>
                <p className="text-xs text-muted-foreground">
                  JPG, PNG, WebP · max {MAX_SIZE_MB} MB · {MAX_IMAGES - images.length} remaining
                </p>
              </div>
            </>
          )}
        </div>
      )}

      {/* Images grid */}
      {isLoading ? (
        <div className="grid grid-cols-3 gap-2 sm:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="aspect-square animate-pulse rounded-md bg-muted" />
          ))}
        </div>
      ) : images.length === 0 ? (
        <div className="flex flex-col items-center gap-2 rounded-lg border py-12 text-center">
          <ImagePlus className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No images yet. Add up to {MAX_IMAGES}.</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4">
          {images.map((img) => (
            <ImageCard
              key={img.id}
              image={img}
              onDelete={() => deleteImage.mutate(img.id)}
              onSetPrimary={() => setPrimary.mutate(img.id)}
              disabled={busy}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function ImageCard({
  image,
  onDelete,
  onSetPrimary,
  disabled,
}: {
  image: FlatImage
  onDelete: () => void
  onSetPrimary: () => void
  disabled: boolean
}) {
  return (
    <div className="group relative aspect-square overflow-hidden rounded-lg border bg-muted">
      <img src={image.url} alt="" className="h-full w-full object-cover" loading="lazy" />

      {image.isPrimary && (
        <div className="absolute top-2 left-2 rounded-md bg-primary px-2 py-0.5 text-xs font-medium text-primary-foreground shadow">
          <Star className="mr-1 inline h-3 w-3 fill-current" />
          Primary
        </div>
      )}

      <div className="absolute inset-0 flex items-end justify-center gap-2 bg-gradient-to-t from-black/70 to-transparent p-2 opacity-0 transition-opacity group-hover:opacity-100">
        {!image.isPrimary && (
          <Button
            type="button"
            size="sm"
            variant="secondary"
            className="h-8 px-2 text-xs"
            onClick={onSetPrimary}
            disabled={disabled}
          >
            <Star className="mr-1 h-3 w-3" />
            Primary
          </Button>
        )}
        <Button
          type="button"
          size="sm"
          variant="destructive"
          className="h-8 px-2 text-xs"
          onClick={onDelete}
          disabled={disabled}
        >
          <Trash2 className="mr-1 h-3 w-3" />
          Delete
        </Button>
      </div>
    </div>
  )
}