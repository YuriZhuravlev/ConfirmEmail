package confirm.email.ui.screens.letters

import confirm.email.base.ViewModel
import confirm.email.data.Resource
import confirm.email.data.model.LetterBox
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

    private val _letters = MutableStateFlow<Resource<LetterBox>>(Resource.LoadingResource())
    val letters = _letters.asStateFlow()

    fun loadLetters() {
        viewModelScope.launch {
            _letters.emit(
                letterRepository.loadLetters(user.value.data?.name)
            )
        }
    }

    fun openLetter(letter: UILetter) {
        viewModelScope.launch { _openLetter.emit(letter) }
    }
}