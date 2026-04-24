import { createRouter, RouterProvider as TanstackRouterProvider } from '@tanstack/react-router'
import { routeTree} from "@/routeTree.gen.ts";

const router  = createRouter({routeTree})

declare module '@tanstack/react-router' {
    interface Register {
        router: typeof router
    }
}

const RouterProvider = () => <TanstackRouterProvider router={router}/>

export default RouterProvider