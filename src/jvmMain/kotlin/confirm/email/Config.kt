package confirm.email

import confirm.email.protocol.ProtocolProceed

var enableLogging = false
var countKey = 8

fun logger() = if (enableLogging) ProtocolProceed.defaultLogger() else null