export type ReviewStatus = "PENDING" | "APPROVED" | "REJECTED"

export interface Review {
  id: number
  userId: number
  userName: string
  flatId: number
  flatAddress?: string
  title: string
  content: string
  rating: number
  createdAt: string
  updatedAt?: string
  status: ReviewStatus
}

export interface CreateReviewRequest {
  title: string
  content: string
  rating: number
}