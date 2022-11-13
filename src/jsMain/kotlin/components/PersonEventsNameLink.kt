package components

import react.FC
import react.Props
import react.router.dom.Link

external interface PersonEventsNameLinkProps : Props{
    var id: Long
    var name: String
}

val PersonEventsNameLink = FC<PersonEventsNameLinkProps> {props ->
    Link {
        to = "/persons/${props.id}/participations"
        +props.name
    }
}