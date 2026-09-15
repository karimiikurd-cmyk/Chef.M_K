package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipesSync(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY name ASC")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<RecipeEntity>>

    @Query("""
        SELECT * FROM recipes 
        WHERE name LIKE '%' || :query || '%' 
           OR mainProduct LIKE '%' || :query || '%' 
           OR shortDescription LIKE '%' || :query || '%'
           OR searchTagsJson LIKE '%' || :query || '%'
           OR categoryNameFa LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE uniqueId = :uniqueId LIMIT 1")
    fun getRecipeById(uniqueId: String): Flow<RecipeEntity?>

    @Query("SELECT * FROM recipes WHERE uniqueId = :uniqueId LIMIT 1")
    suspend fun getRecipeByIdSync(uniqueId: String): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE uniqueId = :uniqueId")
    suspend fun updateFavorite(uniqueId: String, isFavorite: Boolean)

    @Query("UPDATE recipes SET userNotes = :notes WHERE uniqueId = :uniqueId")
    suspend fun updateUserNotes(uniqueId: String, notes: String)

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getCount(): Int
}
