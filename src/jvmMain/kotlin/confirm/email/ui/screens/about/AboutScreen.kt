package confirm.email.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import confirm.email.ui.view.BigText
import confirm.email.ui.view.HeaderText

@Composable
fun AboutScreen(onBackArrow: (() -> Unit)? = null) {
    Column(Modifier.fillMaxSize().background(Color.White).padding(8.dp)) {
        if (onBackArrow != null) {
            Icon(
                painter = painterResource("img/arrow_back.svg"),
                contentDescription = null,
                modifier = Modifier.clickable { onBackArrow() }
            )
        }
        HeaderText("О программе", modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp))
        BigText("Расчетное задание")
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)) {
            Text("Журавлев Юрий", textAlign = TextAlign.Start)
            Box(modifier = Modifier.weight(1f))
            Text("гр. А-05-18", textAlign = TextAlign.End)
        }
        Text("Демонстрация программной реализации электронной почты с подтверждением.")
    }
}