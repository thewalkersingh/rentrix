import {
  useDeleteFlatImage,
  useFlatImages,
  useSetPrimaryImage,
  useUploadFlatImage,
} from "../hooks/useFlatImages"
import { ImageGridUploader, type GridUploadItem } from "./ImageGridUploader"

const MAX_IMAGES = 10
const MAX_SIZE_MB = 15

export function FlatImageUploader({ flatId }: { flatId: number }) {
  const { data: images = [], isLoading } = useFlatImages(flatId)
  const uploadImage = useUploadFlatImage(flatId)
  const deleteImage = useDeleteFlatImage(flatId)
  const setPrimary = useSetPrimaryImage(flatId)

  const items: GridUploadItem[] = images.map((img) => ({
    id: img.id,
    url: img.url,
    isPrimary: img.isPrimary,
    contentType: "image/jpeg",
  }))

  if (isLoading) {
    return (
      <div className="grid grid-cols-2 gap-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="aspect-square animate-pulse rounded-lg bg-muted" />
        ))}
      </div>
    )
  }

  return (
    <ImageGridUploader
      items={items}
      maxItems={MAX_IMAGES}
      columns={5}
      accept="image/jpeg,image/png,image/webp"
      maxSizeMB={MAX_SIZE_MB}
      uploading={uploadImage.isPending}
      disabled={deleteImage.isPending || setPrimary.isPending}
      emptyLabel="Add image"
      emptyHint="JPG, PNG, WebP"
      onUpload={(file) => uploadImage.mutate(file)}
      onDelete={(id) => deleteImage.mutate(id)}
      onSetPrimary={(id) => setPrimary.mutate(id)}
    />
  )
}