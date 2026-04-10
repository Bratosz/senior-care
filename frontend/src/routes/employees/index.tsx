import {createFileRoute} from '@tanstack/react-router'
import EmployeesPage from "@/routes/employees/-pages/EmployeesPage.tsx";

export const Route = createFileRoute('/employees/')({
    component: EmployeesPage,
    loader: async ({}) => {

        const rep = await fetch("https://jsonplaceholder.typicode.com/users")
        const users = await rep.json()

        return { users }
    }
})