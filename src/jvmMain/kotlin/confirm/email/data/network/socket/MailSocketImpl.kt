package confirm.email.data.network.socket

import confirm.email.apiLogger
import confirm.email.data.network.ProtocolConsumer
import confirm.email.data.network.SocketProceed
import confirm.email.utils.formatLog
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

class MailSocketImpl(
    private val socketProceed: SocketProceed,
    private val protocolConsumer: ProtocolConsumer
) : MailSocket {
    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    init {
        protocolConsumer.setOnSend {
            MainScope().launch {
                send(it)
            }
        }
    }

    private var socketSession: ClientWebSocketSession? = null
    private var onConnectionListener: ((Boolean) -> Unit)? = null

    override fun setOnConnectionListener(onConnectionListener: (Boolean) -> Unit) {
        this.onConnectionListener = onConnectionListener
    }

    override suspend fun connect(url: String) {
        try {
            if (socketSession?.isActive == true) {
                socketSession?.close()
            }
            client.webSocket(urlString = url) {
                try {
                    socketSession = this
                    onConnectionListener?.invoke(true)
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                println(text)
                                apiLogger()?.println("${Date().formatLog()}: <- $text")
                                socketProceed.proceed(text)
                            }
                            is Frame.Close -> {
                                println("Close")
                                socketSession = null
                                onConnectionListener?.invoke(false)
                            }
                            else -> {
                                println("Incoming other frame")
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    socketSession = null
                    onConnectionListener?.invoke(false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            socketSession = null
            onConnectionListener?.invoke(false)
        }
    }

    override suspend fun send(text: String) {
        apiLogger()?.println("${Date().formatLog()}: -> $text")
        socketSession?.send(text)
    }

    override suspend fun disconnect() {
        socketSession?.close()
        socketSession = null
        onConnectionListener?.invoke(false)
    }
}