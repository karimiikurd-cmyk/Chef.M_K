package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PrepListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrepListDao {
    @Query("SELECT * FROM prep_lists ORDER BY updatedAt DESC")
    fun getAllPrepLists(): Flow<List<PrepListEntity>>

    @Query("SELECT * FROM prep_lists WHERE id = :id LIMIT 1")
    fun getPrepListById(id: Long): Flow<PrepListEntity?>

    @Query("SELECT * FROM prep_lists WHERE id = :id LIMIT 1")
    suspend fun getPrepListByIdSync(id: Long): PrepListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrepList(prepList: PrepListEntity): Long

    @Update
    suspend fun updatePrepList(prepList: PrepListEntity)

    @Query("DELETE FROM prep_lists WHERE id = :id")
    suspend fun deletePrepList(id: Long)
}
