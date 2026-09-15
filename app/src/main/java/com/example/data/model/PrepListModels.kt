package com.example.data.model

data class PrepListItem(
    val recipeId: String,
    val recipeName: String,
    val isUserRecipe: Boolean = false,
    val targetWeightKg: Double = 1.0,
    val notes: String = ""
)

data class PrepList(
    val id: Long = 0,
    val title: String,
    val items: List<PrepListItem> = emptyList(),
    val completedTasks: Set<String> = emptySet(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class AggregatedIngredient(
    val name: String,
    val totalAmount: Double,
    val unit: String,
    val contributingRecipes: List<String>
)

data class PrepTask(
    val id: String,
    val recipeName: String,
    val stageTitle: String,
    val instructions: String,
    val category: String, // e.g., "برش و آماده‌سازی", "مرینیت و استراحت", "پخت و فرآوری", "بسته‌بندی"
    val isCompleted: Boolean = false
)

data class RecipeProductionPlan(
    val recipeId: String,
    val recipeName: String,
    val targetWeightKg: Double,
    val scaledIngredients: List<IngredientItem>,
    val preparationTime: String,
    val marinationTime: String,
    val cookingTime: String,
    val cookingTemperature: String,
    val cookingMethod: String,
    val storageInstructions: String,
    val notes: String
)

data class CalculatedPrepPlan(
    val prepList: PrepList,
    val plans: List<RecipeProductionPlan>,
    val aggregatedIngredients: List<AggregatedIngredient>,
    val tasks: List<PrepTask>
)
