package confirm.email.protocol

import confirm.email.utils.xor
import io.ktor.util.*
import java.io.PrintStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

sealed class ConfirmEmailProtocol(val uuid: String) {
    class Outbox(
        uuid: String,
        val message: String,
        val emptyMessage: String,
        private val countKeys: Int = 8,
        private val log: PrintStream? = null
    ) : ConfirmEmailProtocol(uuid) {
        private val prefix = "Outbox-$uuid:"

        init {
            log?.println("$prefix message=$message")
        }

        private val key = generateKey().encoded
        private val keys: List<Pair<ByteArray, ByteArray>> by lazy {
            List(countKeys) {
                val first = generateKey().encoded
                Pair(first, first.xor(key))
            }
        }
        private val sendingKeys = MutableList(countKeys) { 0 }
        private val encryptedMessage by lazy {
            encodeData(message.toByteArray(), key).encodeBase64()
        }

        private var encryptedPairs: List<Pair<ByteArray, ByteArray>>? = null
        private var inKeys: List<Pair<ByteArray?, ByteArray?>>? = null

        /**
         * 1) C = Ek(M)
         */
        fun encryptMessage() {
            log?.println("$prefix 1) key=${key.encodeBase64()}, encryptedMessage=$encryptedMessage")
        }

        /**
         * 2)
         */
        fun getEncryptMessage(): String {
            log?.println("$prefix 2) encryptedMessage=$encryptedMessage")
            return encryptedMessage
        }

        /**
         * 3) Генерирование 2n ключей
         */
        fun genKeys() {
            log?.println(
                "$prefix 3) emptyMessage=$emptyMessage, generate keys($countKeys)=${
                    keys.joinToString(prefix = "[", postfix = "]") {
                        "(${it.first.encodeBase64()},${it.second.encodeBase64()})"
                    }
                }")
        }

        /**
         * 4) Шифрование пустого сообщения 2n ключами
         */
        fun encryptedEmptyMessage(): List<Pair<String, String>> {
            val list = List(countKeys) {
                val data = emptyMessage.toByteArray()
                val pair = keys[it]
                Pair(
                    encodeData(data, pair.first).encodeBase64(),
                    encodeData(data, pair.second).encodeBase64()
                )
            }
            log?.println(
                "$prefix 4) encryptedEmptyMessage=${
                    list.joinToString(prefix = "[", postfix = "]") {
                        "(${it.first},${it.second})"
                    }
                }"
            )
            return list
        }

