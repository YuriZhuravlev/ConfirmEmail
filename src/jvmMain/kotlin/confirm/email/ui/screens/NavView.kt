package confirm.email.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import confirm.email.data.repository.UserRepository
import confirm.email.ui.screens.about.AboutScreen
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
    Row(modifier = Modifier.fillMaxSize()) {
        val widthLeftMenu = if (state == Navigation.Login || state == Navigation.CreateLetter)
            0.dp
        else
            200.dp
        Column(Modifier.width(widthLeftMenu).background(Color.LightGray).fillMaxHeight()) {
            user.data?.name?.let { username ->
                Row {
                    Text(
                        username,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                    Icon(
                        painter = painterResource("img/info.svg"),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { state = Navigation.About },
                        contentDescription = "About"
                    )
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            when (state) {
                Navigation.Empty -> {
                    Text("Empty")
                }
                Navigation.About -> {
                    AboutScreen()
                }
                Navigation.Login -> {
                    LoginView(LoginViewModel()) {
                        state = Navigation.Empty
                    }
                }
                Navigation.ReadLetter -> {
                    Text("ReadLetter")
                }
                Navigation.CreateLetter -> {
                    Text("CreateLetter")
                }
            }
        }
    }
}