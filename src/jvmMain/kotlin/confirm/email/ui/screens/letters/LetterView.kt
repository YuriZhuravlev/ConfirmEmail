package confirm.email.ui.screens.letters

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import confirm.email.data.model.UILetter
import confirm.email.ui.view.SmallText
import confirm.email.utils.formatString

@Composable
fun LetterView(letter: UILetter) {
    Column {
        Column(Modifier.background(Color(0xfff8f8ff)).padding(8.dp)) {
            Row {
                Text(
                    text = "${letter.fromName} <${letter.fromEmail}>",
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500)
                )
                SmallText(letter.date.formatString())
            }
            Text(letter.subject)
            Row {
                Text("Кому:", Modifier.padding(end = 8.dp), fontSize = 12.sp)
                Text("${letter.toName} <${letter.toEmail}>", color = Color.DarkGray, fontSize = 12.sp)
            }
        }
        OutlinedTextField(
            letter.data, {},
            readOnly = true,
            modifier = Modifier.fillMaxSize().focusable(false),
            shape = RectangleShape,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                backgroundColor = Color.White
            )
        )
    }
}