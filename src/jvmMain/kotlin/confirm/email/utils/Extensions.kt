package confirm.email.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern.compile
import kotlin.experimental.xor

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

private val logFormatter by lazy {
    SimpleDateFormat("[HH:mm:ss.SSSS dd-MM-yyyy]")
}

fun Date.formatString() = formatter.format(this)

fun Date.formatLog() = logFormatter.format(this)

fun ByteArray.xor(key: ByteArray): ByteArray {
    return ByteArray(size) { i ->
        get(i).xor(key[i])
    }
}