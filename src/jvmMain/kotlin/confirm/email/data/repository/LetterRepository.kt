package confirm.email.data.repository

import com.google.gson.Gson
import confirm.email.data.Resource
import confirm.email.data.files.FilesManager
import confirm.email.data.model.LetterBox
import confirm.email.data.model.UILetter
import confirm.email.data.network.ProtocolConsumer
import confirm.email.data.network.SocketConsumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.PrintStream

class LetterRepository(
    private val filesManager: FilesManager,
    private val protocolConsumer: ProtocolConsumer,
    private val gson: Gson,
    private val userRepository: UserRepository,
    private val socketConsumer: SocketConsumer
) {
    private val _letters = MutableStateFlow<Resource<LetterBox>>(Resource.LoadingResource())
    val letters = _letters.asStateFlow()

    init {
        protocolConsumer.setOnSuccessInbox {
            MainScope().launch(Dispatchers.IO) {
                userRepository.user.value.data?.name?.let { name ->
                    filesManager.saveLetter(name, it, false)
                    loadLetters()
                }
            }
        }
    }

    suspend fun loadLetters() {
        _letters.emit(
            try {
                val name = userRepository.user.value.data?.name
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
                letter.uuid,
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
            socketConsumer.setOnSendError {
                result = Result.failure(Throwable(it))
            }
            while (result == null) {
                delay(TIMEOUT)
            }
            socketConsumer.setOnSendError(null)
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