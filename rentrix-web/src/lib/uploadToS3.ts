/**
 * Uploads a file directly to a presigned S3 URL.
 * Uses XMLHttpRequest for progress events (fetch doesn't support them).
 *
 * Important: does NOT use the shared Axios instance — that would attach our
 * Authorization header, which S3 rejects. S3 expects only the signed URL.
 */
export interface UploadProgress {
  loaded: number
  total: number
  percent: number
}

export function uploadToS3(
  presignedUrl: string,
  file: File,
  contentType: string,
  onProgress?: (p: UploadProgress) => void,
): Promise<void> {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()

    xhr.open("PUT", presignedUrl, true)
    xhr.setRequestHeader("Content-Type", contentType)

    if (onProgress) {
      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          onProgress({
            loaded: event.loaded,
            total: event.total,
            percent: Math.round((event.loaded / event.total) * 100),
          })
        }
      }
    }

    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        resolve()
      } else {
        reject(
          new Error(
            `S3 upload failed with status ${xhr.status}: ${xhr.responseText || "unknown error"}`,
          ),
        )
      }
    }

    xhr.onerror = () => reject(new Error("S3 upload failed: network error"))
    xhr.ontimeout = () => reject(new Error("S3 upload failed: timeout"))

    xhr.timeout = 120_000 // 2 min max — generous for 20 MB proofs on slow connections

    xhr.send(file)
  })
}