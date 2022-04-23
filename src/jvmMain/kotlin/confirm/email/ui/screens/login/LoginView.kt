package confirm.email.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import confirm.email.data.Resource
import confirm.email.ui.view.BigText

@Composable
fun LoginView(viewModel: LoginViewModel, toAbout: () -> Unit, onSuccess: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color.White)) {
        val user by viewModel.user.collectAsState()
        var name by remember { mutableStateOf("") }
        var host by remember { mutableStateOf("") }
        if (user.data != null) {
            onSuccess()
        }
        BigText(
            "Введите данные для входа",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp, top = 24.dp)
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
                viewModel.login(name, host)
            }, enabled = name.isNotBlank()
                    && host.isNotBlank()
                    && user.status != Resource.Status.Loading
        ) {
            Text("Войти")
        }
        if (user.status == Resource.Status.Loading)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        if (user.status == Resource.Status.Failed)
            Text(user.error?.message ?: "Произошла ошибка", color = Color.Red)
    }
}