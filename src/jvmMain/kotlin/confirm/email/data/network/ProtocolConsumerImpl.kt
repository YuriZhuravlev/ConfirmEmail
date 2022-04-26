package confirm.email.data.network

import com.google.gson.Gson
import confirm.email.data.model.UILetter
import confirm.email.data.network.model.SendMessage
import confirm.email.protocol.ProtocolMessage
import confirm.email.protocol.ProtocolProceed
import java.io.PrintStream

class ProtocolConsumerImpl(private val gson: Gson) : ProtocolConsumer {
    private var onSend: (String) -> Unit = {
        println("ProtocolConsumerImpl: default onSend!! $it")
    }
    private var onSuccessInbox: (UILetter) -> Unit = {
        println("ProtocolConsumerImpl: default onSuccessInbox!! $it")
    }
    private val sessions = LinkedHashMap<String, ProtocolProceed>()

    override fun setOnSuccessInbox(onSuccessInbox: (UILetter) -> Unit) {
        this.onSuccessInbox = onSuccessInbox
    }

    private fun send(message: ProtocolMessage) {
        onSend(gson.toJson(SendMessage(message = gson.toJson(message), to = message.to)))
    }

    override fun proceed(text: String) {
        try {
            val message = gson.fromJson(text, ProtocolMessage::class.java)
            if (sessions.contains(message.uuid)) {
                sessions[message.uuid]?.proceed(message)
            } else {
                input(
                    message.uuid,
                    message.encryptedMessage!!,
                    ticket = "TIcket",
                    onError = { it.printStackTrace() },
                    onResult = ::success,
                    to = message.from,
                    from = message.to,
                    printStream = ProtocolProceed.defaultLogger()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun success(text: String) {
        try {
            onSuccessInbox(gson.fromJson(text, UILetter::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        onResult: (String) -> Unit,
        to: String,
        from: String
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
            onResult,
            to,
            from
        )
    }

    override fun output(
        uuid: String,
        message: String,
        emptyMessage: String,
        countKey: Int,
        printStream: PrintStream?,
        onError: (Throwable) -> Unit,
        onResult: (String) -> Unit,
        to: String,
        from: String
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
            onResult,
            to,
            from
        )
    }
}