import React from 'react'
import {SolutionOutlined} from '@ant-design/icons';
import MenuLink from '@/app/layout/menu/MenuLink.tsx'

const LinkPatients: React.FC = () => {
    return <>
        <MenuLink
            to='/patients'
            text='Pacjenci'
            icon={<SolutionOutlined/>}
        />
    </>
}

export default LinkPatients