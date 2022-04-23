package confirm.email.data.repository

import confirm.email.data.Resource
import confirm.email.data.model.UIUser
import confirm.email.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class UserRepository {
    private val _user = MutableStateFlow<Resource<UIUser?>>(Resource.SuccessResource(null))
    val user = _user.asStateFlow()

    suspend fun login(name: String, host: String) {
        _user.emit(Resource.LoadingResource())
        val result = withContext(Dispatchers.IO) {
            try {
                if (name.isValidEmail()) {
                    // TODO
                    Resource.SuccessResource<UIUser?>(UIUser(name, host))
                } else {
                    Resource.FailedResource(Throwable("Неккоректный email-адрес"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.FailedResource(e)
            }
        }
        _user.emit(result)
    }

    suspend fun logout() {
        _user.emit(Resource.SuccessResource(null))
    }
}