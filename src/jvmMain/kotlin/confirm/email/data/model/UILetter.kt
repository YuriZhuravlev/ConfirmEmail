package confirm.email.data.model

import java.util.*

data class UILetter(
    val fromName: String,
    val fromEmail: String,
    val toName: String,
    val toEmail: String,
    val date: Date,
    val subject: String,
    val data: String
)
