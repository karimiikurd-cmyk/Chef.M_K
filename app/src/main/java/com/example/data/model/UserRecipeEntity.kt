package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_recipes",
    indices = [
        Index(value = ["category"]),
        Index(value = ["name"]),
        Index(value = ["isFavorite"])
    ]
)
data class UserRecipeEntity(
    @PrimaryKey val uniqueId: String,
    val name: String,
    val category: String,
    val subcategory: String = "",
    val shortDescription: String = "",
    val mainProduct: String = "",
    val sourceType: String = "My Recipe", // "Chef.M_K Original", "My Recipe", "Imported", "Test Recipe"
    val baseWeight: Double = 1000.0,
    val expectedFinalWeight: Double = 0.0,
    val portionWeight: Double = 0.0,
    val ingredientsJson: String = "[]",
    val preparationStepsJson: String = "[]",
    val preparationTime: String = "",
    val marinationTime: String = "",
    val cookingTime: String = "",
    val cookingTemperature: String = "",
    val cookingMethod: String = "",
    val equipment: String = "",
    val storageMethod: String = "",
    val storageTemperature: String = "",
    val storageDuration: String = "",
    val packagingNotes: String = "",
    val foodSafetyNotes: String = "",
    val professionalTips: String = "",
    val commonMistakes: String = "",
    val storeUsage: String = "",
    val searchTagsJson: String = "[]",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
