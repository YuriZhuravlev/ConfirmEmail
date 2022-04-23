package confirm.email.ui.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, fontSize = 20.sp, fontWeight = FontWeight(700))
}

@Composable
fun BigText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, fontSize = 18.sp, fontWeight = FontWeight(500))
}

@Composable
fun SmallText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text, modifier = modifier, fontSize = 10.sp,
        fontWeight = FontWeight(400),
        color = color,
        maxLines = maxLines
    )
}