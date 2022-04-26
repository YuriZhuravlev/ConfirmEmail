package confirm.email.data.files

import com.google.gson.Gson
import confirm.email.data.model.LetterBox
import confirm.email.data.model.UILetter
import kotlinx.coroutines.*
import java.io.File

class FilesManager(private val gson: Gson) {
    suspend fun loadBox(name: String): LetterBox {
        val inboxLetters = mutableListOf<UILetter>()
        val outboxLetters = mutableListOf<UILetter>()
        withContext(Dispatchers.IO) {
            val main = File(PATH + name)
            if (main.exists()) {
                val jobs = mutableListOf<Job>()
                val outbox = File(main, OUTBOX)
                if (outbox.exists()) {
                    outbox.listFiles().forEach { file ->
                        jobs.add(
                            launch {
                                try {
                                    val letter = gson.fromJson(file.readText(), UILetter::class.java)
                                    outboxLetters.add(letter)
                                } catch (e: Exception) {
                                    println(e.message)
                                }
                            }
                        )
                    }
                }
                val inbox = File(main, INBOX)
                if (inbox.exists()) {
                    inbox.listFiles().forEach { file ->
                        jobs.add(
                            launch {
                                try {
                                    val letter = gson.fromJson(file.readText(), UILetter::class.java)
                                    inboxLetters.add(letter)
                                } catch (e: Exception) {
                                    println(e.message)
                                }
                            }
                        )
                    }
                }
                jobs.joinAll()
            }
        }
        inboxLetters.sortByDescending { it.date }
        outboxLetters.sortByDescending { it.date }
        return LetterBox(inboxLetters, outboxLetters)
    }

    suspend fun saveLetter(name: String, letter: UILetter, outbox: Boolean) {
        withContext(Dispatchers.IO) {
            val folder = File(PATH + name, if (outbox) OUTBOX else INBOX)
            if (!folder.exists())
                folder.mkdirs()
            val file = File(folder, letter.uuid + POSTFIX)
            file.createNewFile()
            file.writeBytes(gson.toJson(letter).toByteArray())
        }
    }

    companion object {
        const val PATH = "boxes/"
        const val INBOX = "inbox/"
        const val OUTBOX = "outbox/"
        const val POSTFIX = ".lt"
    }
}