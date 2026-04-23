import {createFileRoute} from '@tanstack/react-router'
import EmployeesListPage from "@/features/employees/ui/EmployeesListPage.tsx";

export const Route = createFileRoute('/employees/')({
    component: EmployeesListPage,
    loader: async ({}) => {

        const rep = await fetch("https://jsonplaceholder.typicode.com/users")
        const users = await rep.json()

        return { users }
    }
})