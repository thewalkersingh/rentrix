import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import type { Resolver } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Separator } from "@/components/ui/separator"
import { createReviewSchema, type CreateReviewFormValues } from "../schemas"
import { useCreateReview } from "../hooks/useCreateReview"
import { RatingInput } from "./RatingInput"
import { ProofUploader } from "./ProofUploader"
import type { Review } from "@/types"

interface Props {
  flatId: number
  onSuccess?: () => void
}

export function ReviewForm({ flatId, onSuccess }: Props) {
  const createReview = useCreateReview(flatId)
  const [createdReview, setCreatedReview] = useState<Review | null>(null)

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors },
  } = useForm<CreateReviewFormValues>({
    resolver: zodResolver(createReviewSchema) as Resolver<CreateReviewFormValues>,
    defaultValues: { title: "", content: "", rating: undefined as unknown as number },
  })

  const onSubmit = (values: CreateReviewFormValues) => {
    createReview.mutate(values, {
      onSuccess: (review) => {
        reset()
        setCreatedReview(review)
      },
    })
  }

  // Step 2: after review submitted, offer proof upload
  if (createdReview) {
    return (
      <div className="space-y-4">
        <div className="rounded-lg border border-green-500/30 bg-green-50 p-3 text-sm dark:bg-green-950/20">
          <p className="font-medium text-green-800 dark:text-green-300">
            Review submitted for moderation
          </p>
          <p className="mt-1 text-xs text-muted-foreground">
            Optionally add proof of living to get a "Verified stay" badge.
          </p>
        </div>

        <div className="space-y-2">
          <h3 className="text-sm font-medium">Proof of living (optional)</h3>
          <ProofUploader reviewId={createdReview.id} />
        </div>

        <Separator />

        <Button
          type="button"
          variant="outline"
          className="w-full"
          onClick={() => {
            setCreatedReview(null)
            onSuccess?.()
          }}
        >
          Done
        </Button>
      </div>
    )
  }

  // Step 1: fill the review
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label>Your rating</Label>
        <Controller
          control={control}
          name="rating"
          render={({ field }) => (
            <RatingInput
              value={field.value}
              onChange={field.onChange}
              disabled={createReview.isPending}
            />
          )}
        />
        {errors.rating && <p className="text-sm text-destructive">{errors.rating.message}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="title">Title</Label>
        <Input
          id="title"
          placeholder="Summarise your experience"
          {...register("title")}
          disabled={createReview.isPending}
        />
        {errors.title && <p className="text-sm text-destructive">{errors.title.message}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="content">Review</Label>
        <Textarea
          id="content"
          rows={5}
          placeholder="What did you like or dislike? Rent, maintenance, landlord, area…"
          {...register("content")}
          disabled={createReview.isPending}
        />
        {errors.content && <p className="text-sm text-destructive">{errors.content.message}</p>}
      </div>

      <Button type="submit" disabled={createReview.isPending}>
        {createReview.isPending ? "Posting…" : "Post review"}
      </Button>
    </form>
  )
}