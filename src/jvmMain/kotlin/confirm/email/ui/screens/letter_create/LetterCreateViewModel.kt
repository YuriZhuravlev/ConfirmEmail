package confirm.email.ui.screens.letter_create

import confirm.email.base.ViewModel
import confirm.email.countKey
import confirm.email.data.Resource
import confirm.email.data.model.UILetter
import confirm.email.data.repository.LetterRepository
import confirm.email.data.repository.UserRepository
import confirm.email.logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LetterCreateViewModel(
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository
) : ViewModel() {
    private val _result = MutableStateFlow<Resource<UILetter?>>(Resource.SuccessResource(null))
    val result = _result.asStateFlow()

    val user get() = userRepository.user

    fun send(letter: UILetter) {
        viewModelScope.launch {
            _result.emit(Resource.LoadingResource())
            _result.emit(
                letterRepository.send(
                    letter,
                    countKey = countKey,
                    printStream = logger()
                )
            )
        }
    }
}