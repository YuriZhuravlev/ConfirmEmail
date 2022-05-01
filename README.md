# ConfirmEmail
Реализация протокола электронной почты с подтверждением в рамках курса Криптографические методы защиты информации.

Применяется Jetpack Compose for Desktop, Ktor, Gson, javax.crypto. Для обмена информации используется [сервер с вебсокетом](https://github.com/YuriZhuravlev/ConfirmEmailServer)

### Основные классы для обработки протокола:
* ConfirmEmailProtocol.Outbox - класс, реализующий выполнение протокола со стороны отправителя (A).
* ConfirmEmailProtocol.Inbox - класс, реализующий выполнение протокола со стороны получателя (B).
* ProtocolProceed - класс, реализующий последовательное выполнение шагов протокола путем обмена ProtocolMessage. Принимает в качестве параметров обратные вызовы onSend, onResult, onError.
* ProtocolСonsumer - интерфейс предоставляющий обработку входящих сообщений и начало отправки исходящего письма. Его реализация обрабатывает текущие сессии выполнения протокола.

### Общая структура клиент-серверого приложения
![Общая структура клиент-сервера](https://user-images.githubusercontent.com/54802236/166137303-3e369057-1697-40de-8d9d-010e9afeb7c2.png)

### Общая структура обработки сообщений передаваемых по протоколу
![Обработка протокола](https://user-images.githubusercontent.com/54802236/166137337-887d6887-bb6e-4717-b792-6fc716ecc411.png)

### Примеры экранных форм
<img width="802" alt="LettersScreen" src="https://user-images.githubusercontent.com/54802236/166137339-f11e5b54-c428-4104-bb3b-f4fb02f5c7f7.png">
<img width="802" alt="LoginScreen" src="https://user-images.githubusercontent.com/54802236/166137343-862eab6c-9dff-4629-a1dc-004a0b446cd6.png">
<img width="803" alt="NewLetterScreen" src="https://user-images.githubusercontent.com/54802236/166137344-13c8c019-e2cc-4448-af7c-37067fe00d3f.png">



