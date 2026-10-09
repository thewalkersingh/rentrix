import { Link } from "react-router-dom"
import { BedDouble, Building2, CalendarClock, MapPin, Ruler, Star } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { VerifiedBadge } from "./VerifiedBadge"
import type { FlatSummary, PropertyType } from "@/types"

function formatCurrency(n: number) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(n)
}

function formatDate(iso?: string) {
  if (!iso) return null
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
  })
}

const PROPERTY_LABEL: Record<PropertyType, string> = {
  APARTMENT: "Apartment",
  INDEPENDENT_HOUSE: "Independent House",
  PG: "PG",
  ROOM: "Room",
  STUDIO: "Studio",
  VILLA: "Villa",
}

function locationLine(flat: FlatSummary) {
  const parts = [flat.addressLine, flat.city].filter(Boolean)
  return parts.join(", ") || "Location not specified"
}

export function FlatCard({ flat }: { flat: FlatSummary }) {
  const availableFrom = formatDate(flat.availableFrom)

  return (
    <Card className="group relative flex h-full cursor-pointer flex-col overflow-hidden transition-all hover:-translate-y-0.5 hover:shadow-md">
      {/* Stretched link — full card clickable, keyboard accessible */}
      <Link
        to={`/flats/${flat.id}`}
        className="absolute inset-0 z-0"
        aria-label={`View ${locationLine(flat)}`}
      />

      {/* Image */}
      <div className="relative aspect-[16/10] w-full overflow-hidden bg-muted">
        {flat.primaryImageUrl ? (
          <img
            src={flat.primaryImageUrl}
            alt={locationLine(flat)}
            className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
            loading="lazy"
          />
        ) : (
          <div className="flex h-full items-center justify-center bg-gradient-to-br from-muted to-muted/50">
            <Building2 className="h-10 w-10 text-muted-foreground/40" />
          </div>
        )}
      </div>

      <CardHeader className="space-y-1">
        <div className="flex items-start justify-between gap-2">
          <CardTitle className="text-base leading-tight transition-colors group-hover:text-primary">
            {locationLine(flat)}
          </CardTitle>
          {flat.available ? (
            <Badge variant="success" className="shrink-0">
              Available
            </Badge>
          ) : (
            <Badge variant="secondary" className="shrink-0">
              Occupied
            </Badge>
          )}
        </div>

        <CardDescription className="flex flex-wrap items-center gap-1 text-xs">
          <MapPin className="h-3 w-3" />
          {[flat.city, flat.state].filter(Boolean).join(", ") || "—"}
          <span className="mx-1">·</span>
          <span>{PROPERTY_LABEL[flat.propertyType]}</span>
          {flat.verified && (
            <>
              <span className="mx-1">·</span>
              <VerifiedBadge verified />
            </>
          )}
        </CardDescription>
      </CardHeader>

      <CardContent className="flex-1 space-y-3">
        <div className="flex flex-wrap gap-3 text-xs text-muted-foreground">
          <span className="flex items-center gap-1">
            <BedDouble className="h-3 w-3" />
            {flat.numberOfRooms} BHK
          </span>
          {flat.area != null && (
            <span className="flex items-center gap-1">
              <Ruler className="h-3 w-3" />
              {flat.area} m²
            </span>
          )}
          {flat.bathrooms != null && (
            <span className="flex items-center gap-1">{flat.bathrooms} bath</span>
          )}
          {flat.furnished && <span>Furnished</span>}
          {flat.parking && <span>Parking</span>}
        </div>

        {flat.averageRating != null ? (
          <span className="flex items-center gap-1 text-xs text-muted-foreground">
            <Star className="h-3 w-3 fill-yellow-400 text-yellow-400" />
            {flat.averageRating.toFixed(1)}
            {flat.reviewCount != null && ` (${flat.reviewCount})`}
          </span>
        ) : (
          <span className="text-xs text-muted-foreground">No reviews yet</span>
        )}

        {!flat.available && availableFrom && (
          <p className="flex items-center gap-1 text-xs text-muted-foreground">
            <CalendarClock className="h-3 w-3" />
            Available from {availableFrom}
          </p>
        )}
      </CardContent>

      <CardFooter className="flex items-center justify-between border-t pt-4">
        <span className="relative z-10 text-lg font-semibold">{formatCurrency(flat.rent)}</span>
        <Button asChild size="sm" className="relative z-10">
          <Link to={`/flats/${flat.id}`}>View details</Link>
        </Button>
      </CardFooter>
    </Card>
  )
}