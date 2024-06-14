package com.openclassrooms.p8vitesse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

/**
 * Room interface
 */
@Dao
interface CandidateDao {

    /**
     * Insert a candidate in the data base
     * @return : Id of created candidate
     */
    @Insert
    suspend fun insertCandidate(candidate: CandidateDto): Long

/*
    /**
     * Return all candidate order by name
     */
    @Query("""
        SELECT *
        FROM tblCandidate
        ORDER BY lastName, firstName
         """)
    fun getAllCandidates(): Flow<List<CandidateDto>>
*/

    /**
     * Return candidate filter by favorite and by name
     * @param bFavoriteP : 0 = no favorite, 1 favorite, null all
     * @param sFilterName : null = no filter else a string to search candidate by name
     */
    @Query("""
        SELECT * FROM tblCandidate 
        WHERE (:bFavoriteP IS NULL OR topFavorite = :bFavoriteP) 
        AND (:sFilterName IS NULL OR firstName LIKE '%' || :sFilterName || '%' OR lastName LIKE '%' || :sFilterName || '%' )
        ORDER BY 
            lastName  COLLATE NOCASE, 
            firstName COLLATE NOCASE 
    """)  // NOCASE => permet d'ignorer la casse sinon les majuscule apparaissent avant les minuscule par défaut
    fun getCandidates ( bFavoriteP: Boolean? , sFilterName : String? ) : Flow<List<CandidateDto>>

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

    /**
     * Get a candidate by ID
     * @param lID : candidate ID
     */
    @Query("SELECT * FROM tblCandidate WHERE id = :lID")
    fun getCandidateById(lID: Long): Flow<CandidateDto>

}