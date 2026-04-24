import {createRootRoute} from '@tanstack/react-router'
import AppLayout from '@/app/shell/AppLayout.tsx'
import AppNotFound from "@/app/error/AppNotFound.tsx";

export const Route = createRootRoute({
  component: AppLayout,
  notFoundComponent: AppNotFound
})
