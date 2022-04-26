package confirm.email.data.network

import com.google.gson.Gson
import confirm.email.data.network.model.GetMessage
import confirm.email.data.network.socket.MailSocket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.java.KoinJavaComponent.getKoin

class SocketConsumerImpl(
    private val gson: Gson,
    private val protocolConsumer: ProtocolConsumer
) : SocketConsumer, SocketProceed {
    private val _status = MutableStateFlow<SocketConsumer.Status>(SocketConsumer.Status.Disconnected(null))
    override val status = _status.asStateFlow()

    private var username: String? = null
    private var url: String? = null

    override fun setup(username: String, url: String) {
        this.username = username
        this.url = url
    }

    private val mailSocket: MailSocket by lazy {
        getKoin().get<MailSocket>().apply {
            setOnConnectionListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if (it) {
                        username.let { name ->
                            if (name != null)
                                mailSocket.send(name)
                            else
                                mailSocket.disconnect()
                        }
                    } else {
                        _status.emit(SocketConsumer.Status.Disconnected("Потеряно соединение"))
                    }
                }
            }
        }
    }

    override suspend fun proceed(text: String) {
        println("proceed: $text")
        try {
            val message = gson.fromJson(text, GetMessage::class.java)
            // Если первое сообщение
            if (_status.value == SocketConsumer.Status.Connecting) {
                if (message.from == SERVER && message.message == username)
                    _status.emit(SocketConsumer.Status.Connected)
                else {
                    _status.emit(SocketConsumer.Status.Disconnected(message.error))
                }
            } else {
                if (message.message != null) {
                    protocolConsumer.proceed(message.message)
                } else {
                    println("proceed with message null: $message")
                }
            }
        } catch (e: Exception) {
            println("proceed: Failed")
            e.printStackTrace()
        }
    }

    override suspend fun connect(): SocketConsumer.Status {
        _status.emit(SocketConsumer.Status.Connecting)
        println("starting...")
        MainScope().launch(Dispatchers.IO) {
            mailSocket.connect(url!!)
        }
        println("connecting...")
        while (_status.value == SocketConsumer.Status.Connecting) {
            delay(TIMEOUT)
        }
        return _status.value
    }

    override suspend fun disconnect() {
        mailSocket.disconnect()
    }

    companion object {
        private const val TIMEOUT = 100L
        private const val SERVER = "server"
    }
}