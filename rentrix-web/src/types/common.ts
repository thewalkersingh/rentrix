export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface ApiError {
  message: string
  status: number
  timestamp?: string
}

export interface AddressDto {
  addressLine?: string
  street?: string
  city?: string
  state?: string
  zipCode?: string
}
export interface PresignedUploadResponse {
  uploadUrl: string
  key: string
  publicUrl: string | null
  expiresAt: string
}