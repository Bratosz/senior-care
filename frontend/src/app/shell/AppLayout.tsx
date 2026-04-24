import React, {useState} from 'react'
import {Layout} from 'antd'
import {Outlet} from '@tanstack/react-router'
import Menu from '../navigation/Menu.tsx'
import AppContent from "@/app/content/AppContent.tsx";

const {Sider} = Layout

const AppLayout: React.FC = () => {
    const [collapsed, setCollapsed] = useState(false)

    return <>
        <Layout style={{minHeight: '100vh'}}>
            <Sider collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
                <Menu/>
            </Sider>
            <AppContent>
                <Outlet/>
            </AppContent>
        </Layout>
    </>
}

export default AppLayout