import {createFileRoute, notFound} from '@tanstack/react-router'

type EmployeeNotFound = {
    id: string
    message: string
}

type User = {
    id: string
    name: string
}

export const Route = createFileRoute('/employees/$id')({
    component: RouteComponent,
    errorComponent: ({error}) => {
        return <div>{String(error)}</div>
    },
    notFoundComponent: (props) => {
        const {id, message} = props.data as EmployeeNotFound
        return (<div>id: {id} message: {message} </div>)
    },
    pendingComponent: () => <div>Loading...</div>,
    loader: async ({params}) => {

        const res = await fetch(`https://jsonplaceholder.typicode.com/users/${params.id}`)

        if (!res.ok) {
            throw new Error(`Failed to fetch employee with id ${params.id}`)
        }

        const user: User = await res.json()
        console.log(user)
        if (!user) {
            throw notFound({data: {id: params.id, message: "Employee not found"}})
        }

        return user
    }
})

function RouteComponent() {
    const data = Route.useLoaderData()
    console.log(data)

    return <div>Hello employee with id {data.name}</div>
}
