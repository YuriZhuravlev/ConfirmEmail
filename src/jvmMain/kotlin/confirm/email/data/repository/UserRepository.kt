package confirm.email.data.repository

import confirm.email.data.Resource
import confirm.email.data.model.UIUser
import confirm.email.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

object UserRepository {
    private val _user = MutableStateFlow<Resource<UIUser?>>(Resource.SuccessResource(null))
    val user = _user.asStateFlow()

    suspend fun login(name: String) {
        _user.emit(Resource.LoadingResource())
        val result = withContext(Dispatchers.IO) {
            try {
                if (name.isValidEmail()) {
                    Resource.SuccessResource<UIUser?>(UIUser(name))
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
}