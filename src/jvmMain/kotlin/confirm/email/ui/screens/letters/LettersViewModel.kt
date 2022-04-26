package confirm.email.ui.screens.letters

import confirm.email.base.ViewModel
import confirm.email.data.model.UILetter
import confirm.email.data.repository.LetterRepository
import confirm.email.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LettersViewModel(
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository
) : ViewModel() {
    val user get() = userRepository.user
    private val _openLetter = MutableStateFlow<UILetter?>(null)
    val openLetter = _openLetter.asStateFlow()

    val letters get() = letterRepository.letters

    fun loadLetters() {
        viewModelScope.launch {
            letterRepository.loadLetters()
        }
    }

    fun openLetter(letter: UILetter) {
        viewModelScope.launch { _openLetter.emit(letter) }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}