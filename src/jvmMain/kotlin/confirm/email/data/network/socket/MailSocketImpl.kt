package confirm.email.data.network.socket

import confirm.email.data.network.SocketConsumer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*

class MailSocketImpl(private val socketConsumer: SocketConsumer) : MailSocket {
    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    private var socketSession: ClientWebSocketSession? = null
    private var onConnectionListener: ((Boolean) -> Unit)? = null

    override fun setOnConnectionListener(onConnectionListener: (Boolean) -> Unit) {
        this.onConnectionListener = onConnectionListener
    }

    override suspend fun connect(url: String) {
        client.webSocket(urlString = url) {
            send("my@name.co")
            socketSession = this
            onConnectionListener?.invoke(true)
            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println(text)
                            socketConsumer.proceed(text)
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
    }

    override suspend fun send(text: String) {
        socketSession?.send(text)
    }
}