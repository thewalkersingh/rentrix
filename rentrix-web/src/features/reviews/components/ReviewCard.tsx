import { BadgeCheck, Star } from "lucide-react"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import type { Review } from "@/types"

function initials(name: string | null | undefined) {
   if (!name) return '?'
   return name
       .split(' ')
       .map((p) => p[0])
       .filter(Boolean)
       .slice(0, 2)
       .join('')
       .toUpperCase() || '?'
}

function ratingColor(rating: number) {
  if (rating >= 8) return "bg-green-100 text-green-700 dark:bg-green-950/40 dark:text-green-400"
  if (rating >= 5) return "bg-yellow-100 text-yellow-700 dark:bg-yellow-950/40 dark:text-yellow-400"
  return "bg-red-100 text-red-700 dark:bg-red-950/40 dark:text-red-400"
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  })
}

export function ReviewCard({ review }: { review: Review }) {
  return (
    <Card>
      <CardHeader className="flex flex-row items-start justify-between gap-4 space-y-0">
        <div className="flex items-center gap-3">
          <Avatar className="h-9 w-9">
            <AvatarFallback>{initials(review.userName)}</AvatarFallback>
          </Avatar>
          <div>
            <div className="flex items-center gap-2">
              <p className="text-sm font-medium">{review.userName}</p>
              {review.verifiedStay && (
                <Badge
                  variant="outline"
                  className="gap-1 border-green-500/30 bg-green-50 text-green-800 dark:bg-green-950/20 dark:text-green-300"
                >
                  <BadgeCheck className="h-3 w-3" />
                  Verified stay
                </Badge>
              )}
            </div>
            <p className="text-xs text-muted-foreground">
              {formatDate(review.createdAt ?? review.reviewDate)}
            </p>
          </div>
        </div>
        <Badge className={ratingColor(review.rating)}>
          <Star className="mr-1 h-3 w-3 fill-current" />
          {review.rating}/10
        </Badge>
      </CardHeader>
      <CardContent className="space-y-2">
        <h3 className="leading-tight font-semibold">{review.title}</h3>
        <p className="text-sm whitespace-pre-wrap text-muted-foreground">{review.content}</p>
      </CardContent>
    </Card>
  )
}