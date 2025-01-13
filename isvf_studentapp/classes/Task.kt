package es.efb.isvf_studentapp.classes

import java.util.UUID

data class Task(
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val id: UUID = UUID.randomUUID()
)
