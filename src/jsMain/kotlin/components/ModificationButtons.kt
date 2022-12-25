package components

import AppAlertProps
import csstype.Display
import csstype.FlexWrap
import csstype.Length
import csstype.em
import csstype.number
import kotlinx.coroutines.launch
import mainScope
import mui.material.Button
import mui.material.ButtonColor
import mui.material.ButtonVariant
import mui.material.Stack
import mui.material.StackDirection
import mui.system.Box
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.router.useNavigate
import react.useState

class ButtonTexts(
    val saveButtonText: String = "Lagre",
    val cancelButtonText: String = "Avbryt",
    val deleteButtonText: String = "Slett",
    val deleteYesButtonText: String = "Ja, slett!",
    val deleteNoButtonText: String = "Nei"
)

class OnResult(val redirectPath: String, val alertProps: AppAlertProps?) {
    constructor(redirectPath: String, successMessage: String) : this(
        redirectPath,
        AppAlertProps.success(successMessage)
    )
}

interface OnHandler {
    suspend fun handle(): OnResult
}

external interface ModificationButtonsProps : Props {
    var onDelete: OnHandler?
    var deleteRedirectUri: String?
    var onSave: OnHandler
    var cancelRedirectUri: String
    var buttonTexts: ButtonTexts?
    var handleAlert: ((AppAlertProps) -> Unit)?
}

val ModificationButtons = FC<ModificationButtonsProps> { props ->
    var buttonsDisabled by useState(false)
    var showDeleteWarning by useState(false)
    val navigate = useNavigate()
    val buttonTexts =
        if (props.buttonTexts == null || props.buttonTexts == undefined) ButtonTexts() else props.buttonTexts!!
    if (showDeleteWarning) {
        Stack {
            sx {
                marginTop = 1.em
            }
            direction = responsive(StackDirection.row)
            spacing = responsive(2)
            Button {
                variant = ButtonVariant.outlined
                onClick = {
                    mainScope.launch {
                        buttonsDisabled = true
                        val result = props.onDelete!!.handle()
                        if (!(props.handleAlert == null || props.handleAlert == undefined) && result.alertProps != null) {
                            props.handleAlert!!(result.alertProps)
                        }
                        buttonsDisabled = false
                        navigate(result.redirectPath)
                    }
                }
                +buttonTexts.deleteYesButtonText
            }
            Button {
                variant = ButtonVariant.outlined
                onClick = {
                    showDeleteWarning = false
                }
                +buttonTexts.deleteNoButtonText
            }
        }
        return@FC
    }

    Box {
        sx {
            display = Display.flex
            flexWrap = FlexWrap.wrap
            maxWidth = 25.em
            width = "100%".unsafeCast<Length>()
            marginTop = 1.em
        }

        Button {
            sx {
                flexGrow = number(1.0)
            }
            disabled = buttonsDisabled
            variant = ButtonVariant.contained
            onClick = {
                mainScope.launch {
                    buttonsDisabled = true
                    val result = props.onSave.handle()
                    if (!(props.handleAlert == null || props.handleAlert == undefined) && result.alertProps != null) {
                        props.handleAlert!!(result.alertProps)
                    }
                    buttonsDisabled = false
                    navigate(result.redirectPath)
                }
            }
            +buttonTexts.saveButtonText
        }
        Button {
            sx {
                marginLeft = 1.em
                flexGrow = number(1.0)
            }
            variant = ButtonVariant.outlined
            disabled = buttonsDisabled
            onClick = {
                navigate(props.cancelRedirectUri)
            }
            +buttonTexts.cancelButtonText
        }
        if (props.onDelete != null) {
            Button {
                sx {
                    marginTop = 1.em
                }
                color = ButtonColor.warning
                variant = ButtonVariant.text
                onClick = {
                    showDeleteWarning = true
                }
                +buttonTexts.deleteButtonText
            }
        }
    }
}