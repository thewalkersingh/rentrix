import { Link, NavLink, Outlet } from "react-router-dom"
import { Toaster } from "@/components/ui/sonner"
import { Button } from "@/components/ui/button"
import { useAuthStore } from "@/features/auth/store"
import { useAuthBootstrap } from "@/features/auth/hooks/useAuthBootstrap"
import { env } from "@/lib/env"
import { useLogout } from "@/features/auth/hooks/useLogout.ts"
import { useMe } from "@/features/auth/hooks/useMe.ts"
import { ThemeToggle } from "@/components/common/ThemeToggle.tsx"
import { Home, Plus } from "lucide-react"
import { cn } from "cn"

export function RootLayout() {
  useAuthBootstrap()
  const { isAuthenticated, user } = useAuthStore()

  const logout = useLogout()
  useMe()

  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-50 border-b bg-background/80 backdrop-blur-md supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto flex h-14 items-center justify-between px-4">
          <Link to="/" className="flex items-center gap-2 transition-opacity hover:opacity-80">
            <div className="flex h-7 w-7 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <Home className="h-4 w-4" />
            </div>
            <span className="text-lg font-semibold tracking-tight">{env.VITE_APP_NAME}</span>
          </Link>

          <nav className="flex items-center gap-2">
            <NavLink
              to="/flats"
              className={({ isActive }) =>
                cn(
                  "rounded-md px-3 py-1.5 text-sm font-medium transition-colors",
                  isActive
                    ? "bg-accent text-foreground"
                    : "text-muted-foreground hover:bg-accent hover:text-foreground",
                )
              }
            >
              Flats
            </NavLink>

            {isAuthenticated && (
              <Button asChild variant="ghost" size="sm">
                <Link to="/me/reviews">My Reviews</Link>
              </Button>
            )}
            {user?.role === "ADMIN" && (
              <Button asChild variant="ghost" size="sm">
                <Link to="/admin">Admin</Link>
              </Button>
            )}
            {isAuthenticated && (
              <Button asChild variant="ghost" size="sm">
                <Link to="/me/flats">My Flats</Link>
              </Button>
            )}
            {isAuthenticated && (
              <Button asChild size="sm" className="hidden sm:inline-flex">
                <Link to="/me/flats/new">
                  <Plus className="mr-1 h-4 w-4" />
                  Add place
                </Link>
              </Button>
            )}

            {isAuthenticated && (
              <Button asChild size="icon" className="sm:hidden" aria-label="Add place">
                <Link to="/me/flats/new">
                  <Plus className="h-4 w-4" />
                </Link>
              </Button>
            )}

            <ThemeToggle />
            {isAuthenticated ? (
              <>
                <span className="text-sm text-muted-foreground">{user?.name}</span>
                <Button variant="outline" size="sm" onClick={() => logout.mutate()}>
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button asChild variant="ghost" size="sm">
                  <Link to="/login">Login</Link>
                </Button>
                <Button asChild size="sm">
                  <Link to="/signup">Sign up</Link>
                </Button>
              </>
            )}
          </nav>
        </div>
      </header>

      <main className="flex-1">
        <Outlet />
      </main>

      <footer className="border-t">
        <div className="container mx-auto grid gap-6 px-4 py-10 sm:grid-cols-2 lg:grid-cols-4">
          <div className="space-y-2">
            <p className="text-lg font-semibold">{env.VITE_APP_NAME}</p>
            <p className="text-sm text-muted-foreground">
              The story and history of a place before you move in.
            </p>
          </div>
          <div className="space-y-2">
            <p className="text-sm font-medium">Product</p>
            <ul className="space-y-1 text-sm text-muted-foreground">
              <li>
                <Link to="/flats" className="hover:text-foreground">
                  Browse flats
                </Link>
              </li>
              <li>
                <Link to="/signup" className="hover:text-foreground">
                  Write a review
                </Link>
              </li>
            </ul>
          </div>
          <div className="space-y-2">
            <p className="text-sm font-medium">Account</p>
            <ul className="space-y-1 text-sm text-muted-foreground">
              <li>
                <Link to="/login" className="hover:text-foreground">
                  Login
                </Link>
              </li>
              <li>
                <Link to="/signup" className="hover:text-foreground">
                  Sign up
                </Link>
              </li>
            </ul>
          </div>
          <div className="space-y-2">
            <p className="text-sm font-medium">Legal</p>
            <ul className="space-y-1 text-sm text-muted-foreground">
              <li>
                <span className="cursor-not-allowed">Privacy</span>
              </li>
              <li>
                <span className="cursor-not-allowed">Terms</span>
              </li>
            </ul>
          </div>
        </div>
        <div className="border-t">
          <p className="container mx-auto px-4 py-4 text-center text-xs text-muted-foreground">
            © {new Date().getFullYear()} {env.VITE_APP_NAME}. All rights reserved.
            <br />
            Built with ❤️ in India by DoorWayLivings
          </p>
        </div>
      </footer>

      <Toaster richColors position="top-right" />
    </div>
  )
}