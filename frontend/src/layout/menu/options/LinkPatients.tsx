import React from 'react'
import {SolutionOutlined} from '@ant-design/icons';
import MenuLink from '@/app/layout/components/MenuLink.tsx'

const LinkPatients: React.FC = () => {
    return <>
        <MenuLink
            to='/patient'
            text='Pacjenci'
            icon={<SolutionOutlined/>}
        />
    </>
}

export default LinkPatients