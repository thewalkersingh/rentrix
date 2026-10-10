import { useState } from "react"
import { Link } from "react-router-dom"
import { AlertCircle, Check, FileText, Loader2, Star, X } from "lucide-react"
import { toast } from "sonner"
import { usePendingReviews } from "@/features/reviews/hooks/usePendingReviews"
import { useModerateReview } from "@/features/reviews/hooks/useModerateReview"
import { reviewsApi } from "@/api/reviews"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import type { ReviewProof } from "@/types"

function formatDate(iso?: string | null) {
  if (!iso) return ""
  const d = new Date(iso)
  return Number.isNaN(d.getTime())
    ? ""
    : d.toLocaleDateString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric",
      })
}

export default function AdminPage() {
  const { data, isLoading, isError, refetch } = usePendingReviews()
  const moderate = useModerateReview()

  const [proofsByReview, setProofsByReview] = useState<Record<number, ReviewProof[]>>({})
  const [loadingProofId, setLoadingProofId] = useState<number | null>(null)

  const loadProofs = async (reviewId: number) => {
    try {
      const proofs = await reviewsApi.listProofs(reviewId)
      setProofsByReview((prev) => ({ ...prev, [reviewId]: proofs }))
    } catch {
      toast.error("Failed to load proofs")
    }
  }

  const openProof = async (reviewId: number, proofId: number) => {
    setLoadingProofId(proofId)
    try {
      const { url } = await reviewsApi.getProofUrl(reviewId, proofId)
      window.open(url, "_blank", "noopener,noreferrer")
    } catch {
      toast.error("Failed to open proof")
    } finally {
      setLoadingProofId(null)
    }
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
        {pending.map((r) => {
          const reviewProofs = proofsByReview[r.id]
          const dateLabel = formatDate(r.createdAt ?? r.reviewDate)

          return (
            <Card key={r.id}>
              <CardHeader className="flex flex-row items-start justify-between gap-3 space-y-0">
                <div className="space-y-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <Badge variant="secondary">
                      <Star className="mr-1 h-3 w-3 fill-current" />
                      {r.rating}/10
                    </Badge>
                    <span className="text-xs text-muted-foreground">by {r.userName}</span>
                    {r.hasProof && (
                      <Badge
                        variant="outline"
                        className="gap-1 border-amber-500/30 bg-amber-50 text-amber-800 dark:bg-amber-950/20 dark:text-amber-300"
                      >
                        <FileText className="h-3 w-3" />
                        Has proof
                      </Badge>
                    )}
                  </div>
                  <Link
                    to={`/flats/${r.flatId}`}
                    className="text-sm text-muted-foreground underline-offset-4 hover:underline"
                  >
                    {r.flatAddress ?? `Flat #${r.flatId}`}
                  </Link>
                </div>
                {dateLabel && <span className="text-xs text-muted-foreground">{dateLabel}</span>}
              </CardHeader>

              <CardContent className="space-y-3">
                <div className="space-y-2">
                  <h3 className="leading-tight font-semibold">{r.title}</h3>
                  <p className="text-sm whitespace-pre-wrap text-muted-foreground">{r.content}</p>
                </div>

                {/* Proof section */}
                {r.hasProof && (
                  <div className="space-y-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => loadProofs(r.id)}
                      disabled={!!reviewProofs}
                    >
                      <FileText className="mr-1 h-4 w-4" />
                      {reviewProofs
                        ? `${reviewProofs.length} proof${reviewProofs.length === 1 ? "" : "s"}`
                        : "Load proofs"}
                    </Button>

                    {reviewProofs?.map((p) => (
                      <div
                        key={p.id}
                        className="flex items-center gap-2 rounded-md border bg-muted/30 p-2 text-xs"
                      >
                        <FileText className="h-3 w-3 shrink-0 text-muted-foreground" />
                        <span className="flex-1 truncate">{p.originalFilename ?? "proof"}</span>
                        <Button
                          variant="ghost"
                          size="sm"
                          className="h-6 px-2 text-xs"
                          onClick={() => openProof(r.id, p.id)}
                          disabled={loadingProofId === p.id}
                        >
                          {loadingProofId === p.id ? (
                            <Loader2 className="h-3 w-3 animate-spin" />
                          ) : (
                            "Open"
                          )}
                        </Button>
                      </div>
                    ))}
                  </div>
                )}

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
          )
        })}
      </div>
    </section>
  )
}