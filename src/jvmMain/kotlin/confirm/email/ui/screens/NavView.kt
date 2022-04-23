package confirm.email.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import confirm.email.data.repository.UserRepository
import confirm.email.ui.screens.about.AboutScreen
import confirm.email.ui.screens.letter_create.LetterCreateScreen
import confirm.email.ui.screens.letter_create.LetterCreateViewModel
import confirm.email.ui.screens.letters.LettersScreen
import confirm.email.ui.screens.letters.LettersViewModel
import confirm.email.ui.screens.login.LoginView
import confirm.email.ui.screens.login.LoginViewModel
import sharing.file.ui.navigation.Navigation

@Composable
fun NavView() {
    var state by remember { mutableStateOf(Navigation.Login) }
    //var documentPath by remember { mutableStateOf<String?>(null) }
    val user by UserRepository.user.collectAsState()
    if (user.data == null && state != Navigation.Login)
        state = Navigation.Login
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            Navigation.About -> {
                AboutScreen()
            }
            Navigation.Login -> {
                LoginView(LoginViewModel()) {
                    state = Navigation.Letters
                }
            }
            Navigation.Letters -> {
                LettersScreen(LettersViewModel())
            }
            Navigation.CreateLetter -> {
                LetterCreateScreen(LetterCreateViewModel())
            }
        }
    }
}