import { Link } from "react-router-dom"
import { AlertCircle, Loader2 } from "lucide-react"
import { useMyReviews } from "@/features/reviews/hooks/useMyReviews"
import { ReviewStatusBadge } from "@/features/reviews/components/ReviewStatusBadge"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Star } from "lucide-react"
import { EmptyState } from "@/components/common/EmptyState.tsx"

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  })
}

export default function MyReviewsPage() {
  const { data, isLoading, isError, refetch } = useMyReviews()

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
        <p className="text-muted-foreground">Failed to load your reviews.</p>
        <Button variant="outline" onClick={() => refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  const reviews = data?.content ?? []

  return (
    <section className="container mx-auto max-w-3xl px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">My reviews</h1>
        <p className="text-sm text-muted-foreground">
          {reviews.length === 0
            ? "You haven’t written any reviews yet."
            : `${reviews.length} review${reviews.length === 1 ? "" : "s"}`}
        </p>
      </div>

      {reviews.length === 0 && (
        <EmptyState
          icon={<Star className="h-8 w-8" />}
          title="No reviews yet"
          description="Browse flats and share your experience."
          action={
            <Button asChild>
              <Link to="/flats">Browse flats</Link>
            </Button>
          }
        />
      )}

      <div className="space-y-4">
        {reviews.map((r) => (
          <Card key={r.id}>
            <CardHeader className="flex flex-row items-start justify-between gap-3 space-y-0">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <Badge variant="secondary">
                    <Star className="mr-1 h-3 w-3 fill-current" />
                    {r.rating}/10
                  </Badge>
                  <ReviewStatusBadge status={r.status} />
                </div>
                <Link
                  to={`/flats/${r.flatId}`}
                  className="text-sm text-muted-foreground underline-offset-4 hover:underline"
                >
                  {r.flatAddress ?? `Flat #${r.flatId}`}
                </Link>
              </div>
              <span className="text-xs text-muted-foreground">{formatDate(r.createdAt)}</span>
            </CardHeader>
            <CardContent className="space-y-2">
              <h3 className="leading-tight font-semibold">{r.title}</h3>
              <p className="text-sm whitespace-pre-wrap text-muted-foreground">{r.content}</p>
              {r.status === "PENDING" && (
                <p className="text-xs text-muted-foreground">
                  Waiting for moderation before it appears publicly.
                </p>
              )}
              {r.status === "REJECTED" && (
                <p className="text-xs text-destructive">This review was rejected by a moderator.</p>
              )}
            </CardContent>
          </Card>
        ))}
      </div>
    </section>
  )
}