package authentication

import ApiAuthentication
import kotlinx.browser.window
import kotlinx.coroutines.await

// Hacky-wacky løsning frem til ordentlig autentisering er på plass
suspend fun getIsAuthenticated(): Boolean = window.fetch(ApiAuthentication.BASE_PATH).await().ok