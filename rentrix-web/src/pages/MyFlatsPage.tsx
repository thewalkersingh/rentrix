import { Link } from "react-router-dom"
import { AlertCircle, Building2, Loader2, Plus } from "lucide-react"
import { useMyFlats } from "@/features/flats/hooks/useMyFlats"
import { useAuthStore } from "@/features/auth/store"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { EmptyState } from "@/components/common/EmptyState"
import { VerifiedBadge } from "@/features/flats/components/VerifiedBadge"
import type { FlatSummary } from "@/types"

function formatCurrency(n: number) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(n)
}

function locationLine(flat: FlatSummary) {
  return [flat.addressLine, flat.city].filter(Boolean).join(", ") || "Location not specified"
}

function statusLabel(flat: FlatSummary) {
  if (flat.visible) return { text: "Live", variant: "default" as const }
  return { text: "Pending review", variant: "secondary" as const }
}

export default function MyFlatsPage() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)
  const user = useAuthStore((s) => s.user)
  const { data, isLoading, isError, refetch } = useMyFlats()

  if (!isAuthenticated) {
    return (
      <section className="container mx-auto px-4 py-16 text-center">
        <p className="mb-4 text-muted-foreground">Sign in to see your flats.</p>
        <Button asChild>
          <Link to="/login">Sign in</Link>
        </Button>
      </section>
    )
  }

  if (isLoading) {
    return (
      <div className="container mx-auto flex justify-center px-4 py-16">
        <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
      </div>
    )
  }

  if (isError) {
    return (
      <div className="container mx-auto flex flex-col items-center gap-3 px-4 py-16 text-center">
        <AlertCircle className="h-8 w-8 text-destructive" />
        <p className="text-muted-foreground">Failed to load your flats.</p>
        <Button variant="outline" onClick={() => refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  const flats = data?.content ?? []
  const isLandlord = user?.role === "LANDLORD"
  const heading = isLandlord ? "My listings" : "My places"
  const subheading = isLandlord ? "Flats you rent out" : "Places you've added to review"

  const emptyTitle = isLandlord ? "No listings yet" : "No places yet"
  const emptyDescription = isLandlord
    ? "List a flat you rent out — it will appear in public listings immediately."
    : "Add a place you've lived in and share your honest review."

  return (
    <section className="container mx-auto max-w-4xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">{heading}</h1>
          <p className="text-sm text-muted-foreground">
            {flats.length === 0
              ? subheading
              : `${flats.length} ${isLandlord ? "listing" : "place"}${flats.length === 1 ? "" : "s"}`}
          </p>
        </div>
        <Button asChild>
          <Link to="/me/flats/new">
            <Plus className="mr-1 h-4 w-4" />
            {isLandlord ? "Add listing" : "Add place"}
          </Link>
        </Button>
      </div>

      {flats.length === 0 && (
        <EmptyState
          icon={<Building2 className="h-8 w-8" />}
          title={emptyTitle}
          description={emptyDescription}
          action={
            <Button asChild>
              <Link to="/me/flats/new">
                {isLandlord ? "Add your first listing" : "Add your first place"}
              </Link>
            </Button>
          }
        />
      )}

      <div className="space-y-3">
        {flats.map((flat) => {
          const status = statusLabel(flat)
          return (
            <Card key={flat.id}>
              <CardHeader className="flex flex-row items-start justify-between gap-3 space-y-0">
                <div className="space-y-1">
                  <CardTitle className="text-base leading-tight">{locationLine(flat)}</CardTitle>
                  <p className="text-sm text-muted-foreground">
                    {[flat.city, flat.state].filter(Boolean).join(", ")}
                  </p>
                </div>
                <div className="flex shrink-0 flex-wrap justify-end gap-1">
                  <Badge variant={status.variant}>{status.text}</Badge>
                  <VerifiedBadge verified={flat.verified} />
                </div>
              </CardHeader>
              <CardContent className="text-sm text-muted-foreground">
                <span className="font-medium text-foreground">{formatCurrency(flat.rent)}</span>
                {" · "}
                {flat.numberOfRooms} BHK
                {flat.area != null && ` · ${flat.area} m²`}
              </CardContent>
              <CardFooter className="gap-2 border-t pt-4">
                <Button asChild variant="outline" size="sm">
                  <Link to={`/flats/${flat.id}`}>View</Link>
                </Button>
                <Button asChild variant="ghost" size="sm">
                  <Link to={`/me/flats/${flat.id}/edit`}>Edit</Link>
                </Button>
              </CardFooter>
            </Card>
          )
        })}
      </div>
    </section>
  )
}