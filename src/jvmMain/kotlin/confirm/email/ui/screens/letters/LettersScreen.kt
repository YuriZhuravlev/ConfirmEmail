package confirm.email.ui.screens.letters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import confirm.email.data.Resource
import confirm.email.ui.screens.about.AboutScreen
import confirm.email.ui.view.BigText
import confirm.email.ui.view.SmallText
import confirm.email.utils.formatString

@Composable
fun LettersScreen(viewModel: LettersViewModel) {
    Row(Modifier.fillMaxSize()) {
        val user = viewModel.user.collectAsState()
        val letters = viewModel.letters.collectAsState()
        val openLetter = viewModel.openLetter.collectAsState()
        var firstLoad by remember { mutableStateOf(false) }
        var showAbout by remember { mutableStateOf(false) }
        if (!firstLoad) {
            viewModel.loadLetters()
            firstLoad = true
        }
        Column(
            Modifier.width(260.dp)
                .background(Color(0xfff5f5f5))
                .fillMaxHeight()
        ) {
            user.value.data?.name?.let { username ->
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
                            .clickable { showAbout = true }
                            .padding(8.dp),
                        contentDescription = "About"
                    )
                    Icon(
                        painter = painterResource("img/logout.svg"),
                        modifier = Modifier
                            .clickable { viewModel.logout() }
                            .padding(8.dp),
                        contentDescription = "Logout"
                    )
                }
                val box = letters.value
                when (box) {
                    is Resource.SuccessResource -> {
                        if (box.data == null || box.data.isEmpty()) {
                            Divider()
                            BigText("Писем нет")
                        } else {
                            if (box.data.outbox.isNotEmpty()) {
                                LazyColumn(modifier = Modifier.weight(1f)) {
                                    item {
                                        Divider()
                                        BigText("Исходящие")
                                    }
                                    items(box.data.outbox) { letter ->
                                        Divider()
                                        Row(modifier = Modifier.padding(4.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.openLetter(letter)
                                                showAbout = false
                                            }
                                        ) {
                                            Column(Modifier.weight(1f)) {
                                                SmallText(
                                                    "${letter.toName} <${letter.toEmail}>",
                                                    color = Color.DarkGray, maxLines = 1
                                                )
                                                Text(letter.subject, maxLines = 1)
                                            }
                                            SmallText(
                                                letter.date.formatString(),
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    item { Divider() }
                                }
                            }
                            if (box.data.inbox.isNotEmpty()) {
                                LazyColumn(modifier = Modifier.weight(1f)) {
                                    item {
                                        Divider()
                                        BigText("Входящие")
                                    }
                                    items(box.data.inbox) { letter ->
                                        Divider()
                                        Row(modifier = Modifier.padding(4.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.openLetter(letter)
                                                showAbout = false
                                            }
                                        ) {
                                            Column(Modifier.weight(1f)) {
                                                SmallText(
                                                    "${letter.fromName} <${letter.fromEmail}>",
                                                    color = Color.DarkGray, maxLines = 1
                                                )
                                                Text(letter.subject, maxLines = 1)
                                            }
                                            SmallText(
                                                letter.date.formatString(),
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    item { Divider() }
                                }
                            }
                        }
                    }
                    is Resource.LoadingResource -> {
                        Text("Загрузка...", Modifier.padding(horizontal = 8.dp))
                        LinearProgressIndicator(Modifier.padding(horizontal = 8.dp))
                    }
                    is Resource.FailedResource -> {
                        Text(
                            box.error?.message ?: "Ошибка",
                            color = Color.Red,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.width(1.dp).fillMaxHeight()
                .background(Color.LightGray)
        )
        if (showAbout) {
            AboutScreen()
        } else {
            openLetter.value.let { letter ->
                if (letter != null) {
                    LetterView(letter)
                } else {
                    EmptyView()
                }
            }
        }
    }
}


@Composable
fun EmptyView() {
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Text("Сообщений не выбрано", modifier = Modifier.align(Alignment.Center))
    }
}