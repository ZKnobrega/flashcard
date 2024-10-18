package com.example.flashcard

import androidx.room.*

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    suspend fun getAllDecks(): List<DeckEntity>

    @Insert
    suspend fun insertDeck(deck: DeckEntity): Long

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)
}
