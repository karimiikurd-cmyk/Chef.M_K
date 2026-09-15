package com.example.data.repository

import com.example.data.model.AggregatedIngredient
import com.example.data.model.CalculatedPrepPlan
import com.example.data.model.IngredientItem
import com.example.data.model.PrepList
import com.example.data.model.PrepTask
import com.example.data.model.Recipe
import com.example.data.model.RecipeProductionPlan
import java.util.Locale

object PrepListCalculator {

    fun calculatePrepPlan(
        prepList: PrepList,
        recipesMap: Map<String, Recipe>
    ): CalculatedPrepPlan {
        val productionPlans = mutableListOf<RecipeProductionPlan>()
        val allScaledIngredients = mutableListOf<Pair<String, IngredientItem>>() // (RecipeName, Ingredient)
        val allTasks = mutableListOf<PrepTask>()

        for (item in prepList.items) {
            val recipe = recipesMap[item.recipeId] ?: continue
            val targetGrams = item.targetWeightKg * 1000.0
            val scaledIngredients = recipe.scaleForWeight(targetGrams)

            productionPlans.add(
                RecipeProductionPlan(
                    recipeId = item.recipeId,
                    recipeName = item.recipeName.ifBlank { recipe.name },
                    targetWeightKg = item.targetWeightKg,
                    scaledIngredients = scaledIngredients,
                    preparationTime = recipe.preparationTime,
                    marinationTime = recipe.marinationTime,
                    cookingTime = recipe.cookingTime,
                    cookingTemperature = recipe.cookingTemperature,
                    cookingMethod = recipe.cookingMethod,
                    storageInstructions = recipe.storageInstructions,
                    notes = item.notes
                )
            )

            for (ing in scaledIngredients) {
                allScaledIngredients.add(Pair(item.recipeName.ifBlank { recipe.name }, ing))
            }

            // Generate structured kitchen tasks for this recipe
            val steps = recipe.preparationSteps
            if (steps.isNotEmpty()) {
                steps.forEachIndexed { index, stepText ->
                    val colonIndex = stepText.indexOf(':')
                    val stageTitle = if (colonIndex > 0 && colonIndex < 80) {
                        stepText.substring(0, colonIndex).trim()
                    } else {
                        "مرحله ${index + 1}"
                    }
                    val body = if (colonIndex > 0 && colonIndex < 80) {
                        stepText.substring(colonIndex + 1).trim()
                    } else {
                        stepText
                    }

                    val taskCategory = when {
                        index == 0 || stageTitle.contains("برش") || stageTitle.contains("آماده‌سازی") -> "برش و قصابی"
                        stageTitle.contains("چاشنی") || stageTitle.contains("مرینیت") || stageTitle.contains("توالی") -> "طعم‌دار کردن"
                        stageTitle.contains("استراحت") || stageTitle.contains("ورز") -> "ورز و استراحت"
                        stageTitle.contains("سیخ") || stageTitle.contains("پخت") || stageTitle.contains("دما") -> "فرآوری و پخت"
                        else -> "کنترل کیفی و بسته‌بندی"
                    }

                    val taskId = "${item.recipeId}_step_$index"
                    allTasks.add(
                        PrepTask(
                            id = taskId,
                            recipeName = item.recipeName.ifBlank { recipe.name },
                            stageTitle = stageTitle,
                            instructions = body,
                            category = taskCategory,
                            isCompleted = prepList.completedTasks.contains(taskId)
                        )
                    )
                }
            } else {
                // Fallback default task if no detailed steps
                val taskId = "${item.recipeId}_default_prep"
                allTasks.add(
                    PrepTask(
                        id = taskId,
                        recipeName = item.recipeName.ifBlank { recipe.name },
                        stageTitle = "تدارکات و آماده‌سازی بچ ${item.targetWeightKg} کیلوگرم",
                        instructions = "آماده‌سازی مواد اولیه و فرآوری طبق دستورالعمل استاندارد کارگاهی",
                        category = "فرآوری کلی",
                        isCompleted = prepList.completedTasks.contains(taskId)
                    )
                )
            }
        }

        // Aggregate shared ingredients
        val aggregatedList = aggregateIngredients(allScaledIngredients)

        return CalculatedPrepPlan(
            prepList = prepList,
            plans = productionPlans,
            aggregatedIngredients = aggregatedList,
            tasks = allTasks
        )
    }

