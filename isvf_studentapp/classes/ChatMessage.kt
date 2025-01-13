package es.igs.android.classes

import java.time.Instant

data class ChatMessage(
    val username: String = "",
    val message: String = "",
    val userUID: String,
    val timestamp: Instant = Instant.now()
)