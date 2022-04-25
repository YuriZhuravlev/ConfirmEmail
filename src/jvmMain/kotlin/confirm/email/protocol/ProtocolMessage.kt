package confirm.email.protocol

data class ProtocolMessage(
    val step: Int,
    val uuid: String,
    val encryptedMessage: String? = null,
    val encryptedPairs: List<Pair<String, String>>? = null,
    val halfKeys: List<Pair<String?, String?>>? = null,
    val bytesSlice: String? = null,
    val byteIndex: Int? = null
)
