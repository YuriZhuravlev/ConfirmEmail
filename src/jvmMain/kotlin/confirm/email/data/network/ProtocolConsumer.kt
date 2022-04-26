package confirm.email.data.network

import java.io.PrintStream

interface ProtocolConsumer {
    fun proceed(text: String)

    fun setOnSend(onSend: ((String) -> Unit))

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