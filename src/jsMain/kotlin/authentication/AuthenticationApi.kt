package authentication

import kotlinx.browser.window
import kotlinx.coroutines.await

// Hacky-wacky løsning frem til ordentlig autentisering er på plass
suspend fun getIsAuthenticated(): Boolean = !window.fetch("/check_authentication").await().url.endsWith("/login")