package confirm.email.data.repository

import confirm.email.data.Resource
import confirm.email.data.model.UILetter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object LetterRepository {
    private val _letters = MutableStateFlow(Resource.LoadingResource<List<UILetter>>())
    val letters = _letters.asStateFlow()
}