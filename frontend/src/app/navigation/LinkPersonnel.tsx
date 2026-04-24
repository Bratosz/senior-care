import React from 'react'
import {TeamOutlined} from '@ant-design/icons';
import MenuLink from '@/app/navigation/MenuLink.tsx'

const LinkPatients: React.FC = () => {
    return <>
        <MenuLink
            to='/employees'
            text='Personel'
            icon={<TeamOutlined/>}
        />
    </>
}

export default LinkPatients