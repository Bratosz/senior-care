import React from 'react'
import {Link, type LinkProps} from '@tanstack/react-router'

type Props = {
    to: LinkProps['to']
    text: string
    icon?: React.ReactNode
}

const MenuLink: React.FC<Props> = ({to, text, icon}) => {
    return <>
        <Link
            to={to}
            style={{ fontWeight: '300'}}
        >
            {icon}<span>{text}</span>
        </Link>
    </>
}

export default MenuLink