package confirm.email.ui.screens.letters

import confirm.email.base.ViewModel
import confirm.email.data.repository.LetterRepository
import confirm.email.data.repository.UserRepository

class LettersViewModel(
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository
) : ViewModel() {
}