package confirm.email.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern.compile

private val emailRegex = compile(
    "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

fun String.isValidEmail() = emailRegex.matcher(this).matches()

private val formatter by lazy {
    SimpleDateFormat("HH:mm d.MM", Locale.getDefault())
}

fun Date.formatString() = formatter.format(this)