package confirm.email.ui.screens.login

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
fun LoginView(viewModel: LoginViewModel, onSuccess: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        val user by viewModel.user.collectAsState()
        var name by remember { mutableStateOf("") }
        if (user.data != null) {
            onSuccess()
        }
        BigText(
            "Введите адрес электронной почты",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
        )
        TextField(
            value = name, onValueChange = { name = it },
            enabled = user.status != Resource.Status.Loading,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            singleLine = true, isError = user.status == Resource.Status.Failed
        )
        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            viewModel.login(name)
        }, enabled = name.isNotBlank() && user.status != Resource.Status.Loading) {
            Text("Войти")
        }
        if (user.status == Resource.Status.Loading)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        if (user.status == Resource.Status.Failed)
            Text(user.error?.message ?: "Произошла ошибка", color = Color.Red)
    }
}