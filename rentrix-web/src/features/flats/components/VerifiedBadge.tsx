import { BadgeCheck } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import { cn } from "@/lib/utils"

interface Props {
  verified?: boolean
  className?: string
}

export function VerifiedBadge({ verified, className }: Props) {
  if (!verified) return null
  return (
    <Badge variant="outline" className={cn("gap-1 text-xs", className)}>
      <BadgeCheck className="h-3 w-3" />
      Verified
    </Badge>
  )
}