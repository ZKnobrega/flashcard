package com.example.flashcard.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.flashcard.database.entities.Baralho

@Dao
interface BaralhoDao {
    @Insert
    suspend fun inserirBaralho(baralho: Baralho): Long

    @Query("SELECT * FROM baralhos")
    suspend fun listarBaralhos(): List<Baralho>

    @Query("DELETE FROM baralhos WHERE id = :baralhoId")
    suspend fun deletarBaralho(baralhoId: Int)
}
