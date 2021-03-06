package confirm.email.data.network.model

import com.google.gson.annotations.SerializedName

data class SendMessage(
    @SerializedName("message")
    val message: String,
    @SerializedName("to")
    val to: String
)