    private fun aggregateIngredients(
        items: List<Pair<String, IngredientItem>>
    ): List<AggregatedIngredient> {
        val grouped = mutableMapOf<String, MutableList<Pair<String, IngredientItem>>>()

        for (pair in items) {
            val normalizedKey = normalizeIngredientName(pair.second.name)
            val list = grouped.getOrPut(normalizedKey) { mutableListOf() }
            list.add(pair)
        }

        val result = mutableListOf<AggregatedIngredient>()

        for ((_, group) in grouped) {
            val displayName = group.first().second.name.trim()
            val contributingRecipes = group.map { "${it.first} (${formatAmount(it.second.amount)} ${it.second.unit})" }.distinct()

            // Group by unit compatibility
            val byUnit = group.groupBy { normalizeUnit(it.second.unit) }

            for ((unitGroup, unitItems) in byUnit) {
                var totalAmount = 0.0
                for (item in unitItems) {
                    val rawUnit = item.second.unit.trim().lowercase()
                    val amount = item.second.amount
                    totalAmount += when {
                        rawUnit == "کیلوگرم" || rawUnit == "کیلو" || rawUnit == "kg" -> amount * 1000.0
                        rawUnit == "لیتر" || rawUnit == "l" || rawUnit == "lit" -> amount * 1000.0
                        else -> amount
                    }
                }

                val finalUnit: String
                val finalAmount: Double
                when (unitGroup) {
                    "WEIGHT" -> {
                        if (totalAmount >= 1000.0) {
                            finalAmount = roundToOneDecimal(totalAmount / 1000.0)
                            finalUnit = "کیلوگرم"
                        } else {
                            finalAmount = roundToOneDecimal(totalAmount)
                            finalUnit = "گرم"
                        }
                    }
                    "VOLUME" -> {
                        if (totalAmount >= 1000.0) {
                            finalAmount = roundToOneDecimal(totalAmount / 1000.0)
                            finalUnit = "لیتر"
                        } else {
                            finalAmount = roundToOneDecimal(totalAmount)
                            finalUnit = "میلی‌لیتر"
                        }
                    }
                    else -> {
                        finalAmount = roundToOneDecimal(totalAmount)
                        finalUnit = unitItems.first().second.unit
                    }
                }

                result.add(
                    AggregatedIngredient(
                        name = displayName,
                        totalAmount = finalAmount,
                        unit = finalUnit,
                        contributingRecipes = contributingRecipes
                    )
                )
            }
        }

        return result.sortedByDescending { it.totalAmount }
    }

    private fun normalizeIngredientName(raw: String): String {
        return raw.trim().lowercase()
            .replace("  ", " ")
            .replace("ی", "ی")
            .replace("ك", "ک")
            .replace("ـ", "")
            .trim()
    }

    private fun normalizeUnit(unit: String): String {
        val u = unit.trim().lowercase()
        return when {
            u in listOf("گرم", "کیلوگرم", "کیلو", "g", "kg") -> "WEIGHT"
            u in listOf("میلی‌لیتر", "لیتر", "cc", "ml", "l", "lit") -> "VOLUME"
            u in listOf("عدد", "دانه", "شاخه", "حبه", "بوته", "بند") -> "COUNT"
            u in listOf("قاشق غذاخوری", "قاشق سوپ‌خوری", "قاشق غذا خوری", "قاشق مرباخوری", "قاشق چایخوری", "قاشق چای‌خوری", "پیمانه") -> "SPOON_CUP"
            else -> u
        }
    }

    private fun roundToOneDecimal(value: Double): Double {
        return (value * 10.0).toLong() / 10.0
    }

    private fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toLong().toString()
        } else {
            String.format(Locale.US, "%.1f", amount)
        }
    }
}
