package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipes",
    indices = [
        Index(value = ["category"]),
        Index(value = ["name"]),
        Index(value = ["isFavorite"])
    ]
)
data class RecipeEntity(
    @PrimaryKey val uniqueId: String,
    val name: String,
    val category: String,
    val categoryNameFa: String,
    val shortDescription: String,
    val mainProduct: String,
    val baseWeight: Double,
    val ingredientsJson: String,
    val preparationStepsJson: String,
    val preparationTime: String,
    val marinationTime: String,
    val cookingTime: String,
    val cookingTemperature: String,
    val cookingMethod: String,
    val professionalTips: String,
    val commonMistakes: String,
    val storageInstructions: String,
    val recommendedStorageTime: String,
    val storeUsage: String,
    val searchTagsJson: String,
    val foodSafetyAlert: String,
    val extraDetailsJson: String,
    val relatedSauceIdsJson: String,
    val relatedSpiceIdsJson: String,
    val relatedSaladIdsJson: String,
    val isFavorite: Boolean = false,
    val userNotes: String = ""
)
