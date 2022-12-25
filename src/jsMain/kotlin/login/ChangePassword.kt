package login

import ApiAdmin
import authentication.getIsAuthenticated
import csstype.em
import dto.ChangePasswordDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.js.jso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mainScope
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertVariant
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.InputBaseProps
import mui.material.Stack
import mui.material.TextField
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.Box
import mui.system.sx
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.InputType
import react.router.dom.Link
import react.router.useNavigate
import react.useEffectOnce
import react.useState

val ChangePassword = FC<Props> {
    var oldPassword by useState("")
    var newPassword by useState("")
    var isAuthenticated by useState(false)
    var failedPasswordChange by useState(false)
    var successfulPasswordChange by useState(false)

    useEffectOnce {
        mainScope.launch {
            isAuthenticated = getIsAuthenticated()
        }
    }

    Box {

        if (!isAuthenticated) {
            Alert {
                variant = AlertVariant.outlined
                color = AlertColor.error
                +"Må være logget inn for å kunne endre passord"
            }
            return@Box
        }

        Stack {

            spacing = 3.asDynamic()
            sx {
                width = 20.em
            }

            Typography {
                variant = TypographyVariant.h2
                component = "h2".asDynamic()
                +"Endre password"
            }

            Link {
                to = "/"
                +"Tilbake til forside"
            }

            TextField {
                id = "password"
                label = ReactNode("Gammelt passord")
                type = InputType.password
                asDynamic().InputProps = jso<InputBaseProps> {
                    onChange = {
                        oldPassword = it.target.asDynamic().value as String
                        failedPasswordChange = false
                    }
                    value = oldPassword
                }
            }

            TextField {
                id = "newPassword"
                label = ReactNode("Nytt passord")
                type = InputType.password
                asDynamic().InputProps = jso<InputBaseProps> {
                    onChange = {
                        newPassword = it.target.asDynamic().value as String
                        failedPasswordChange = false
                    }
                    value = newPassword
                }
            }

            if (failedPasswordChange) {
                Alert {
                    onClose = {
                        failedPasswordChange = false
                    }
                    severity = AlertColor.error
                    color = AlertColor.error
                    variant = AlertVariant.outlined
                    +"Feil ved endring av passord"
                }
            }

            if (successfulPasswordChange) {
                Alert {
                    onClose = {
                        successfulPasswordChange = false
                    }
                    severity = AlertColor.success
                    color = AlertColor.success
                    variant = AlertVariant.outlined
                    +"Passor dendret"
                }
            }

            Button {
                variant = ButtonVariant.outlined
                onClick = {
                    mainScope.launch {
                        failedPasswordChange = false
                        val loginResult = putChangePassword(oldPassword, newPassword)
                        if (loginResult) {
                            successfulPasswordChange = true
                        } else {
                            failedPasswordChange = true
                        }
                    }
                }
                +"Endre password"
            }
        }
    }
}

suspend fun putChangePassword(oldPassword: String, newPassword: String): Boolean {
    val body = ChangePasswordDto(oldPassword, newPassword)
    val response = window.fetch(
        ApiAdmin.BASE_PATH, RequestInit(
            method = "PUT",
            headers = Headers().apply {
                append("Content-Type", "application/json")
            },
            body = Json.encodeToString(body)
        )
    ).await()

    return if (response.ok) {
        console.log("change password response ok", response)
        true
    } else {
        console.log("change password response not ok", response)
        false
    }
}