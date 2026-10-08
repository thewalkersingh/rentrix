import { useNavigate, useParams, Link } from "react-router-dom"
import { AlertCircle, ArrowLeft, Loader2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { FlatForm } from "@/features/flats/components/FlatForm"
import { useFlat } from "@/features/flats/hooks/useFlat"
import { useUpdateFlat } from "@/features/flats/hooks/useUpdateFlat"
import { toFormValues, toUpdateRequest } from "@/features/flats/utils"
import type { FlatFormValues } from "@/features/flats/schemas"

export default function EditFlatPage() {
  const { id } = useParams<{ id: string }>()
  const flatId = Number(id)
  const navigate = useNavigate()
  const { data: flat, isLoading, isError } = useFlat(flatId)
  const updateFlat = useUpdateFlat()

  if (isLoading) {
    return (
      <div className="container mx-auto flex justify-center px-4 py-16">
        <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
      </div>
    )
  }

  if (isError || !flat) {
    return (
      <div className="container mx-auto flex flex-col items-center gap-3 px-4 py-16 text-center">
        <AlertCircle className="h-8 w-8 text-destructive" />
        <p className="text-muted-foreground">Flat not found.</p>
        <Button asChild variant="outline">
          <Link to="/me/flats">Back to my flats</Link>
        </Button>
      </div>
    )
  }

  const handleSubmit = (values: FlatFormValues) => {
    updateFlat.mutate(
      { id: flatId, payload: toUpdateRequest(values) },
      { onSuccess: () => navigate("/me/flats") },
    )
  }

  return (
    <section className="container mx-auto max-w-3xl px-4 py-8">
      <Button asChild variant="ghost" size="sm" className="mb-4">
        <Link to="/me/flats">
          <ArrowLeft className="mr-1 h-4 w-4" /> Back
        </Link>
      </Button>

      <div className="mb-6">
        <h1 className="text-2xl font-bold">Edit flat</h1>
        <p className="text-sm text-muted-foreground">
          Update the details — only the fields you change are saved.
        </p>
      </div>

      <FlatForm
        defaultValues={toFormValues(flat)}
        submitLabel="Save changes"
        onSubmit={handleSubmit}
        isSubmitting={updateFlat.isPending}
      />
    </section>
  )
}