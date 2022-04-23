package confirm.email.data.repository

import confirm.email.data.Resource
import confirm.email.data.files.FilesManager
import confirm.email.data.model.LetterBox
import confirm.email.data.model.UILetter
import kotlinx.coroutines.delay

class LetterRepository(private val filesManager: FilesManager) {
    suspend fun loadLetters(name: String?): Resource<LetterBox> {
        return try {
            delay(400)
            if (name == null) {
                Resource.FailedResource(Throwable(""))
            } else {
                Resource.SuccessResource(filesManager.loadBox(name))
            }
        } catch (e: Exception) {
            Resource.FailedResource(e)
        }
    }

    suspend fun send(letter: UILetter): Resource<UILetter?> {
        return try {
            filesManager.saveLetter(letter.fromEmail, letter, true)
            Resource.SuccessResource(letter)
        } catch (e: Exception) {
            Resource.FailedResource(e)
        }
    }

}