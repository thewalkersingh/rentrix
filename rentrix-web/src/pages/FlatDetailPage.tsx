import { Link, useParams } from "react-router-dom"
import { useState } from "react"
import { AlertCircle, ArrowLeft, MapPin, Star, User } from "lucide-react"
import { useFlat } from "@/features/flats/hooks/useFlat"
import { useReviews } from "@/features/reviews/hooks/useReviews"
import { useAuthStore } from "@/features/auth/store"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { ReviewForm } from "@/features/reviews/components/ReviewForm"
import { ReviewList } from "@/features/reviews/components/ReviewList"
import type { PropertyType } from "@/types"
import { VerifiedBadge } from "@/features/flats/components/VerifiedBadge.tsx"

function formatCurrency(n: number) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(n)
}

const PROPERTY_LABEL: Record<PropertyType, string> = {
  APARTMENT: "Apartment",
  INDEPENDENT_HOUSE: "Independent House",
  PG: "PG",
  ROOM: "Room",
  STUDIO: "Studio",
  VILLA: "Villa",
}

export default function FlatDetailPage() {
  const { id } = useParams<{ id: string }>()
  const flatId = Number(id)

  const { data: flat, isLoading, isError } = useFlat(flatId)
  const { data: reviewsPage, isLoading: reviewsLoading } = useReviews(flatId)
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)

  const [showForm, setShowForm] = useState(false)

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="h-64 animate-pulse rounded-lg border bg-muted/30" />
      </div>
    )
  }

  if (isError || !flat) {
    return (
      <div className="container mx-auto flex flex-col items-center gap-3 px-4 py-16 text-center">
        <AlertCircle className="h-8 w-8 text-destructive" />
        <p className="text-muted-foreground">Flat not found.</p>
        <Button asChild variant="outline">
          <Link to="/flats">Back to flats</Link>
        </Button>
      </div>
    )
  }

  const addressLine = [flat.address.addressLine, flat.address.street].filter(Boolean).join(", ")
  const cityLine = [flat.address.city, flat.address.state, flat.address.zipCode]
    .filter(Boolean)
    .join(", ")

  return (
    <section className="container mx-auto px-4 py-8">
      <Button asChild variant="ghost" size="sm" className="mb-4">
        <Link to="/flats">
          <ArrowLeft className="mr-1 h-4 w-4" /> Back
        </Link>
      </Button>

      <div className="grid gap-8 lg:grid-cols-[1fr_400px]">
        <div className="space-y-8">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div className="space-y-1">
              <div className="flex flex-wrap items-center gap-2">
                <h1 className="text-3xl leading-tight font-bold">
                  {addressLine || "Address not provided"}
                </h1>
                <VerifiedBadge verified={flat.verified} />
              </div>
              <p className="flex items-center gap-1 text-muted-foreground">
                <MapPin className="h-4 w-4" />
                {cityLine || "—"}
              </p>
              <p className="text-sm text-muted-foreground">{PROPERTY_LABEL[flat.propertyType]}</p>
            </div>
            {flat.available ? (
              <Badge>Available</Badge>
            ) : (
              <Badge variant="secondary">Currently occupied</Badge>
            )}
          </div>

          <Separator />

          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-semibold">
                Reviews {reviewsPage && `(${reviewsPage.totalElements})`}
              </h2>
              {isAuthenticated && !showForm && (
                <Button size="sm" onClick={() => setShowForm(true)}>
                  Write a review
                </Button>
              )}
            </div>

            {!isAuthenticated && (
              <p className="rounded-md border bg-muted/30 p-3 text-sm text-muted-foreground">
                <Link to="/login" className="text-primary underline-offset-4 hover:underline">
                  Sign in
                </Link>{" "}
                to write a review.
              </p>
            )}

            {showForm && (
              <div className="rounded-lg border bg-card p-4">
                <h3 className="mb-3 font-semibold">Your review</h3>
                <ReviewForm flatId={flatId} onSuccess={() => setShowForm(false)} />
              </div>
            )}

            <ReviewList reviews={reviewsPage?.content ?? []} isLoading={reviewsLoading} />
          </div>
        </div>

        <aside className="lg:sticky lg:top-20 lg:self-start">
          <div className="space-y-4 rounded-lg border bg-card p-6">
            <div className="space-y-1">
              <p className="text-sm text-muted-foreground">Monthly rent</p>
              <p className="text-3xl font-bold">{formatCurrency(flat.rent)}</p>
            </div>
            {flat.averageRating != null && (
              <>
                <Separator />
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Average rating</p>
                  <p className="flex items-center gap-2 text-2xl font-bold">
                    <Star className="h-5 w-5 fill-yellow-400 text-yellow-400" />
                    {flat.averageRating.toFixed(1)}
                    <span className="text-sm font-normal text-muted-foreground">/ 10</span>
                  </p>
                  {flat.reviewCount != null && (
                    <p className="text-xs text-muted-foreground">
                      Based on {flat.reviewCount} reviews
                    </p>
                  )}
                </div>
              </>
            )}
            {flat.ownerName && (
              <>
                <Separator />
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <User className="h-4 w-4" />
                  Listed by {flat.ownerName}
                </div>
              </>
            )}

            {!flat.ownerName && flat.createdByName && (
              <>
                <Separator />
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <User className="h-4 w-4" />
                  Added by {flat.createdByName}
                </div>
              </>
            )}
            <Separator />
            <Button className="w-full" disabled={!flat.available}>
              {flat.available ? "Contact landlord" : "Currently unavailable"}
            </Button>
            <p className="text-center text-xs text-muted-foreground">Contact feature coming soon</p>
          </div>
        </aside>
      </div>
    </section>
  )
}