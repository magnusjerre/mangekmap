package components

import csstype.BackgroundColor
import csstype.BoxShadow
import csstype.BoxShadowInset
import csstype.Color
import csstype.FontWeight
import csstype.NamedColor
import csstype.px
import csstype.rgba
import mui.material.TableCell
import mui.material.TableCellProps
import mui.system.sx
import react.FC

external interface HeaderTableCellProps : TableCellProps {
    var highlightOnHover: Boolean?
    var backgroundColor: BackgroundColor?
}

val HeaderTableCell = FC<HeaderTableCellProps> { it ->
    TableCell {
        padding = it.padding
        sx {
            width = it.sx?.width
            fontWeight = FontWeight.bold
            backgroundColor = it.backgroundColor ?: NamedColor.transparent
            if (it.highlightOnHover == true) {
                hover {
                    boxShadow = BoxShadow(inset = BoxShadowInset.inset, 100.px, 100.px, rgba(255, 255, 255, 0.5))
                }
            }
        }
        +it.children
    }
}