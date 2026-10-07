import { Link } from "react-router-dom"
import {
  ArrowRight,
  Building2,
  CheckCircle2,
  MapPin,
  Plus,
  Search,
  ShieldCheck,
  Star,
  Users,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { env } from "@/lib/env"

const features = [
  {
    icon: Search,
    title: "Search before you sign",
    description:
      "Look up any address and see verified tenant reviews before you commit to a lease.",
  },
  {
    icon: ShieldCheck,
    title: "Moderated reviews",
    description:
      "Every review goes through moderation so you get honest signal, not spam or grudges.",
  },
  {
    icon: Star,
    title: "Rate on what matters",
    description:
      "Rent, area, landlord behaviour, maintenance, water supply — score it all on a 1–10 scale.",
  },
  {
    icon: Users,
    title: "Built by tenants",
    description:
      "A community-driven record of rental history. What you share helps the next renter.",
  },
]

const stats = [
  { label: "Flats rated", value: "2,400+" },
  { label: "Cities covered", value: "18" },
  { label: "Verified reviews", value: "9,800+" },
  { label: "Happy tenants", value: "6,200+" },
]

const testimonials = [
  {
    name: "Ananya R.",
    role: "Tenant · Bangalore",
    rating: 5,
    quote:
      "I avoided a flat with chronic water issues because of a review here. Worth every second.",
  },
  {
    name: "Rohit M.",
    role: "Tenant · Mumbai",
    rating: 5,
    quote: "Finally a place where reviews are actually moderated. No fake 10/10s from brokers.",
  },
  {
    name: "Sneha K.",
    role: "Tenant · Hyderabad",
    rating: 4,
    quote: "The rating breakdown helped me compare two flats side by side. Made the decision easy.",
  },
]

export default function HomePage() {
  return (
    <div className="flex flex-col">
      {/* ── HERO ─────────────────────────────────────────── */}
      <section className="relative overflow-hidden border-b">
        {/* backdrop */}
        <div
          aria-hidden
          className="bg-[radial-gradient(ellipse_at_top,var(--color-primary),transparent_60%)]/[10] pointer-events-none absolute inset-0 -z-10"
        />

        <div className="container mx-auto flex flex-col items-center gap-8 px-4 py-24 text-center sm:py-32">
          <Badge variant="secondary" className="gap-1">
            <span className="relative flex h-2 w-2">
              <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-primary opacity-75" />
              <span className="relative inline-flex h-2 w-2 rounded-full bg-primary" />
            </span>
            Now live in 18 cities
          </Badge>

          <h1 className="max-w-3xl text-4xl font-bold tracking-tight text-balance sm:text-5xl md:text-6xl">
            Know the place <span className="text-primary">before</span> you move in.
          </h1>

          <p className="max-w-2xl text-lg text-balance text-muted-foreground">
            {env.VITE_APP_NAME} is the honest story of every rental — real ratings from real tenants
            on rent, area, and landlords. No brokers. No guesswork.
          </p>

          <div className="flex flex-col gap-3 sm:flex-row">
            <Button asChild size="lg">
              <Link to="/flats">
                Browse flats
                <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
            </Button>
            <Button asChild size="lg" variant="outline">
              <Link to="/signup">Write your first review</Link>
            </Button>
            <Button asChild size="lg" variant="outline">
              <Link to="/me/flats/new">Add a place you've lived in</Link>
            </Button>
          </div>

          {/* quick search teaser */}
          <div className="mt-6 flex w-full max-w-xl items-center gap-2 rounded-full border bg-background/60 px-4 py-2 text-sm text-muted-foreground backdrop-blur">
            <MapPin className="h-4 w-4" />
            <span>Try “Bangalore”, “HSR Layout”, or “Andheri West”…</span>
          </div>
        </div>
      </section>

      {/* ── STATS ────────────────────────────────────────── */}
      <section className="border-b bg-muted/30">
        <div className="container mx-auto grid grid-cols-2 gap-6 px-4 py-10 sm:grid-cols-4">
          {stats.map((s) => (
            <div key={s.label} className="text-center">
              <p className="text-2xl font-bold sm:text-3xl">{s.value}</p>
              <p className="text-xs tracking-wide text-muted-foreground uppercase sm:text-sm">
                {s.label}
              </p>
            </div>
          ))}
        </div>
      </section>

      {/* ── FEATURES ─────────────────────────────────────── */}
      <section className="container mx-auto px-4 py-20">
        <div className="mx-auto mb-12 max-w-2xl text-center">
          <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
            Renting, without the surprises
          </h2>
          <p className="mt-3 text-muted-foreground">
            Everything you wish you'd known before signing the last lease.
          </p>
        </div>

        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {features.map(({ icon: Icon, title, description }) => (
            <Card key={title} className="border-muted/60">
              <CardContent className="space-y-3 pt-6">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <Icon className="h-5 w-5" />
                </div>
                <h3 className="leading-tight font-semibold">{title}</h3>
                <p className="text-sm text-muted-foreground">{description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      {/* ── HOW IT WORKS ─────────────────────────────────── */}
      <section className="border-y bg-muted/20">
        <div className="container mx-auto px-4 py-20">
          <div className="mx-auto mb-12 max-w-2xl text-center">
            <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">How it works</h2>
          </div>

          <div className="grid gap-8 md:grid-cols-3">
            {[
              {
                step: "01",
                title: "Search a flat",
                description: "Browse by city, rent, rooms, and availability.",
              },
              {
                step: "02",
                title: "Read real reviews",
                description: "See what previous tenants actually experienced.",
              },
              {
                step: "03",
                title: "Share your own",
                description: "Rate 1–10 and help the next renter decide.",
              },
            ].map(({ step, title, description }) => (
              <div key={step} className="relative space-y-3">
                <span className="text-5xl font-bold text-primary/20">{step}</span>
                <h3 className="text-lg font-semibold">{title}</h3>
                <p className="text-sm text-muted-foreground">{description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
      {/* ── ADD A PLACE CTA ──────────────────────────────── */}
      <section className="container mx-auto px-4 py-16">
        <Card className="border-primary/20 bg-linear-to-br from-primary/5 via-transparent to-transparent">
          <CardContent className="flex flex-col items-start gap-6 p-8 sm:flex-row sm:items-center sm:justify-between">
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <Plus className="h-5 w-5 text-primary" />
                <h3 className="text-xl font-bold sm:text-2xl">Lived somewhere? Add it.</h3>
              </div>
              <p className="max-w-2xl text-sm text-muted-foreground sm:text-base">
                Help the next renter. Add a flat you've lived in and share your honest review — even
                if no landlord has listed it yet.
              </p>
            </div>
            <Button asChild size="lg" className="shrink-0">
              <Link to="/me/flats/new">
                Add a place
                <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
            </Button>
          </CardContent>
        </Card>
      </section>
      {/* ── TESTIMONIALS ─────────────────────────────────── */}
      <section className="container mx-auto px-4 py-20">
        <div className="mx-auto mb-12 max-w-2xl text-center">
          <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">Tenants are talking</h2>
        </div>

        <div className="grid gap-6 md:grid-cols-3">
          {testimonials.map((t) => (
            <Card key={t.name}>
              <CardContent className="space-y-4 pt-6">
                <div className="flex gap-0.5">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Star
                      key={i}
                      className={
                        i < t.rating
                          ? "h-4 w-4 fill-yellow-400 text-yellow-400"
                          : "h-4 w-4 text-muted-foreground"
                      }
                    />
                  ))}
                </div>
                <p className="text-sm text-muted-foreground">“{t.quote}”</p>
                <div className="border-t pt-3">
                  <p className="text-sm font-medium">{t.name}</p>
                  <p className="text-xs text-muted-foreground">{t.role}</p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      {/* ── CTA ──────────────────────────────────────────── */}
      <section className="border-t bg-gradient-to-b from-primary/5 to-transparent">
        <div className="container mx-auto flex flex-col items-center gap-6 px-4 py-20 text-center">
          <Building2 className="h-10 w-10 text-primary" />
          <h2 className="max-w-2xl text-3xl font-bold tracking-tight text-balance sm:text-4xl">
            Your next flat deserves a review.
          </h2>
          <p className="max-w-xl text-muted-foreground">
            Join {env.VITE_APP_NAME} and help build the rental history every tenant wishes they had.
          </p>
          <div className="flex flex-col gap-3 sm:flex-row">
            <Button asChild size="lg">
              <Link to="/signup">
                Get started — it's free
                <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
            </Button>
            <Button asChild size="lg" variant="ghost">
              <Link to="/flats">Browse flats first</Link>
            </Button>
          </div>

          <div className="mt-4 flex flex-wrap items-center justify-center gap-4 text-xs text-muted-foreground">
            <span className="flex items-center gap-1">
              <CheckCircle2 className="h-3 w-3" /> Free forever
            </span>
            <span className="flex items-center gap-1">
              <CheckCircle2 className="h-3 w-3" /> Moderated reviews
            </span>
            <span className="flex items-center gap-1">
              <CheckCircle2 className="h-3 w-3" /> No broker spam
            </span>
          </div>
        </div>
      </section>
    </div>
  )
}