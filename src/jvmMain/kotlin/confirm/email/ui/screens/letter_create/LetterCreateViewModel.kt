package confirm.email.ui.screens.letter_create

import confirm.email.base.ViewModel
import confirm.email.data.repository.LetterRepository
import confirm.email.data.repository.UserRepository

class LetterCreateViewModel(
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository
) : ViewModel() {
}