import {createRootRoute} from '@tanstack/react-router'
import AppLayout from '@/app/layout/AppLayout.tsx'
import AppNotFound from "@/app/layout/error/AppNotFound.tsx";

export const Route = createRootRoute({
  component: AppLayout,
  notFoundComponent: AppNotFound
})
