import React from 'react'
import {TeamOutlined} from '@ant-design/icons';
import MenuLink from '@/app/layout/components/MenuLink.tsx'

const LinkPatients: React.FC = () => {
    return <>
        <MenuLink
            to='/personnel'
            text='Personel'
            icon={<TeamOutlined/>}
        />
    </>
}

export default LinkPatients