import {createFileRoute} from '@tanstack/react-router'
import PatientsListPage from "@/features/patients/pages/PatientsListPage.tsx"

export const Route = createFileRoute('/patients/')({
    component: PatientsListPage,
})
