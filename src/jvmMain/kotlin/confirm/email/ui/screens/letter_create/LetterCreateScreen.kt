package confirm.email.ui.screens.letter_create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import confirm.email.data.Resource
import confirm.email.data.model.UILetter
import confirm.email.ui.view.SmallText
import java.util.*

@Composable
fun LetterCreateScreen(viewModel: LetterCreateViewModel, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Color.White)
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        val result by viewModel.result.collectAsState()
        var fromName by remember { mutableStateOf("") }
        val fromEmail by remember { mutableStateOf(viewModel.user.value.data?.name ?: "") }
        var toName by remember { mutableStateOf("") }
        var toEmail by remember { mutableStateOf("") }
        var subject by remember { mutableStateOf("") }
        var text by remember { mutableStateOf("") }
        if (result.data != null) {
            onBack()
        }
        Row {
            Icon(
                painterResource("img/arrow_back.svg"),
                null,
                Modifier.clickable { onBack() }
            )
            if (result.status == Resource.Status.Failed) {
                Text(
                    result.error?.message ?: "Ошибка",
                    modifier = Modifier.padding(start = 16.dp).weight(1f),
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
        Row {
            SmallText(
                "Кому (имя):",
                modifier = Modifier.align(Alignment.CenterVertically), color = Color.DarkGray
            )
            BasicTextField(
                toName, { toName = it },
                modifier = Modifier.weight(1f)
                    .padding(start = 8.dp),
                maxLines = 1
            )
        }
        Divider()
        Row {
            SmallText(
                "Кому:",
                modifier = Modifier.align(Alignment.CenterVertically), color = Color.DarkGray
            )
            BasicTextField(
                toEmail, { toEmail = it }, modifier = Modifier.weight(1f)
                    .padding(start = 8.dp), maxLines = 1
            )
        }
        Divider()
        Row {
            SmallText(
                "Отправитель (имя):",
                modifier = Modifier.align(Alignment.CenterVertically),
                color = Color.DarkGray
            )
            BasicTextField(
                fromName,
                { fromName = it },
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                maxLines = 1
            )
        }
        Divider()
        Row {
            SmallText(
                "Отправитель:",
                modifier = Modifier.align(Alignment.CenterVertically), color = Color.DarkGray
            )
            BasicTextField(
                fromEmail, {},
                modifier = Modifier.weight(1f).padding(start = 8.dp), maxLines = 1,
                readOnly = true
            )
        }
        Divider()
        Row {
            SmallText(
                "Тема:",
                modifier = Modifier.align(Alignment.CenterVertically), color = Color.DarkGray
            )
            BasicTextField(
                subject,
                { subject = it },
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                maxLines = 1
            )
        }
        OutlinedTextField(text, { text = it }, modifier = Modifier.weight(1f).fillMaxWidth(), shape = RectangleShape)
        Row {
            Button(
                onClick = {
                    viewModel.send(
                        UILetter(
                            UUID.randomUUID().toString(),
                            fromName,
                            fromEmail,
                            toName,
                            toEmail,
                            Date(),
                            subject,
                            text
                        )
                    )
                },
                enabled = result.status != Resource.Status.Loading,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Отправить", modifier = Modifier.padding(end = 6.dp))
                if (result.status == Resource.Status.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        painterResource("img/mail.svg"),
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}