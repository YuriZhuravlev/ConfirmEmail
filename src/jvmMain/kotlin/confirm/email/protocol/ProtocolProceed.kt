package confirm.email.protocol

import confirm.email.utils.formatString
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.*

sealed class ProtocolProceed(
    val uuid: String,
    protected val onSend: ((ProtocolMessage) -> Unit),
    protected val onError: ((Throwable) -> Unit),
    protected val onResult: ((String) -> Unit)
) {
    abstract fun proceed(message: ProtocolMessage)


    companion object {
        fun defaultLogger(path: String = "log.txt") = PrintStream(FileOutputStream(File(path), true))
    }

    class OutputProtocolProceed(
        uuid: String,
        message: String,
        emptyMessage: String,
        countKey: Int = 8,
        printStream: PrintStream? = null,
        onSend: ((ProtocolMessage) -> Unit),
        onError: ((Throwable) -> Unit),
        onResult: ((String) -> Unit)
    ) : ProtocolProceed(
        uuid, onSend, onError, onResult
    ) {
        private lateinit var instanceProtocol: ConfirmEmailProtocol.Outbox

        init {
            try {
                instanceProtocol = ConfirmEmailProtocol.Outbox(
                    message,
                    emptyMessage,
                    countKey,
                    printStream
                )
                instanceProtocol.encryptMessage()
                onSend(
                    ProtocolMessage(
                        2,
                        uuid,
                        encryptedMessage = instanceProtocol.getEncryptMessage()
                    )
                )
                instanceProtocol.genKeys()
                onSend(
                    ProtocolMessage(
                        4,
                        uuid,
                        encryptedPairs = instanceProtocol.encryptedEmptyMessage()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }

        override fun proceed(message: ProtocolMessage) {
            try {
                when (message.step) {
                    6 -> {
                        instanceProtocol.setEncryptedTickets(message.encryptedPairs!!)
                        onSend(
                            ProtocolMessage(
                                7,
                                uuid,
                                halfKeys = instanceProtocol.getSupportKeys()
                            )
                        )
                    }
                    8 -> {
                        instanceProtocol.setSupportInKeys(message.halfKeys!!)
                        if (!instanceProtocol.decryptingTickets()) {
                            onError(Throwable("Квитанции неверны"))
                        } else {
                            sliceBytes = instanceProtocol.getBytesSliceKeys()
                        }
                    }
                    11 -> {
                        if (message.byteIndex == inSliceBytes.size) {
                            inSliceBytes.add(message.bytesSlice!!)
                            if (sliceBytes!!.size == inSliceBytes.size) {
                                // передача окончена
                                instanceProtocol.setBytesSliceInKeys(inSliceBytes)
                                val mes = instanceProtocol.decryptingTicketsFinally()
                                if (mes != null)
                                    onResult(mes)
                                else
                                    onError(Throwable("Неудалось расшифровавать квитанцию"))
                            } else {
                                onSend(
                                    ProtocolMessage(
                                        12,
                                        uuid,
                                        bytesSlice = sliceBytes!![byteIndex],
                                        byteIndex = byteIndex
                                    )
                                )
                                byteIndex++
                            }
                        } else
                            onError(Throwable("Рассинхронизация при передаче ключей!"))

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }

        private var sliceBytes: List<String>? = null
        private var byteIndex = 0

        private val inSliceBytes = mutableListOf<String>()
    }

    class InputProtocolProceed(
        uuid: String,
        encryptedMessage: String,
        printStream: PrintStream? = null,
        ticket: String,
        onSend: ((ProtocolMessage) -> Unit),
        onError: ((Throwable) -> Unit),
        onResult: ((String) -> Unit)
    ) :
        ProtocolProceed(uuid, onSend, onError, onResult) {

        private lateinit var instanceProtocol: ConfirmEmailProtocol.Inbox

        init {
            try {
                instanceProtocol = ConfirmEmailProtocol.Inbox(
                    encryptedMessage,
                    ticket,
                    Date().formatString(),
                    uuid,
                    printStream
                )
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }

        override fun proceed(message: ProtocolMessage) {
            try {
                when (message.step) {
                    4 -> {
                        instanceProtocol.setEncryptedEmptyMessage(message.encryptedPairs!!)
                        instanceProtocol.genKeys()
                        onSend(
                            ProtocolMessage(
                                6,
                                uuid,
                                encryptedPairs = instanceProtocol.getEncryptedTickets()
                            )
                        )
                    }
                    7 -> {
                        instanceProtocol.setSupportInKeys(message.halfKeys!!)
                        onSend(
                            ProtocolMessage(
                                8,
                                uuid,
                                halfKeys = instanceProtocol.getSupportKeys()
                            )
                        )
                        if (instanceProtocol.decryptingTickets()) {
                            sliceBytes = instanceProtocol.getBytesSliceKeys()
                            onSend(
                                ProtocolMessage(
                                    11,
                                    uuid,
                                    bytesSlice = sliceBytes!![byteIndex],
                                    byteIndex = byteIndex
                                )
                            )
                        } else {
                            onError(Throwable("Квитанции неверны"))
                        }
                    }
                    12 -> {
                        if (message.byteIndex == inSliceBytes.size) {
                            inSliceBytes.add(message.bytesSlice!!)
                            if (sliceBytes!!.size == inSliceBytes.size) {
                                // передача окончена
                                instanceProtocol.setBytesSliceInKeys(inSliceBytes)
                                val mes = instanceProtocol.decryptionMessage()
                                if (mes != null)
                                    onResult(mes)
                                else
                                    onError(Throwable("Неудалось расшифровавать сообщение"))
                            } else {
                                byteIndex++
                                onSend(
                                    ProtocolMessage(
                                        11,
                                        uuid,
                                        bytesSlice = sliceBytes!![byteIndex],
                                        byteIndex = byteIndex
                                    )
                                )
                            }
                        } else
                            onError(Throwable("Рассинхронизация при передаче ключей!"))
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }

        private var sliceBytes: List<String>? = null
        private var byteIndex = 0

        private val inSliceBytes = mutableListOf<String>()
    }
}