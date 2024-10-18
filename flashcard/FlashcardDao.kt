package com.example.flashcard

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcard.FlashcardEntity

@Dao
interface FlashcardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    suspend fun getFlashcardsForDeck(deckId: Long): List<FlashcardEntity>

    @Query("DELETE FROM flashcards WHERE id = :flashcardId")
    suspend fun deleteFlashcard(flashcardId: Long)
}