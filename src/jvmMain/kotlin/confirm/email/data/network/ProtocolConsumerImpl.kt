package confirm.email.data.network

import com.google.gson.Gson
import confirm.email.protocol.ProtocolMessage
import confirm.email.protocol.ProtocolProceed
import java.io.PrintStream

class ProtocolConsumerImpl(private val gson: Gson) : ProtocolConsumer {
    private var onSend: (String) -> Unit = {}
    private val sessions = LinkedHashMap<String, ProtocolProceed>()

    private fun send(message: ProtocolMessage) {
        onSend(gson.toJson(message))
    }

    override fun proceed(text: String) {
        val message = gson.fromJson(text, ProtocolMessage::class.java)
        sessions[message.uuid]?.proceed(message)
    }

    override fun setOnSend(onSend: (String) -> Unit) {
        this.onSend = onSend
    }

    override fun input(
        uuid: String,
        encryptedMessage: String,
        printStream: PrintStream?,
        ticket: String,
        onError: (Throwable) -> Unit,
        onResult: (String) -> Unit
    ) {
        sessions[uuid] = ProtocolProceed.InputProtocolProceed(
            uuid,
            encryptedMessage,
            printStream,
            ticket,
            ::send,
            {
                sessions.remove(uuid)
                onError(it)
            },
            onResult
        )
    }

    override fun output(
        uuid: String,
        message: String,
        emptyMessage: String,
        countKey: Int,
        printStream: PrintStream?,
        onError: (Throwable) -> Unit,
        onResult: (String) -> Unit
    ) {
        sessions[uuid] = ProtocolProceed.OutputProtocolProceed(
            uuid,
            message,
            emptyMessage,
            countKey,
            printStream,
            ::send,
            {
                sessions.remove(uuid)
                onError(it)
            },
            onResult
        )
    }
}