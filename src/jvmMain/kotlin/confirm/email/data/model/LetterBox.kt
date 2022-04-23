package confirm.email.data.model

data class LetterBox(
    val inbox: List<UILetter>,
    val outbox: List<UILetter>
) {
    fun isEmpty() = inbox.isEmpty() || outbox.isEmpty()
}