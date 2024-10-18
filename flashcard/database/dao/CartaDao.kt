package com.example.flashcard.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.flashcard.database.entities.Carta

@Dao
interface CartaDao {
    @Insert
    suspend fun inserirCarta(carta: Carta): Long

    @Query("SELECT * FROM cartas WHERE baralhoId = :baralhoId")
    suspend fun listarCartas(baralhoId: Int): List<Carta>

    @Query("DELETE FROM cartas WHERE baralhoId = :baralhoId")
    suspend fun deletarCartasPorBaralho(baralhoId: Int)
}