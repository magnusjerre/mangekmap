package components

import csstype.Length
import csstype.em
import mui.material.Box
import mui.material.BoxProps
import mui.material.Paper
import mui.system.sx
import react.FC
import tableBoxPadding


val TableBox = FC<BoxProps> {
    Box {
        component = Paper
        sx {
            marginLeft = it.sx?.marginLeft
            marginTop = it.sx?.marginTop ?: 2.em
            marginRight = it.sx?.marginRight ?: 2.em
            padding = tableBoxPadding
            width = Length.fitContent
        }
       +it.children
    }
}