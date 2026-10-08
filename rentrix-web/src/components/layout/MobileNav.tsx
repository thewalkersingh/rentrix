import { useState } from "react"
import { Link, NavLink } from "react-router-dom"
import { Building2, Home, Menu, Plus, Star, User } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet"
import { Separator } from "@/components/ui/separator"
import { cn } from "@/lib/utils"
import { useAuthStore } from "@/features/auth/store"
import { useLogout } from "@/features/auth/hooks/useLogout"
import { env } from "@/lib/env"

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  cn(
    "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
    isActive
      ? "bg-accent text-foreground"
      : "text-muted-foreground hover:bg-accent hover:text-foreground",
  )

export function MobileNav() {
  const [open, setOpen] = useState(false)
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)
  const user = useAuthStore((s) => s.user)
  const logout = useLogout()

  const close = () => setOpen(false)

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="ghost" size="icon" className="md:hidden" aria-label="Open menu">
          <Menu className="h-5 w-5" />
        </Button>
      </SheetTrigger>

      <SheetContent side="right" className="w-72 p-0">
        <SheetHeader className="border-b px-4 py-4">
          <SheetTitle className="flex items-center gap-2 text-left">
            <div className="flex h-7 w-7 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <Home className="h-4 w-4" />
            </div>
            {env.VITE_APP_NAME}
          </SheetTitle>
        </SheetHeader>

        <div className="flex flex-col gap-1 p-3">
          <NavLink to="/flats" className={navLinkClass} onClick={close}>
            <Building2 className="h-4 w-4" />
            Browse flats
          </NavLink>

          {isAuthenticated && (
            <>
              <NavLink to="/me/flats" className={navLinkClass} onClick={close}>
                <User className="h-4 w-4" />
                My Flats
              </NavLink>

              <NavLink to="/me/flats/new" className={navLinkClass} onClick={close}>
                <Plus className="h-4 w-4" />
                Add a place
              </NavLink>

              <NavLink to="/me/reviews" className={navLinkClass} onClick={close}>
                <Star className="h-4 w-4" />
                My Reviews
              </NavLink>
            </>
          )}

          {user?.role === "ADMIN" && (
            <NavLink to="/admin" className={navLinkClass} onClick={close}>
              Admin
            </NavLink>
          )}

          <Separator className="my-2" />

          {isAuthenticated ? (
            <Button
              variant="outline"
              className="justify-start"
              onClick={() => {
                close()
                logout.mutate()
              }}
            >
              Log out
            </Button>
          ) : (
            <div className="flex flex-col gap-2">
              <Button asChild variant="outline" onClick={close}>
                <Link to="/login">Login</Link>
              </Button>
              <Button asChild onClick={close}>
                <Link to="/signup">Sign up</Link>
              </Button>
            </div>
          )}
        </div>
      </SheetContent>
    </Sheet>
  )
}