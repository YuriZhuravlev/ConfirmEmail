package confirm.email.data.repository

import confirm.email.data.Resource
import confirm.email.data.model.LetterBox
import confirm.email.data.model.UILetter
import java.util.*

class LetterRepository {
    suspend fun loadLetters(name: String?): Resource<LetterBox> {
        return try {
            if (name == null) {
                Resource.FailedResource(Throwable(""))
            } else {
                Resource.SuccessResource(
                    LetterBox(
                        listOf(
                            UILetter(
                                "User1",
                                name,
                                "User2",
                                "toemail@asd.asd",
                                Date(),
                                "Title my letter",
                                "Yext my letter:\nDear friend,\n\nSomething Lore, Ipsum"
                            ),
                            UILetter(
                                "User1",
                                name,
                                "User2",
                                "toemail@asd.asd",
                                Date(),
                                "Title my letter2 long title_gg_vp, yes",
                                "Yext my letter2:\nDear friend,\n\nSomething Lore, Ipsum"
                            )
                        ),
                        listOf(
                            UILetter(
                                "User2",
                                name,
                                "User1",
                                "fromemail@asd.asd",
                                Date(),
                                "Title your letter",
                                "Yext your letter:\nDear friend,\n\nSomething Lorem Ipsum"
                            )
                        )
                    )
                )
            }
        } catch (e: Exception) {
            Resource.FailedResource(e)
        }
    }

}