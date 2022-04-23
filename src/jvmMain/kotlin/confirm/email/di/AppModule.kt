package confirm.email.di

import confirm.email.data.network.socket.MailSocket
import confirm.email.data.network.socket.MailSocketImpl
import confirm.email.data.repository.LetterRepository
import confirm.email.data.repository.UserRepository
import confirm.email.ui.screens.letter_create.LetterCreateViewModel
import confirm.email.ui.screens.letters.LettersViewModel
import confirm.email.ui.screens.login.LoginViewModel
import org.koin.dsl.module

val AppModule = module {
    single { LetterRepository() }
    single { UserRepository() }
    factory { LetterCreateViewModel(get(), get()) }
    factory { LettersViewModel(get(), get()) }
    factory { LoginViewModel(get()) }
    factory<MailSocket> { MailSocketImpl() }
}