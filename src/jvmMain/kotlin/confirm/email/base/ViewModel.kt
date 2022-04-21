package confirm.email.base

import kotlinx.coroutines.*

open class ViewModel() {
    protected val viewModelScope = MainScope() + CoroutineName("$javaClass") + Dispatchers.IO
    fun close() {
        viewModelScope.cancel(EndViewModel())
    }

    inner class EndViewModel() : CancellationException()
}