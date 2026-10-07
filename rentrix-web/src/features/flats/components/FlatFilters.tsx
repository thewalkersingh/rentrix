import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { X } from "lucide-react"
import type { PropertyType } from "@/types"
import { useFiltersStore } from "../store"

const PROPERTY_TYPES: { value: PropertyType; label: string }[] = [
  { value: "APARTMENT", label: "Apartment" },
  { value: "INDEPENDENT_HOUSE", label: "Independent House" },
  { value: "PG", label: "PG" },
  { value: "ROOM", label: "Room" },
  { value: "STUDIO", label: "Studio" },
  { value: "VILLA", label: "Villa" },
]

// Rating filter options — value is a string because Select requires it
type RatingOption = "any" | "has" | "8" | "6" | "4"

const RATING_OPTIONS: { value: RatingOption; label: string }[] = [
  { value: "any", label: "Any rating" },
  { value: "has", label: "Has reviews" },
  { value: "8", label: "Excellent (8+)" },
  { value: "6", label: "Good (6+)" },
  { value: "4", label: "OK (4+)" },
]

/** Convert current filter state → select value */
function currentRatingValue(filters: { minRating?: number; hasReviews?: boolean }): RatingOption {
  if (filters.hasReviews === true && filters.minRating == null) return "has"
  if (filters.minRating === 8) return "8"
  if (filters.minRating === 6) return "6"
  if (filters.minRating === 4) return "4"
  return "any"
}

export function FlatFilters() {
  const filters = useFiltersStore((s) => s.filters)
  const setFilter = useFiltersStore((s) => s.setFilter)
  const setFilters = useFiltersStore((s) => s.setFilters)
  const resetFilters = useFiltersStore((s) => s.resetFilters)

  const hasAnyFilter =
    filters.city ||
    filters.minRent != null ||
    filters.maxRent != null ||
    filters.minRooms != null ||
    filters.maxRooms != null ||
    filters.furnished != null ||
    filters.parking != null ||
    filters.propertyType != null ||
    filters.available != null ||
    filters.minRating != null ||
    filters.hasReviews === true

  const handleRatingChange = (v: RatingOption) => {
    // Clear both, then set based on selection
    if (v === "any") {
      setFilters({ minRating: undefined, hasReviews: undefined })
    } else if (v === "has") {
      setFilters({ minRating: undefined, hasReviews: true })
    } else {
      setFilters({ minRating: Number(v), hasReviews: undefined })
    }
  }

  return (
    <div className="space-y-4 rounded-lg border bg-card p-4">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-semibold">Filters</h2>
        {hasAnyFilter && (
          <Button variant="ghost" size="sm" onClick={resetFilters}>
            <X className="mr-1 h-3 w-3" /> Clear
          </Button>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="city">City</Label>
        <Input
          id="city"
          placeholder="e.g. Bangalore"
          value={filters.city ?? ""}
          onChange={(e) => setFilter("city", e.target.value || undefined)}
        />
      </div>

      {/* ── Rating filter ──────────────────────────────────────────── */}
      <div className="space-y-2">
        <Label>Rating</Label>
        <Select value={currentRatingValue(filters)} onValueChange={handleRatingChange}>
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {RATING_OPTIONS.map((o) => (
              <SelectItem key={o.value} value={o.value}>
                {o.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* ── Rent range ─────────────────────────────────────────────── */}
      <div className="grid grid-cols-2 gap-2">
        <div className="space-y-2">
          <Label htmlFor="minRent">Min rent</Label>
          <Input
            id="minRent"
            type="number"
            min={0}
            value={filters.minRent ?? ""}
            onChange={(e) =>
              setFilter("minRent", e.target.value ? Number(e.target.value) : undefined)
            }
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="maxRent">Max rent</Label>
          <Input
            id="maxRent"
            type="number"
            min={0}
            value={filters.maxRent ?? ""}
            onChange={(e) =>
              setFilter("maxRent", e.target.value ? Number(e.target.value) : undefined)
            }
          />
        </div>
      </div>

      {/* ── Rooms ──────────────────────────────────────────────────── */}
      <div className="space-y-2">
        <Label>Rooms</Label>
        <Select
          value={filters.minRooms != null ? String(filters.minRooms) : "any"}
          onValueChange={(v) => {
            if (v === "any") {
              setFilters({ minRooms: undefined, maxRooms: undefined })
            } else {
              setFilters({ minRooms: Number(v), maxRooms: Number(v) })
            }
          }}
        >
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="any">Any</SelectItem>
            <SelectItem value="1">1 BHK</SelectItem>
            <SelectItem value="2">2 BHK</SelectItem>
            <SelectItem value="3">3 BHK</SelectItem>
            <SelectItem value="4">4 BHK</SelectItem>
            <SelectItem value="5">5+ BHK</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* ── Property type ──────────────────────────────────────────── */}
      <div className="space-y-2">
        <Label>Property type</Label>
        <Select
          value={filters.propertyType ?? "any"}
          onValueChange={(v) =>
            setFilter("propertyType", v === "any" ? undefined : (v as PropertyType))
          }
        >
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="any">Any</SelectItem>
            {PROPERTY_TYPES.map((p) => (
              <SelectItem key={p.value} value={p.value}>
                {p.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* ── Furnishing ─────────────────────────────────────────────── */}
      <div className="space-y-2">
        <Label>Furnishing</Label>
        <Select
          value={filters.furnished == null ? "any" : filters.furnished ? "true" : "false"}
          onValueChange={(v) => setFilter("furnished", v === "any" ? undefined : v === "true")}
        >
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="any">Any</SelectItem>
            <SelectItem value="true">Furnished</SelectItem>
            <SelectItem value="false">Unfurnished</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/*TODO: We have to decide if we can put Rooms and Furnishing filters together*/}


      {/* ── Parking ────────────────────────────────────────────────── */}
      <div className="space-y-2">
        <Label>Parking</Label>
        <Select
          value={filters.parking == null ? "any" : filters.parking ? "true" : "false"}
          onValueChange={(v) => setFilter("parking", v === "any" ? undefined : v === "true")}
        >
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="any">Any</SelectItem>
            <SelectItem value="true">Yes</SelectItem>
            <SelectItem value="false">No</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* ── Availability ───────────────────────────────────────────── */}
      <div className="space-y-2">
        <Label>Availability</Label>
        <Select
          value={filters.available == null ? "any" : filters.available ? "true" : "false"}
          onValueChange={(v) => setFilter("available", v === "any" ? undefined : v === "true")}
        >
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="any">Any</SelectItem>
            <SelectItem value="true">Available</SelectItem>
            <SelectItem value="false">Occupied</SelectItem>
          </SelectContent>
        </Select>
      </div>
    </div>
  )
}