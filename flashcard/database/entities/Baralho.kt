package com.example.flashcard.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baralhos")
data class Baralho(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String
)
