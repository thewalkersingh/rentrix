import { Link, NavLink, Outlet } from "react-router-dom"
import { Toaster } from "@/components/ui/sonner"
import { Button } from "@/components/ui/button"
import { useAuthStore } from "@/features/auth/store"
import { useAuthBootstrap } from "@/features/auth/hooks/useAuthBootstrap"
import { env } from "@/lib/env"
import { useMe } from "@/features/auth/hooks/useMe.ts"
import { Home, Plus } from "lucide-react"
import { cn } from "cn"
import { UserMenu } from "@/components/layout/UserMenu.tsx"
import {ThemeToggle} from "@/components/common/ThemeToggle.tsx";
import {MobileNav} from "@/components/layout/MobileNav.tsx";

export function RootLayout() {
  const { isAuthenticated } = useAuthStore()
  useAuthBootstrap()
  useMe()

  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-50 border-b bg-background/80 backdrop-blur-md supports-backdrop-filter:bg-background/60">
        <div className="container mx-auto flex h-14 items-center justify-between px-4">
          <Link to="/" className="flex items-center gap-2 transition-opacity hover:opacity-80">
            <div className="flex h-7 w-7 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <Home className="h-4 w-4" />
            </div>
            <span className="text-lg font-semibold tracking-tight">{env.VITE_APP_NAME}</span>
          </Link>

          <nav className="flex items-center gap-2">
            {/* Desktop-only nav */}
            <NavLink
              to="/flats"
              className={({ isActive }) =>
                cn(
                  "hidden rounded-md px-3 py-1.5 text-sm font-medium transition-colors md:inline-flex",
                  isActive
                    ? "bg-accent text-foreground"
                    : "text-muted-foreground hover:bg-accent hover:text-foreground",
                )
              }
            >
              Flats
            </NavLink>

            {/* Add place — desktop */}
            {isAuthenticated && (
              <Button asChild size="sm" className="hidden md:inline-flex">
                <Link to="/me/flats/new">
                  <Plus className="mr-1 h-4 w-4" />
                  Add place
                </Link>
              </Button>
            )}

            {/* Add place — mobile icon-only */}
            {isAuthenticated && (
              <Button asChild size="icon" className="md:hidden" aria-label="Add place">
                <Link to="/me/flats/new">
                  <Plus className="h-4 w-4" />
                </Link>
              </Button>
            )}

            <ThemeToggle />

            {/* User menu — desktop only */}
            <div className="hidden md:flex md:items-center md:gap-2">
              {isAuthenticated ? (
                <UserMenu />
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
            </div>

            {/* Mobile hamburger */}
            <MobileNav />
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
                <Link to="/privacy" className="hover:text-foreground">
                  Privacy Policy
                </Link>
              </li>
              <li>
                <Link to="/terms" className="hover:text-foreground">
                  Terms of Service
                </Link>
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