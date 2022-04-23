package confirm.email.protocol

sealed class ConfirmEmailProtocol {
    class Inbox : ConfirmEmailProtocol() {

    }

    class Outbox : ConfirmEmailProtocol() {

    }
}