package confirm.email.data.network.model

import com.google.gson.annotations.SerializedName

data class GetMessage(
    @SerializedName("message")
    val message: String?,
    @SerializedName("from")
    val from: String?,
    @SerializedName("error")
    val error: String?
)