        /**
         * 6) Получение 2n зашифрованных квитанций
         */
        fun setEncryptedTickets(pairs: List<Pair<String, String>>) {
            log?.println(
                "$prefix 6) encryptedTickets=${
                    pairs.joinToString(prefix = "[", postfix = "]") {
                        "(${it.first},${it.second})"
                    }
                }"
            )
            encryptedPairs = pairs.map { it.first.decodeBase64Bytes() to it.second.decodeBase64Bytes() }
        }

        /**
         * 7) Отправка половины ключей, возможна распределенная передача, либо все сразу
         */
        fun getSupportKeys(): List<Pair<String?, String?>> {
            val list = List<Pair<String?, String?>>(countKeys) {
                if (Random.nextBoolean()) {
                    sendingKeys[it] = 1
                    Pair(keys[it].first.encodeBase64(), null)
                } else {
                    sendingKeys[it] = 2
                    Pair(null, keys[it].second.encodeBase64())
                }
            }
            log?.println("$prefix 7) supportKeys=${
                list.joinToString(prefix = "[", postfix = "]") {
                    "(${it.first},${it.second})"
                }
            }")
            return list
        }

        /**
         * 8) Получение половины ключей, возможна распределенная передача, либо все сразу
         */
        fun setSupportInKeys(list: List<Pair<String?, String?>>) {
            log?.println("$prefix 8) supportInKeys=${
                list.joinToString(prefix = "[", postfix = "]") {
                    "(${it.first},${it.second})"
                }
            }")
            inKeys = List(countKeys) {
                val pair = list[it]
                pair.first?.decodeBase64Bytes() to pair.second?.decodeBase64Bytes()
            }
        }

        /**
         * 9) Расшифровка всех квитанций, которые можно расшифровать
         */
        fun decryptingTickets(): Boolean {
            var success = true
            var first: ByteArray? = null
            var second: ByteArray? = null
            inKeys?.forEachIndexed { index, keyPair ->
                encryptedPairs?.get(index)?.let {
                    keyPair.first?.let { key ->
                        if (first != null)
                            success = success && first.contentEquals(decodeData(it.first, key))
                        else
                            first = decodeData(it.first, key)
                    }
                    keyPair.second?.let { key ->
                        if (second != null)
                            success = success && second.contentEquals(decodeData(it.second, key))
                        else
                            second = decodeData(it.second, key)
                    }
                }
            }
            log?.println("$prefix 9) success=$success, first=${first?.let { String(it) }}, second=${
                second?.let { String(it) }
            }")
            return success
        }

        /**
         * 11-12) Передача первых байтов 2n ключей (проще оперировать байтами, для 256-битного - 32)
         */
        fun getBytesSliceKeys(): List<String> {
            val bytes = List<String>(KEY_LENGTH / 8) { indexByte ->
                val array = ByteArray(2 * keys.size)
                keys.forEachIndexed { index, pair ->
                    array[2 * index] = pair.first[indexByte]
                    array[2 * index + 1] = pair.second[indexByte]
                }
                array.encodeBase64()
            }
            log?.println("$prefix 12) getBytesSliceKeys=$bytes")
            return bytes
        }

        /**
         * 11-12) Получение первых байтов 2n ключей
         */
        fun setBytesSliceInKeys(bytesList: List<String>) {
            log?.println("$prefix 11) setBytesSliceInKeys bytes=$bytesList")
            val bytesDecode = bytesList.map { it.decodeBase64Bytes() }
            val newKeys = List<Pair<ByteArray, ByteArray>>(countKeys) {
                Pair(ByteArray(KEY_LENGTH / 8), ByteArray(KEY_LENGTH / 8))
            }
            bytesDecode.forEachIndexed { indexByte, bytes ->
                newKeys.forEachIndexed { index, pair ->
                    pair.first[indexByte] = bytes[2 * index]
                    pair.second[indexByte] = bytes[2 * index + 1]
                }
            }
            inKeys = newKeys
        }

        /**
         * 13) Расшифровка оставшихся квитанций
         */
        fun decryptingTicketsFinally(): String? {
            var success = true
            var first: ByteArray? = null
            var second: ByteArray? = null
            inKeys?.forEachIndexed { index, keyPair ->
                encryptedPairs?.get(index)?.let {
                    keyPair.first?.let { key ->
                        if (first != null)
                            success = success && first.contentEquals(decodeData(it.first, key))
                        else
                            first = decodeData(it.first, key)
                    }
                    keyPair.second?.let { key ->
                        if (second != null)
                            success = success && second.contentEquals(decodeData(it.second, key))
                        else
                            second = decodeData(it.second, key)
                    }
                }
            }
            log?.println("$prefix 13) success=$success, first=${first?.let { String(it) }}, second=${
                second?.let { String(it) }
            }")
            return if (success) first?.let { String(it) } + second?.let { String(it) }
            else
                null
        }
    }

    class Inbox(
        val encryptedMessage: String,
        val ticket1: String,
        val ticket2: String,
        uuid: String,
        private val log: PrintStream? = null
    ) : ConfirmEmailProtocol(uuid) {
        private val prefix = "Inbox-$uuid:"
        private var countKeys: Int = 0
        private var keys: List<Pair<ByteArray, ByteArray>>? = null
        private val sendingKeys by lazy {
            MutableList(countKeys) { 0 }
        }

        private var inKeys: List<Pair<ByteArray?, ByteArray?>>? = null

        private var encryptedEmptyMessage: List<Pair<ByteArray, ByteArray>>? = null

        /**
         * 4) Получение зашифрованных пустых сообщений
         */
        fun setEncryptedEmptyMessage(list: List<Pair<String, String>>) {
            countKeys = list.size
            log?.println("$prefix 4) setEncryptedEmptyMessage($countKeys)=$list")
            encryptedEmptyMessage = list.map {
                it.first.decodeBase64Bytes() to it.second.decodeBase64Bytes()
            }
        }

        /**
         * 5) Генерация 2n ключей
         */
        fun genKeys() {
            keys = List(countKeys) {
                generateKey().encoded to generateKey().encoded
            }
            log?.println(
                "$prefix 5) genkeys=${
                    keys!!.joinToString(prefix = "[", postfix = "]") {
                        "(${it.first}, ${it.second})"
                    }
                }"
            )
        }

        /**
         * 6) Зашифрованные квитанции
         */
        fun getEncryptedTickets(): List<Pair<String, String>> {
            val first = ticket1.toByteArray()
            val second = ticket2.toByteArray()
            val list = keys!!.map { pair ->
                encodeData(first, pair.first).encodeBase64() to encodeData(second, pair.second).encodeBase64()
            }
            log?.println(
                "$prefix 6) getEncryptedTickets($ticket1, $ticket2)=${
                    list.joinToString(prefix = "[", postfix = "]") {
                        "(${it.first}, ${it.second})"
                    }
                }"
            )
            return list
        }

        /**
         * 7) Получение половины ключей, возможна распределенная передача, либо все сразу
         */
        fun setSupportInKeys(list: List<Pair<String?, String?>>) {
            log?.println("$prefix 7) supportInKeys=${
                list.joinToString(prefix = "[", postfix = "]") {
                    "(${it.first},${it.second})"
                }
            }")
            inKeys = List(countKeys) {
                val pair = list[it]
                pair.first?.decodeBase64Bytes() to pair.second?.decodeBase64Bytes()
            }
        }

        /**
         * 8) Отправка половины ключей, возможна распределенная передача, либо все сразу
         */
        fun getSupportKeys(): List<Pair<String?, String?>> {
            val list = List<Pair<String?, String?>>(countKeys) {
                if (Random.nextBoolean()) {
                    sendingKeys[it] = 1
                    Pair(keys!![it].first.encodeBase64(), null)
                } else {
                    sendingKeys[it] = 2
                    Pair(null, keys!![it].second.encodeBase64())
                }
            }
            log?.println("$prefix 8) supportKeys=${
                list.joinToString(prefix = "[", postfix = "]") {
                    "(${it.first},${it.second})"
                }
            }")
            return list
        }

        /**
         * 10) Расшифровка всех пустых сообщений, которые можно расшифровать
         */
        fun decryptingTickets(): Boolean {
            var success = true
            var first: ByteArray? = null
            var second: ByteArray? = null
            inKeys?.forEachIndexed { index, keyPair ->
                encryptedEmptyMessage?.get(index)?.let {
                    keyPair.first?.let { key ->
                        if (first != null) {
                            success = success && first.contentEquals(decodeData(it.first, key))
                        } else
                            first = decodeData(it.first, key)
                    }
                    keyPair.second?.let { key ->
                        if (second != null)
                            success = success && second.contentEquals(decodeData(it.second, key))
                        else
                            second = decodeData(it.second, key)
                    }
                }
            }
            log?.println("$prefix 10) success=$success, first=${first?.let { String(it) }}, second=${
                second?.let { String(it) }
            }")
            return success
        }

        /**
         * 11-12) Передача первых байтов 2n ключей (проще оперировать байтами, для 256-битного - 32)
         */
        fun getBytesSliceKeys(): List<String> {
            val bytes = List<String>(KEY_LENGTH / 8) { indexByte ->
                val array = ByteArray(2 * keys!!.size)
                keys!!.forEachIndexed { index, pair ->
                    array[2 * index] = pair.first[indexByte]
                    array[2 * index + 1] = pair.second[indexByte]
                }
                array.encodeBase64()
            }
            log?.println("$prefix 11) getBytesSliceKeys=$bytes")
            return bytes
        }

        /**
         * 11-12) Получение первых байтов 2n ключей
         */
        fun setBytesSliceInKeys(bytesList: List<String>) {
            log?.println("$prefix 12) setBytesSliceInKeys bytes=$bytesList")
            val bytesDecode = bytesList.map { it.decodeBase64Bytes() }
            val newKeys = List<Pair<ByteArray, ByteArray>>(countKeys) {
                Pair(ByteArray(KEY_LENGTH / 8), ByteArray(KEY_LENGTH / 8))
            }
            bytesDecode.forEachIndexed { indexByte, bytes ->
                newKeys.forEachIndexed { index, pair ->
                    pair.first[indexByte] = bytes[2 * index]
                    pair.second[indexByte] = bytes[2 * index + 1]
                }
            }
            inKeys = newKeys
        }


        /**
         * 14) Расшифровка оставшихся пустых сообщений
         */
        fun decryptionMessage(): String? {
            var success = true
            var first: ByteArray? = null
            var second: ByteArray? = null
            inKeys?.forEachIndexed { index, keyPair ->
                encryptedEmptyMessage?.get(index)?.let {
                    keyPair.first?.let { key ->
                        if (first != null)
                            success = success && first.contentEquals(decodeData(it.first, key))
                        else
                            first = decodeData(it.first, key)
                    }
                    keyPair.second?.let { key ->
                        if (second != null)
                            success = success && second.contentEquals(decodeData(it.second, key))
                        else
                            second = decodeData(it.second, key)
                    }
                }
            }
            log?.println("$prefix 14) success=$success, first=${first?.let { String(it) }}, second=${
                second?.let { String(it) }
            }")
            return if (success) {
                val resultKey = inKeys!!.first().let { it.first!!.xor(it.second!!) }
                val message = decodeData(encryptedMessage.decodeBase64Bytes(), resultKey)
                    .decodeToString()
                log?.println("$prefix resultKey=${resultKey.encodeBase64()}, message=$message")
                message
            } else
                null
        }
    }

    companion object {
        private const val CIPHER_NAME = "AES"
        private const val KEY_LENGTH = 128

        private val generator by lazy {
            KeyGenerator.getInstance("AES").apply {
                init(KEY_LENGTH)
            }
        }

        private fun generateKey() = generator.generateKey()
        private fun encodeData(data: ByteArray, key: ByteArray): ByteArray {
            val sks = SecretKeySpec(key, CIPHER_NAME)
            val c: Cipher = Cipher.getInstance(CIPHER_NAME)
            c.init(Cipher.ENCRYPT_MODE, sks)
            return c.doFinal(data)
        }

        private fun decodeData(data: ByteArray, key: ByteArray): ByteArray {
            val sks = SecretKeySpec(key, CIPHER_NAME)
            val c: Cipher = Cipher.getInstance(CIPHER_NAME)
            c.init(Cipher.DECRYPT_MODE, sks)
            return c.doFinal(data)
        }
    }
}