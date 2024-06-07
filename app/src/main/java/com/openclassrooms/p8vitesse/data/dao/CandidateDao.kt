package com.openclassrooms.p8vitesse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

    /**
     * Insert a candidate in the data base
     * @return : Id of created candidate
     */
    @Insert
    suspend fun insertCandidate(candidate: CandidateDto): Long

    /**
     * Return candidate filter by favorite or not
     * @param nTopFavorite : 0 = no favorite, 1 favorite
     */
    @Query("SELECT * FROM tblCandidate WHERE (topFavorite = :nTopFavorite)")
    fun getCandidatesFilterByFavorite(nTopFavorite : Integer): Flow<List<CandidateDto>>

    /**
     * Return all candidate
     */
    @Query("SELECT * FROM tblCandidate")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    /**
     * Update a candidate
     * @return : number of lines updated
     */
    @Update
    suspend fun updateCandidate(candidate: CandidateDto): Int

    /**
     * Delete a candidate
     */
    @Query("DELETE FROM tblCandidate WHERE id = :id")
    suspend fun deleteCandidateById(id: Long)

}