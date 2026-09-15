package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRecipeDao {
    @Query("SELECT * FROM user_recipes ORDER BY updatedAt DESC")
    fun getAllUserRecipes(): Flow<List<UserRecipeEntity>>

    @Query("SELECT * FROM user_recipes WHERE uniqueId = :id LIMIT 1")
    fun getUserRecipeById(id: String): Flow<UserRecipeEntity?>

    @Query("SELECT * FROM user_recipes WHERE uniqueId = :id LIMIT 1")
    suspend fun getUserRecipeByIdSync(id: String): UserRecipeEntity?

    @Query("SELECT COUNT(*) FROM user_recipes")
    fun getUserRecipeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_recipes")
    suspend fun getUserRecipeCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRecipe(recipe: UserRecipeEntity)

    @Update
    suspend fun updateUserRecipe(recipe: UserRecipeEntity)

    @Query("DELETE FROM user_recipes WHERE uniqueId = :id")
    suspend fun deleteUserRecipe(id: String)

    @Query("UPDATE user_recipes SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE uniqueId = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean, updatedAt: Long = System.currentTimeMillis())
}
