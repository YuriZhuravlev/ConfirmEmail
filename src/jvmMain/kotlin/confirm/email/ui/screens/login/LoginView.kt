package confirm.email.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import confirm.email.data.Resource
import confirm.email.ui.view.BigText
import confirm.email.ui.view.SmallText

@Composable
fun LoginView(viewModel: LoginViewModel, toAbout: () -> Unit, onSuccess: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color.White).padding(vertical = 24.dp)) {
        val user by viewModel.user.collectAsState()
        var logging by remember { mutableStateOf(false) }
        var countKeys by remember { mutableStateOf("8") }
        var name by remember { mutableStateOf("@example.co") }
        var host by remember { mutableStateOf("wss://confirm-email-socket.herokuapp.com/") }
        if (user.data != null) {
            onSuccess()
        }
        BigText(
            "Введите данные для входа",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
        )
        TextField(
            value = host, onValueChange = { host = it },
            enabled = user.status != Resource.Status.Loading,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            singleLine = true, isError = user.status == Resource.Status.Failed,
            label = { Text("Адрес сервера") }
        )
        TextField(
            value = name, onValueChange = { name = it },
            enabled = user.status != Resource.Status.Loading,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            singleLine = true, isError = user.status == Resource.Status.Failed,
            label = { Text("Адрес электронной почты") }
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                viewModel.login(name, host, logging, countKeys.toInt())
            }, enabled = name.isNotBlank()
                    && host.isNotBlank()
                    && user.status != Resource.Status.Loading
                    && countKeys.toIntOrNull().let { it != null && it in 2..32 }
        ) {
            Text("Войти")
        }
        if (user.status == Resource.Status.Loading)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        if (user.status == Resource.Status.Failed)
            Text(
                user.error?.message ?: "Произошла ошибка",
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        Box(Modifier.weight(1f))
        Column(
            Modifier.padding(start = 8.dp).width(300.dp).border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row {
                SmallText("Логгирование включено", Modifier.align(Alignment.CenterVertically).weight(1f))
                Switch(
                    checked = logging, onCheckedChange = { logging = it },
                    Modifier.align(Alignment.CenterVertically).height(12.dp)
                )
            }
            Divider(Modifier.padding(vertical = 6.dp))
            Row {
                SmallText(
                    "Количество пар ключей для исходящих сообщений [2..32]",
                    Modifier.align(Alignment.CenterVertically).weight(1f)
                )
                BasicTextField(
                    countKeys,
                    { countKeys = it },
                    Modifier.align(Alignment.CenterVertically).padding(start = 4.dp),
                    textStyle = TextStyle(fontSize = 12.sp, textAlign = TextAlign.End),
                    singleLine = true
                )
            }
        }
        Text("О программе", modifier =
        Modifier.align(Alignment.End)
            .padding(end = 18.dp)
            .clickable { toAbout() })
    }
}