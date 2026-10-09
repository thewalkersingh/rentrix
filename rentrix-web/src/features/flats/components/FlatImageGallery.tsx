import { useState } from "react"
import { ChevronLeft, ChevronRight, X } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import type { FlatImage } from "@/types"

interface Props {
  images: FlatImage[]
  className?: string
}

export function FlatImageGallery({ images, className }: Props) {
  const [lightboxIndex, setLightboxIndex] = useState<number | null>(null)

  if (!images || images.length === 0) return null

  const open = (i: number) => setLightboxIndex(i)
  const close = () => setLightboxIndex(null)
  const prev = () =>
    setLightboxIndex((i) => (i === null ? null : (i - 1 + images.length) % images.length))
  const next = () => setLightboxIndex((i) => (i === null ? null : (i + 1) % images.length))

  return (
    <>
      <div className={cn("grid gap-2", className)}>
        {/* Hero image */}
        <button
          type="button"
          onClick={() => open(0)}
          className="group relative aspect-[16/10] overflow-hidden rounded-lg border bg-muted"
        >
          <img
            src={images[0].url}
            alt="Flat"
            className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
            loading="eager"
          />
        </button>

        {/* Thumbnail strip */}
        {images.length > 1 && (
          <div className="grid grid-cols-4 gap-2">
            {images.slice(1, 5).map((img, idx) => {
              const i = idx + 1
              const remaining = images.length - 5
              return (
                <button
                  key={img.id}
                  type="button"
                  onClick={() => open(i)}
                  className="group relative aspect-square overflow-hidden rounded-md border bg-muted"
                >
                  <img
                    src={img.url}
                    alt={`Flat ${i + 1}`}
                    className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
                    loading="lazy"
                  />
                  {i === 4 && remaining > 0 && (
                    <div className="absolute inset-0 flex items-center justify-center bg-black/60 text-sm font-semibold text-white">
                      +{remaining}
                    </div>
                  )}
                </button>
              )
            })}
          </div>
        )}
      </div>

      {/* Lightbox */}
      {lightboxIndex !== null && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 p-4"
          onClick={close}
        >
          <Button
            variant="ghost"
            size="icon"
            className="absolute top-4 right-4 text-white hover:bg-white/10"
            onClick={close}
            aria-label="Close"
          >
            <X className="h-5 w-5" />
          </Button>

          <Button
            variant="ghost"
            size="icon"
            className="absolute top-1/2 left-4 -translate-y-1/2 text-white hover:bg-white/10"
            onClick={(e) => {
              e.stopPropagation()
              prev()
            }}
            aria-label="Previous"
          >
            <ChevronLeft className="h-6 w-6" />
          </Button>

          <img
            src={images[lightboxIndex].url}
            alt={`Flat ${lightboxIndex + 1}`}
            className="max-h-[90vh] max-w-[90vw] rounded-lg object-contain"
            onClick={(e) => e.stopPropagation()}
          />

          <Button
            variant="ghost"
            size="icon"
            className="absolute top-1/2 right-4 -translate-y-1/2 text-white hover:bg-white/10"
            onClick={(e) => {
              e.stopPropagation()
              next()
            }}
            aria-label="Next"
          >
            <ChevronRight className="h-6 w-6" />
          </Button>

          <div className="absolute bottom-4 left-1/2 -translate-x-1/2 rounded-full bg-black/60 px-3 py-1 text-sm text-white">
            {lightboxIndex + 1} / {images.length}
          </div>
        </div>
      )}
    </>
  )
}