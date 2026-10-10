export type ReviewStatus = "PENDING" | "APPROVED" | "REJECTED"

export interface Review {
  id: number
  userId: number
  userName?: string
  flatId: number
  flatAddress?: string
  title: string
  content: string
  rating: number

  reviewDate?: string
  createdAt?: string

  status: ReviewStatus

  // v0.1.3 — proof of living
  hasProof?: boolean
  verifiedStay?: boolean
}
export interface CreateReviewRequest {
  title: string
  content: string
  rating: number
}

export interface ReviewProof {
  id: number
  originalFilename?: string
  contentType: string
  sizeBytes: number
  displayOrder: number
  verified: boolean
}