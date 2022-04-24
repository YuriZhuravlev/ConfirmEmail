package confirm.email.data.network

import kotlinx.coroutines.flow.StateFlow

interface SocketConsumer {
    val status: StateFlow<Status>

    fun setup(username: String, url: String)

    suspend fun connect(): Status
    suspend fun disconnect()

    sealed class Status {
        object Connected : Status()
        object Connecting : Status()
        class Disconnected(val reason: String?) : Status()
    }
}