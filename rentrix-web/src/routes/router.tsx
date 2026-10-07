import { createBrowserRouter, Navigate } from "react-router-dom"
import { RootLayout } from "@/components/layout/RootLayout"
import { ProtectedRoute } from "@/components/common/ProtectedRoute"
import { RoleGate } from "@/components/common/RoleGate"

import HomePage from "@/pages/HomePage"
import FlatsPage from "@/pages/FlatsPage"
import FlatDetailPage from "@/pages/FlatDetailPage"
import LoginPage from "@/pages/LoginPage"
import SignupPage from "@/pages/SignupPage"
import MyReviewsPage from "@/pages/MyReviewsPage"
import AdminPage from "@/pages/AdminPage"
import NotFoundPage from "@/pages/NotFoundPage"
import { RouteError } from "@/routes/RouteError.tsx"
import MyFlatsPage from "@/pages/MyFlatsPage.tsx"
import CreateFlatPage from "@/pages/CreateFlatPage.tsx";
import EditFlatPage from "@/pages/EditFlatPage.tsx";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    errorElement: <RouteError />,
    children: [
      { index: true, element: <HomePage /> },
      { path: "flats", element: <FlatsPage /> },
      { path: "flats/:id", element: <FlatDetailPage /> },
      { path: "login", element: <LoginPage /> },
      { path: "signup", element: <SignupPage /> },

      // Authenticated routes
      {
        element: <ProtectedRoute />,
        children: [{ path: "me/reviews", element: <MyReviewsPage /> }],
      },

      // Admin-only routes
      {
        element: <ProtectedRoute />,
        children: [
          {
            element: <RoleGate allow={["ADMIN"]} />,
            children: [{ path: "admin", element: <AdminPage /> }],
          },
        ],
      },
      // Me Flats
      {
        element: <ProtectedRoute />,
        children: [
          { path: "me/reviews", element: <MyReviewsPage /> },
          { path: "me/flats", element: <MyFlatsPage /> },
          { path: "me/flats", element: <MyFlatsPage /> },
          { path: "me/flats/new", element: <CreateFlatPage /> },
          { path: "me/flats/:id/edit", element: <EditFlatPage /> },
        ],
      },
      { path: "404", element: <NotFoundPage /> },
      { path: "*", element: <Navigate to="/404" replace /> },
    ],
  },
])