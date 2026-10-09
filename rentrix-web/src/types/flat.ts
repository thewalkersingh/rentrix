import type { AddressDto } from "./common"

export type PropertyType = "APARTMENT" | "INDEPENDENT_HOUSE" | "PG" | "ROOM" | "STUDIO" | "VILLA"

export interface FlatSummary {
  id: number
  rent: number
  numberOfRooms: number
  area?: number
  furnished?: boolean
  bathrooms?: number
  parking?: boolean
  availableFrom?: string
  propertyType: PropertyType
  available: boolean

  // Flattened address bits
  addressLine?: string
  city?: string
  state?: string

  // v0.1.2 — visibility flags
  verified?: boolean
  visible?: boolean

  // Ratings (populated by Step 4)
  averageRating?: number | null
  reviewCount?: number | null

  primaryImageUrl?: string | null
}

export interface Flat {
  id: number
  rent: number
  numberOfRooms: number
  area?: number
  floorNumber?: number
  totalFloors?: number
  furnished?: boolean
  bathrooms?: number
  parking?: boolean
  availableFrom?: string
  propertyType: PropertyType
  description?: string
  available: boolean

  address: AddressDto

  ownerId?: number
  ownerName?: string

  // v0.1.2 — who created this flat + lifecycle flags
  createdById?: number
  createdByName?: string
  verified?: boolean
  visible?: boolean

  averageRating?: number | null
  reviewCount?: number | null

  createdAt: string
  updatedAt: string

  images?: FlatImage[]
  primaryImageUrl?: string | null
}

export interface FlatFilters {
   city?: string
   state?: string
   minRent?: number
   maxRent?: number
   minRooms?: number
   maxRooms?: number
   furnished?: boolean
   parking?: boolean
   propertyType?: PropertyType
   available?: boolean
   q?: string
   page?: number
   size?: number
   sort?: string

   // v0.1.2 — review-based
   minRating?: number
   hasReviews?: boolean
}

export interface CreateFlatRequest {
  rent: number
  numberOfRooms: number
  area?: number
  floorNumber?: number
  totalFloors?: number
  furnished?: boolean
  bathrooms?: number
  parking?: boolean
  availableFrom?: string
  propertyType: PropertyType
  description?: string
  available?: boolean
  address: AddressDto
}
export interface FlatImage {
   id: number
   url: string
   displayOrder: number
   isPrimary: boolean
}
export type UpdateFlatRequest = Partial<CreateFlatRequest>