package confirm.email.data.network

import confirm.email.data.model.UILetter
import java.io.PrintStream

interface ProtocolConsumer {
    fun proceed(text: String)

    fun setOnSend(onSend: ((String) -> Unit))

    fun setOnSuccessInbox(onSuccessInbox: ((UILetter) -> Unit))

    fun output(
        uuid: String,
        message: String,
        emptyMessage: String,
        countKey: Int = 8,
        printStream: PrintStream? = null,
        onError: ((Throwable) -> Unit),
        onResult: ((String) -> Unit),
        to: String,
        from: String
    )

    fun input(
        uuid: String,
        encryptedMessage: String,
        printStream: PrintStream? = null,
        ticket: String,
        onError: ((Throwable) -> Unit),
        onResult: ((String) -> Unit),
        to: String,
        from: String
    )
}