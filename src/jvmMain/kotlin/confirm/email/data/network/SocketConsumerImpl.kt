package confirm.email.data.network

import confirm.email.data.network.socket.MailSocket
import confirm.email.data.network.socket.MailSocketImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SocketConsumerImpl : SocketConsumer {
    private val _status = MutableStateFlow<Status>(Status.Disconnected)
    val status = _status.asStateFlow()

    val mailSocket: MailSocket by lazy {
        MailSocketImpl(this).apply {
            setOnConnectionListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if (it) {
                        _status.emit(Status.Connected)
                    } else {
                        _status.emit(Status.Disconnected)
                    }
                }
            }
        }
    }

    override fun proceed(text: String) {
        //TODO("Not yet implemented")
    }

    enum class Status {
        Connected, Connecting, Disconnected
    }
}