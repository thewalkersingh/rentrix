import { useRef, useState } from "react"
import { ImagePlus, Loader2, Star, Trash2, Upload } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { toast } from "sonner"

export interface GridUploadItem {
  id: number
  url?: string // optional — for docs, may not have a thumbnail URL
  label?: string // for docs, show filename
  isPrimary?: boolean
  contentType?: string
}

interface Props {
  items: GridUploadItem[]
  maxItems: number
  columns?: 3 | 4 | 5
  accept: string
  maxSizeMB: number
  uploading?: boolean
  disabled?: boolean
  emptyLabel?: string
  emptyHint?: string
  onUpload: (file: File) => void
  onDelete?: (id: number) => void
  onSetPrimary?: (id: number) => void
}

const colsClass = {
  3: "grid-cols-3",
  4: "grid-cols-2 sm:grid-cols-3 md:grid-cols-4",
  5: "grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5",
}

export function ImageGridUploader({
  items,
  maxItems,
  columns = 5,
  accept,
  maxSizeMB,
  uploading,
  disabled,
  emptyLabel = "Add image",
  emptyHint,
  onUpload,
  onDelete,
  onSetPrimary,
}: Props) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [dragging, setDragging] = useState(false)

  const canUploadMore = items.length < maxItems
  const busy = uploading || disabled

  const handleFiles = (files: FileList | null) => {
    if (!files || files.length === 0 || busy) return
    const remaining = maxItems - items.length
    const toUpload = Array.from(files).slice(0, remaining)
    toUpload.forEach((file) => {
      if (file.size > maxSizeMB * 1024 * 1024) {
        toast.error(`${file.name}: too large (max ${maxSizeMB} MB)`)
        return
      }
      onUpload(file)
    })
    if (inputRef.current) inputRef.current.value = ""
  }

  return (
    <>
      <input
        ref={inputRef}
        type="file"
        accept={accept}
        multiple
        className="hidden"
        onChange={(e) => handleFiles(e.target.files)}
      />

      <div className={cn("grid gap-2", colsClass[columns])}>
        {/* Filled slots */}
        {items.map((item) => (
          <SlotCard
            key={item.id}
            item={item}
            disabled={busy}
            onDelete={onDelete ? () => onDelete(item.id) : undefined}
            onSetPrimary={onSetPrimary ? () => onSetPrimary(item.id) : undefined}
          />
        ))}

        {/* Empty upload slot */}
        {canUploadMore && (
          <button
            type="button"
            onClick={() => !busy && inputRef.current?.click()}
            onDragOver={(e) => {
              e.preventDefault()
              setDragging(true)
            }}
            onDragLeave={() => setDragging(false)}
            onDrop={(e) => {
              e.preventDefault()
              setDragging(false)
              handleFiles(e.dataTransfer.files)
            }}
            disabled={busy}
            className={cn(
              "flex aspect-square flex-col items-center justify-center gap-1 rounded-lg border-2 border-dashed p-2 text-center transition-colors",
              dragging
                ? "border-primary bg-primary/5"
                : "border-muted-foreground/30 hover:border-primary/50 hover:bg-accent/50",
              busy && "pointer-events-none opacity-50",
            )}
          >
            {uploading ? (
              <>
                <Loader2 className="h-6 w-6 animate-spin text-primary" />
                <span className="text-xs text-muted-foreground">Uploading…</span>
              </>
            ) : (
              <>
                <Upload className="h-6 w-6 text-muted-foreground" />
                <span className="text-xs font-medium">{emptyLabel}</span>
                {emptyHint && (
                  <span className="text-[10px] leading-tight text-muted-foreground">
                    {emptyHint}
                  </span>
                )}
                <span className="text-[10px] text-muted-foreground">
                  {maxItems - items.length} left
                </span>
              </>
            )}
          </button>
        )}
      </div>
    </>
  )
}

// ── Slot card ────────────────────────────────────────────────────────

function SlotCard({
  item,
  disabled,
  onDelete,
  onSetPrimary,
}: {
  item: GridUploadItem
  disabled?: boolean
  onDelete?: () => void
  onSetPrimary?: () => void
}) {
  const isImage = !item.contentType || item.contentType.startsWith("image/")

  return (
    <div className="group relative aspect-square overflow-hidden rounded-lg border bg-muted">
      {isImage && item.url ? (
        <img src={item.url} alt="" className="h-full w-full object-cover" loading="lazy" />
      ) : (
        <div className="flex h-full flex-col items-center justify-center gap-1 p-2 text-center">
          <ImagePlus className="h-6 w-6 text-muted-foreground" />
          {item.label && (
            <span className="line-clamp-2 text-[10px] text-muted-foreground">{item.label}</span>
          )}
        </div>
      )}

      {item.isPrimary && (
        <div className="absolute top-1.5 left-1.5 rounded bg-primary px-1.5 py-0.5 text-[10px] font-medium text-primary-foreground shadow">
          <Star className="mr-0.5 inline h-2.5 w-2.5 fill-current" />
          Primary
        </div>
      )}

      <div className="absolute inset-0 flex items-end justify-center gap-1.5 bg-gradient-to-t from-black/70 to-transparent p-1.5 opacity-0 transition-opacity group-hover:opacity-100">
        {!item.isPrimary && onSetPrimary && (
          <Button
            type="button"
            size="sm"
            variant="secondary"
            className="h-7 px-1.5 text-[10px]"
            onClick={onSetPrimary}
            disabled={disabled}
          >
            <Star className="mr-0.5 h-3 w-3" />
            Primary
          </Button>
        )}
        {onDelete && (
          <Button
            type="button"
            size="sm"
            variant="destructive"
            className="h-7 px-1.5 text-[10px]"
            onClick={onDelete}
            disabled={disabled}
          >
            <Trash2 className="mr-0.5 h-3 w-3" />
            Delete
          </Button>
        )}
      </div>
    </div>
  )
}