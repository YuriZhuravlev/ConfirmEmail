package confirm.email.data.network.model

import com.google.gson.annotations.SerializedName

data class ErrorMessage(
    @SerializedName("error")
    val error: String
)
