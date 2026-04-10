import {createFileRoute} from '@tanstack/react-router'
import PatientPage from "@/routes/patients/-pages/PatientPage.tsx"

export const Route = createFileRoute('/patients/')({
    component: PatientPage,
})
