package confirm.email.ui.screens.letter_create

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable

@Composable
fun LetterCreateScreen(viewModel: LetterCreateViewModel) {
    Column {

        OutlinedTextField("Body", {})
    }
}