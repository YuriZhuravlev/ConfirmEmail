package confirm.email.ui.screens.letters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LettersScreen(lettersViewModel: LettersViewModel) {
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.width(200.dp).background(Color.LightGray).fillMaxHeight()) {
//        user.data?.name?.let { username ->
//            Row {
//                Text(
//                    username,
//                    modifier = Modifier
//                        .align(Alignment.CenterVertically)
//                        .weight(1f)
//                        .padding(start = 8.dp)
//                )
//                Icon(
//                    painter = painterResource("img/info.svg"),
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .clickable { state = Navigation.About },
//                    contentDescription = "About"
//                )
//            }
//        }
        }
        EmptyView()
    }
}


@Composable
fun EmptyView() {
    Box(Modifier.fillMaxSize()) {
        Text("Сообщений не выбрано", modifier = Modifier.align(Alignment.Center))
    }
}