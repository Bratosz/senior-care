import {createRootRoute} from '@tanstack/react-router'
import AppLayout from '@/layout/AppLayout.tsx'
import AppNotFound from "@/layout/error/AppNotFound.tsx";

export const Route = createRootRoute({
  component: AppLayout,
  notFoundComponent: AppNotFound
})
