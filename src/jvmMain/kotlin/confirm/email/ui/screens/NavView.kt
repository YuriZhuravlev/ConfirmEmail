package confirm.email.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import confirm.email.data.repository.UserRepository
import confirm.email.ui.screens.about.AboutScreen
import confirm.email.ui.screens.letter_create.LetterCreateScreen
import confirm.email.ui.screens.letters.LettersScreen
import confirm.email.ui.screens.login.LoginView
import org.koin.java.KoinJavaComponent.getKoin
import sharing.file.ui.navigation.Navigation

val statesWithoutAuth = arrayOf(Navigation.Login, Navigation.About)

@Composable
fun NavView() {
    var state by remember { mutableStateOf(Navigation.Login) }
    val user by getKoin().get<UserRepository>().user.collectAsState()
    if (user.data == null && state !in statesWithoutAuth)
        state = Navigation.Login
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            Navigation.About -> {
                AboutScreen {
                    state = Navigation.Login
                }
            }
            Navigation.Login -> {
                LoginView(getKoin().get(), toAbout = {
                    state = Navigation.About
                }) {
                    state = Navigation.Letters
                }
            }
            Navigation.Letters -> {
                LettersScreen(getKoin().get()) {
                    state = Navigation.CreateLetter
                }
            }
            Navigation.CreateLetter -> {
                LetterCreateScreen(getKoin().get()) {
                    state = Navigation.Letters
                }
            }
        }
    }
}