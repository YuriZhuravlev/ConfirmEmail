package confirm.email.data.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SocketConsumerImpl : SocketConsumer {
    private val _status = MutableStateFlow<Status>(Status.Disconnected)
    val status = _status.asStateFlow()

    override fun proceed(text: String) {
        //TODO("Not yet implemented")
    }

    enum class Status {
        Connected, Connecting, Disconnected
    }
}