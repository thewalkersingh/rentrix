import { Link } from "react-router-dom"
import { LayoutDashboard, LogOut, Plus, Shield, Star } from "lucide-react"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { useAuthStore } from "@/features/auth/store"
import { useLogout } from "@/features/auth/hooks/useLogout"

function initials(name?: string) {
  if (!name) return "?"
  return name
    .split(" ")
    .map((p) => p[0])
    .filter(Boolean)
    .slice(0, 2)
    .join("")
    .toUpperCase()
}

export function UserMenu() {
  const user = useAuthStore((s) => s.user)
  const logout = useLogout()

  if (!user) return null

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="rounded-full" aria-label="Account menu">
          <Avatar className="h-8 w-8">
            <AvatarFallback className="bg-primary/10 text-xs font-semibold text-primary">
              {initials(user.name)}
            </AvatarFallback>
          </Avatar>
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuLabel>
          <div className="space-y-0.5">
            <p className="text-sm leading-tight font-medium">{user.name || user.email}</p>
            <p className="text-xs font-normal text-muted-foreground">{user.email}</p>
          </div>
        </DropdownMenuLabel>

        <DropdownMenuSeparator />

        <DropdownMenuItem asChild>
          <Link to="/me/flats" className="cursor-pointer">
            <LayoutDashboard className="mr-2 h-4 w-4" />
            My Flats
          </Link>
        </DropdownMenuItem>

        <DropdownMenuItem asChild>
          <Link to="/me/flats/new" className="cursor-pointer">
            <Plus className="mr-2 h-4 w-4" />
            Add a place
          </Link>
        </DropdownMenuItem>

        <DropdownMenuItem asChild>
          <Link to="/me/reviews" className="cursor-pointer">
            <Star className="mr-2 h-4 w-4" />
            My Reviews
          </Link>
        </DropdownMenuItem>

        {user.role === "ADMIN" && (
          <>
            <DropdownMenuSeparator />
            <DropdownMenuItem asChild>
              <Link to="/admin" className="cursor-pointer">
                <Shield className="mr-2 h-4 w-4" />
                Admin
              </Link>
            </DropdownMenuItem>
          </>
        )}

        <DropdownMenuSeparator />

        <DropdownMenuItem
          onClick={() => logout.mutate()}
          className="cursor-pointer text-destructive focus:text-destructive"
        >
          <LogOut className="mr-2 h-4 w-4" />
          Log out
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}