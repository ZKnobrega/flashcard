package com.example.flashcard.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cartas",
    foreignKeys = [ForeignKey(
        entity = Baralho::class,
        parentColumns = ["id"],
        childColumns = ["baralhoId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Carta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val frente: String,
    val verso: String,
    val baralhoId: Int // Relaciona com o Baralho
)