import React from 'react'
import {Breadcrumb, Layout, theme} from 'antd'

const { Header, Content, Footer } = Layout

type Props = {
    children: React.ReactNode
}

const AppContent: React.FC<Props> = ({ children }: { children: React.ReactNode }) => {
    const {
        token: { colorBgContainer, borderRadiusLG },
    } = theme.useToken()

    return <>
        <Layout>
            <Header style={{ padding: 0, background: colorBgContainer }} />

            <Content style={{ margin: '0 16px' }}>
                <Breadcrumb style={{ margin: '16px 0' }} items={[{ title: 'User' }, { title: 'Bill' }]} />
                <div
                    style={{
                        padding: 24,
                        minHeight: 360,
                        background: colorBgContainer,
                        borderRadius: borderRadiusLG,
                    }}
                >
                    {children}
                </div>
            </Content>

            <Footer style={{ textAlign: 'center' }}>
                Ant Design ©{new Date().getFullYear()} Created by Ant UED
            </Footer>
        </Layout>
    </>
}

export default AppContent