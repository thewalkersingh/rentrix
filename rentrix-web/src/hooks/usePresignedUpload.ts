import { useState } from "react"
import { compressImageIfNeeded } from "@/lib/imageCompression"
import { uploadToS3, type UploadProgress } from "@/lib/uploadToS3"
import type { PresignedUploadResponse } from "@/types"

export interface PresignedUploadFlow<T> {
  /** Called by the caller — initiates the flow. */
  upload: (file: File) => Promise<T | null>
  /** Uploading in progress. */
  isUploading: boolean
  /** Progress of the S3 PUT — null if not uploading. */
  progress: UploadProgress | null
  /** Any error from the flow. */
  error: Error | null
  /** Reset the flow (clear error, progress). */
  reset: () => void
}

interface Options<T> {
  /** Requests a presigned URL from the backend. */
  presign: (
    filename: string,
    contentType: string,
    sizeBytes: number,
  ) => Promise<PresignedUploadResponse>
  /** Confirms the upload with the backend after S3 PUT succeeds. */
  confirm: (payload: {
    key: string
    sizeBytes: number
    contentType: string
    originalFilename: string
  }) => Promise<T>
  /** Whether to compress before upload. Default: true for images. */
  shouldCompress?: boolean
  /** Called after successful upload. */
  onSuccess?: (result: T) => void
  /** Called on error. */
  onError?: (error: Error) => void
}

export function usePresignedUpload<T>({
  presign,
  confirm,
  shouldCompress = true,
  onSuccess,
  onError,
}: Options<T>): PresignedUploadFlow<T> {
  const [isUploading, setIsUploading] = useState(false)
  const [progress, setProgress] = useState<UploadProgress | null>(null)
  const [error, setError] = useState<Error | null>(null)

  const reset = () => {
    setError(null)
    setProgress(null)
  }

  const upload = async (file: File): Promise<T | null> => {
    setIsUploading(true)
    setError(null)
    setProgress(null)

    try {
      // 1. Compress if it's an image and compression is enabled
      let fileToUpload = file
      if (shouldCompress && file.type.startsWith("image/")) {
        const result = await compressImageIfNeeded(file)
        fileToUpload = result.file
        if (result.wasCompressed) {
          console.log(
            `Compressed: ${formatBytes(result.originalSize)} → ${formatBytes(result.compressedSize)}`,
          )
        }
      }

      // 2. Request presigned URL from backend
      const presigned = await presign(fileToUpload.name, fileToUpload.type, fileToUpload.size)

      // 3. Upload to S3 (with progress)
      setProgress({ loaded: 0, total: fileToUpload.size, percent: 0 })
      await uploadToS3(presigned.uploadUrl, fileToUpload, fileToUpload.type, setProgress)

      // 4. Confirm with backend
      const result = await confirm({
        key: presigned.key,
        sizeBytes: fileToUpload.size,
        contentType: fileToUpload.type,
        originalFilename: fileToUpload.name,
      })

      onSuccess?.(result)
      return result
    } catch (err) {
      const e = err instanceof Error ? err : new Error(String(err))
      setError(e)
      onError?.(e)
      return null
    } finally {
      setIsUploading(false)
      setProgress(null)
    }
  }

  return { upload, isUploading, progress, error, reset }
}

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(0)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}