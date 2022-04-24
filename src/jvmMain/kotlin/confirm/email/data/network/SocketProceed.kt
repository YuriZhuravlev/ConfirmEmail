package confirm.email.data.network

interface SocketProceed {
    suspend fun proceed(text: String)
}