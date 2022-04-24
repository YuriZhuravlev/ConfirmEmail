package confirm.email.data.network.socket

interface MailSocket {
    fun setOnConnectionListener(onConnectionListener: (Boolean) -> Unit)
    suspend fun send(text: String)
    suspend fun connect(url: String)
    suspend fun disconnect()
}
