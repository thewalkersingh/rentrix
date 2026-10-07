import { AlertCircle, Check, Loader2, Star, X } from "lucide-react"
import { usePendingReviews } from "@/features/reviews/hooks/usePendingReviews"
import { useModerateReview } from "@/features/reviews/hooks/useModerateReview"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Link } from "react-router-dom"

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  })
}

export default function AdminPage() {
  const { data, isLoading, isError, refetch } = usePendingReviews()
  const moderate = useModerateReview()

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
        <p className="text-muted-foreground">Failed to load moderation queue.</p>
        <Button variant="outline" onClick={() => refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  const pending = data?.content ?? []

  return (
    <section className="container mx-auto max-w-3xl px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Moderation queue</h1>
        <p className="text-sm text-muted-foreground">
          {pending.length === 0
            ? "No pending reviews."
            : `${pending.length} review${pending.length === 1 ? "" : "s"} awaiting moderation`}
        </p>
      </div>
      {pending.length === 0 && (
        <div className="rounded-lg border py-16 text-center text-muted-foreground">
          All caught up. 🎉
        </div>
      )}

      <div className="space-y-4">
        {pending.map((r) => (
          <Card key={r.id}>
            <CardHeader className="flex flex-row items-start justify-between gap-3 space-y-0">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <Badge variant="secondary">
                    <Star className="mr-1 h-3 w-3 fill-current" />
                    {r.rating}/10
                  </Badge>
                  <span className="text-xs text-muted-foreground">by {r.userName}</span>
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
            <CardContent className="space-y-3">
              <div className="space-y-2">
                <h3 className="leading-tight font-semibold">{r.title}</h3>
                <p className="text-sm whitespace-pre-wrap text-muted-foreground">{r.content}</p>
              </div>
              <div className="flex gap-2">
                <Button
                  size="sm"
                  onClick={() => moderate.mutate({ id: r.id, status: "APPROVED" })}
                  disabled={moderate.isPending}
                >
                  <Check className="mr-1 h-4 w-4" /> Approve
                </Button>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => moderate.mutate({ id: r.id, status: "REJECTED" })}
                  disabled={moderate.isPending}
                >
                  <X className="mr-1 h-4 w-4" /> Reject
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </section>
  )
}