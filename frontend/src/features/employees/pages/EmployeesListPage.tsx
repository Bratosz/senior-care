import React from 'react'
import {Route} from "@/routes/employees/index.tsx";

type Props = {
}

const EmployeesPage: React.FC<Props> = ({}) => {
    const data = Route.useLoaderData()
    console.log(data)
    return <div>Hello "/personnel/"!</div>
}

export default EmployeesPage