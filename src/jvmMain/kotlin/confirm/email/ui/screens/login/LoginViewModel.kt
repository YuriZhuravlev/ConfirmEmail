package confirm.email.ui.screens.login

import confirm.email.base.ViewModel
import confirm.email.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository
    val user get() = userRepository.user

    fun login(name: String) {
        viewModelScope.launch {
            userRepository.login(name)
        }
    }
}