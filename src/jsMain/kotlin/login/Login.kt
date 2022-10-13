package login

import csstype.em
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.js.jso
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
import react.useState

val Login = FC<Props> {
    var username by useState("")
    var password by useState("")
    val navigate = useNavigate()
    var failedLogin by useState(false)

    Box {

        Stack {

            spacing = 3.asDynamic()
            sx {
                width = 20.em
            }

            Typography {
                variant = TypographyVariant.h2
                component = "h2".asDynamic()
                +"Login"
            }

            Link {
                to = "/"
                +"Tilbake til forside"
            }

            TextField {
                id = "username"
                label = ReactNode("Username")
                asDynamic().InputProps = jso<InputBaseProps> {
                    onChange = {
                        username = it.target.asDynamic().value as String
                        failedLogin = false
                    }
                    value = username
                }
            }

            TextField {
                id = "password"
                label = ReactNode("Passord")
                type = InputType.password
                asDynamic().InputProps = jso<InputBaseProps> {
                    onChange = {
                        password = it.target.asDynamic().value as String
                        failedLogin = false
                    }
                    value = password
                }
            }

            if (failedLogin) {
                Alert {
                    onClose = {
                        failedLogin = false
                    }
                    severity = AlertColor.error
                    color = AlertColor.error
                    variant = AlertVariant.outlined
                    +"Feil ved login"
                }
            }

            Button {
                variant = ButtonVariant.outlined
                onClick = {
                    mainScope.launch {
                        failedLogin = false
                        val loginResult = postLogin(username, password)
                        if (loginResult) {
                            navigate("/")
                        } else {
                            failedLogin = true
                        }
                    }
                }
                +"Login"
            }
        }
    }
}

suspend fun postLogin(username: String, password: String): Boolean {
    val body = "username=$username&password=$password"
    console.log("login body", body)
    val response = window.fetch(
        "/loginprocessingurl", RequestInit(
            method = "POST",
            headers = Headers().apply {
                append("Content-Type", "application/x-www-form-urlencoded")
            },
            body = body
        )
    ).await()

    if (response.ok) {
        console.log("login response ok", response)
        return true
    } else {
        console.log("response not ok", response)
        return false
    }
}