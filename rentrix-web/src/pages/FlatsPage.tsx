import { AlertCircle, Loader2 } from "lucide-react"
import { useFlats } from "@/features/flats/hooks/useFlats"
import { FlatCard } from "@/features/flats/components/FlatCard"
import { FlatFilters } from "@/features/flats/components/FlatFilters"
import { FlatPagination } from "@/features/flats/components/FlatPagination"
import { useFiltersStore } from "@/features/flats/store"
import { Button } from "@/components/ui/button"
import { Link } from "react-router-dom"
import { Plus } from "lucide-react"
import { useAuthStore } from "@/features/auth/store"

export default function FlatsPage() {
  const { data, isLoading, isError, error, refetch, isFetching, isSlow } = useFlats()
  const filters = useFiltersStore((s) => s.filters)
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)
  const resetFilters = useFiltersStore((s) => s.resetFilters)
  const flats = data?.content ?? []

  return (
    <section className="container mx-auto px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Browse flats</h1>
          <p className="text-sm text-muted-foreground">
            {data ? `${data.totalElements} flats found` : "Loading…"}
          </p>
        </div>

        <div className="flex items-center gap-2">
          {isFetching && !isLoading && (
            <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
          )}
          {isAuthenticated && (
            <Button asChild size="sm">
              <Link to="/me/flats/new">
                <Plus className="mr-1 h-4 w-4" />
                Add a place
              </Link>
            </Button>
          )}
        </div>
      </div>

      {isSlow && (
        <div className="mb-4 flex items-center gap-2 rounded-lg border border-yellow-300/50 bg-yellow-50 px-4 py-3 text-sm text-yellow-800 dark:bg-yellow-950/30 dark:text-yellow-300">
          <Loader2 className="h-4 w-4 animate-spin" />
          Waking up the server — first request after inactivity can take 30–60 seconds. Please wait…
        </div>
      )}

      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        <aside className="lg:sticky lg:top-20 lg:self-start">
          <FlatFilters />
        </aside>

        <div>
          {isLoading && (
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {Array.from({ length: 6 }).map((_, i) => (
                <div key={i} className="h-64 animate-pulse rounded-lg border bg-muted/30" />
              ))}
            </div>
          )}

          {isError && (
            <div className="flex flex-col items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/5 py-12 text-center">
              <AlertCircle className="h-8 w-8 text-destructive" />
              <p className="text-sm text-muted-foreground">
                {(error as { response?: { data?: { message?: string } } })?.response?.data
                  ?.message ?? "Failed to load flats"}
              </p>
              <Button variant="outline" size="sm" onClick={() => refetch()}>
                Try again
              </Button>
            </div>
          )}

          {!isLoading && !isError && flats.length === 0 && (
            <div className="rounded-lg border py-16 text-center">
              <p className="text-muted-foreground">No flats match your filters.</p>
              <Button variant="outline" size="sm" className="mt-4" onClick={resetFilters}>
                Clear filters
              </Button>
            </div>
          )}

          {!isLoading && !isError && flats.length > 0 && (
            <>
              <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
                {flats.map((flat) => (
                  <FlatCard key={flat.id} flat={flat} />
                ))}
              </div>
              <FlatPagination totalPages={data?.totalPages ?? 0} currentPage={filters.page ?? 0} />
            </>
          )}
        </div>
      </div>
    </section>
  )
}