import { useNavigate } from "react-router-dom"
import { ArrowLeft } from "lucide-react"
import { Link } from "react-router-dom"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { FlatForm } from "@/features/flats/components/FlatForm"
import { useCreateFlat } from "@/features/flats/hooks/useCreateFlat"
import { toCreateRequest } from "@/features/flats/utils"
import { useAuthStore } from "@/features/auth/store"
import type { FlatFormValues } from "@/features/flats/schemas"

export default function CreateFlatPage() {
  const navigate = useNavigate()
  const createFlat = useCreateFlat()
  const user = useAuthStore((s) => s.user)

  const handleSubmit = (values: FlatFormValues) => {
    createFlat.mutate(toCreateRequest(values), {
      onSuccess: () => {
        toast.success(
          user?.role === "LANDLORD"
            ? "Flat listed — it's now live"
            : "Flat added — it will appear publicly after your first approved review",
        )
        navigate(`/me/flats`)
        // or navigate(`/flats/${flat.id}`)
      },
    })
  }

  return (
    <section className="container mx-auto max-w-3xl px-4 py-8">
      <Button asChild variant="ghost" size="sm" className="mb-4">
        <Link to="/me/flats">
          <ArrowLeft className="mr-1 h-4 w-4" /> Back
        </Link>
      </Button>

      <div className="mb-6 space-y-1">
        <h1 className="text-2xl font-bold">
          {user?.role === "LANDLORD" ? "List a flat" : "Add a place you've lived in"}
        </h1>
        <p className="text-sm text-muted-foreground">
          {user?.role === "LANDLORD"
            ? "List a flat you rent out. It will appear in public listings immediately."
            : "Add a place you've lived in. It will appear publicly after your first approved review."}
        </p>
      </div>

      <FlatForm
        submitLabel="Create flat"
        onSubmit={handleSubmit}
        isSubmitting={createFlat.isPending}
      />
    </section>
  )
}