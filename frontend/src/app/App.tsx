import RouterProvider from "@/app/providers/RouterProvider.tsx";
import QueryProvider from "@/app/providers/QueryProvider.tsx";

const App = () =>
    <QueryProvider>
        <RouterProvider/>
    </QueryProvider>

export default App
