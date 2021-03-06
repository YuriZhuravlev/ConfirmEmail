// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import confirm.email.di.AppModule
import confirm.email.ui.screens.NavView
import org.koin.core.context.startKoin

@Composable
@Preview
fun App() {
    MaterialTheme(colors = MaterialTheme.colors.copy(primary = Color.Black)) {
        NavView()
    }
}

fun main() = application {
    startKoin {
        modules(AppModule)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Электронное письмо с подтверждением",
        icon = painterResource("img/icon.svg")
    ) {
        App()
    }
}

