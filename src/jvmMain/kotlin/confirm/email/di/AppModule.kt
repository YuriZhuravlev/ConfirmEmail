package confirm.email.di

import com.google.gson.Gson
import confirm.email.data.files.FilesManager
import confirm.email.data.network.socket.MailSocket
import confirm.email.data.network.socket.MailSocketImpl
import confirm.email.data.repository.LetterRepository
import confirm.email.data.repository.UserRepository
import confirm.email.ui.screens.letter_create.LetterCreateViewModel
import confirm.email.ui.screens.letters.LettersViewModel
import confirm.email.ui.screens.login.LoginViewModel
import org.koin.dsl.module

val AppModule = module {
    single { Gson() }
    single { FilesManager(get()) }
    single { LetterRepository(get()) }
    single { UserRepository() }
    factory { LetterCreateViewModel(get(), get()) }
    factory { LettersViewModel(get(), get()) }
    factory { LoginViewModel(get()) }
    factory<MailSocket> { MailSocketImpl() }
}