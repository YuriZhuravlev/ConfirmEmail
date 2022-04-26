package confirm.email.data.repository

import com.google.gson.Gson
import confirm.email.data.Resource
import confirm.email.data.files.FilesManager
import confirm.email.data.model.LetterBox
import confirm.email.data.model.UILetter
import confirm.email.data.network.ProtocolConsumer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.PrintStream
import java.util.*

class LetterRepository(
    private val filesManager: FilesManager,
    private val protocolConsumer: ProtocolConsumer,
    private val gson: Gson
) {
    private val _letters = MutableStateFlow<Resource<LetterBox>>(Resource.LoadingResource())
    val letters = _letters.asStateFlow()

    suspend fun loadLetters(name: String?) {
        _letters.emit(
            try {
                if (name == null) {
                    Resource.FailedResource(Throwable("Имя пользователя пусто!"))
                } else {
                    Resource.SuccessResource(filesManager.loadBox(name))
                }
            } catch (e: Exception) {
                Resource.FailedResource(e)
            }
        )
    }

    suspend fun send(
        letter: UILetter,
        countKey: Int = 8,
        printStream: PrintStream? = null
    ): Resource<UILetter?> {
        return try {
            var result: Result<String>? = null
            protocolConsumer.output(
                UUID.randomUUID().toString(),
                gson.toJson(letter),
                letter.fromEmail + letter.data,
                countKey,
                printStream,
                onError = {
                    result = Result.failure(it)
                },
                onResult = {
                    result = Result.success(it)
                },
                to = letter.toEmail,
                from = letter.fromEmail
            )
            while (result == null) {
                delay(TIMEOUT)
            }
            if (result!!.isSuccess) {
                filesManager.saveLetter(letter.fromEmail, letter, true)
                Resource.SuccessResource(letter)
            } else {
                Resource.FailedResource(result!!.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Resource.FailedResource(e)
        }
    }

    companion object {
        private const val TIMEOUT = 100L
    }
}