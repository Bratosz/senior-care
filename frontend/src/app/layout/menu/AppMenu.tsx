import React from 'react'
import {Menu, type MenuProps} from 'antd'
import {FileOutlined, SolutionOutlined, TeamOutlined, UserOutlined,} from '@ant-design/icons'
import {type LinkProps, useNavigate} from "@tanstack/react-router"

type MenuItem = Required<MenuProps>['items'][number]

const getRouteItem = (
    label: React.ReactNode,
    key: LinkProps['to'],
    icon?: React.ReactNode,
    children?: MenuItem[],
): MenuItem => {
    return { key, icon, children, label} as MenuItem;
}

const getPlainItem = (
    label: React.ReactNode,
    key: React.Key,
    icon?: React.ReactNode,
    children?: MenuItem[],
): MenuItem => {
    return { key, icon, children, label} as MenuItem;
}

const items: MenuItem[] = [
    getRouteItem('Pacjenci', '/patients', <SolutionOutlined/>),
    getRouteItem('Personel', '/employees', <TeamOutlined/>),
    getPlainItem('User', 'sub1', <UserOutlined/>, [
        getRouteItem('Tom', '3'),
        getRouteItem('Bill', '4'),
        getRouteItem('Alex', '5'),
    ]),
    getRouteItem('Team', 'sub2', <TeamOutlined/>, [
        getRouteItem('Team 1', '6'),
        getRouteItem('Team 2', '8')
    ]),
    getRouteItem('Files', '9', <FileOutlined/>),
];

const AppMenu: React.FC = () => {
    const navigate = useNavigate()

    return <>
        <Menu
            theme='dark'
            mode='inline'
            items={items}
            onClick={(e) => {
                if (e.key.startsWith('/')) {
                    navigate({to: e.key})
                }
            }}
        />
    </>
}

export default AppMenu