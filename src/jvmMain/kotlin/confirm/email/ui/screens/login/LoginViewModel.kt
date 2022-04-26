package confirm.email.ui.screens.login

import confirm.email.base.ViewModel
import confirm.email.countKey
import confirm.email.data.repository.UserRepository
import confirm.email.enableLogging
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    val user get() = userRepository.user

    fun login(name: String, host: String, logging: Boolean, countKeys: Int) {
        enableLogging = logging
        countKey = countKeys
        viewModelScope.launch {
            userRepository.login(name, host)
        }
    }
}