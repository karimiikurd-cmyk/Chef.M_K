package com.example

import com.example.data.model.IngredientItem
import com.example.data.model.MeatCategory
import com.example.data.model.PrepList
import com.example.data.model.PrepListItem
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.data.repository.MeatKnowledgeData
import com.example.data.repository.PrepListCalculator
import com.example.data.repository.RecipeValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `test MeatKnowledgeData contains cuts for all protein categories`() {
        val cuts = MeatKnowledgeData.allCuts
        assertTrue("Must have rich catalog of cuts", cuts.size >= 15)

        val categories = cuts.map { it.category }.toSet()
        assertTrue("Must include Lamb cuts", categories.contains(MeatCategory.LAMB))
        assertTrue("Must include Beef cuts", categories.contains(MeatCategory.BEEF))
        assertTrue("Must include Poultry cuts", categories.contains(MeatCategory.POULTRY))
        assertTrue("Must include Fish cuts", categories.contains(MeatCategory.FISH))

        for (cut in cuts) {
            assertTrue("Cut name in Persian cannot be blank", cut.nameFa.isNotBlank())
            assertTrue("Best uses cannot be empty", cut.bestUsesFa.isNotEmpty())
            assertTrue("Butchery tips cannot be blank", cut.butcheryTipsFa.isNotBlank())
            assertTrue("Tenderness score must be valid", cut.tendernessScore in 1..5)
        }
    }

    @Test
    fun `test PrepListCalculator aggregates ingredients accurately`() {
        val sampleRecipe1 = Recipe(
            uniqueId = "R1",
            name = "جوجه کباب زعفرانی",
            category = RecipeCategory.CHICKEN_JOOJEH,
            categoryNameFa = "جوجه کباب",
            shortDescription = "تست ۱",
            mainProduct = "سینه مرغ",
            baseWeight = 1000.0,
            ingredients = listOf(
                IngredientItem("سینه مرغ", 1000.0, "گرم", true),
                IngredientItem("پیاز", 200.0, "گرم", true),
                IngredientItem("زعفران", 5.0, "گرم", true)
            ),
            preparationSteps = listOf("مرحله اول: آماده‌سازی گوشت", "مرحله دوم: مرینیت"),
            preparationTime = "20 دقیقه",
            marinationTime = "4 ساعت",
            cookingTime = "15 دقیقه",
            cookingTemperature = "متوسط",
            cookingMethod = "کباب پز",
            professionalTips = "نکته تست",
            commonMistakes = "اشتباه تست",
            storageInstructions = "در یخچال",
            recommendedStorageTime = "3 روز",
            storeUsage = "ویترین تازه",
            searchTags = listOf("جوجه", "زعفرانی"),
            foodSafetyAlert = "زنجیره سرد رعایت شود"
        )

        val sampleRecipe2 = Recipe(
            uniqueId = "R2",
            name = "کباب برگ گوسفندی",
            category = RecipeCategory.KEBAB,
            categoryNameFa = "کباب",
            shortDescription = "تست ۲",
            mainProduct = "راسته گوسفندی",
            baseWeight = 1000.0,
            ingredients = listOf(
                IngredientItem("راسته گوسفندی", 1000.0, "گرم", true),
                IngredientItem("پیاز", 150.0, "گرم", true),
                IngredientItem("زعفران", 3.0, "گرم", true)
            ),
            preparationSteps = listOf("مرحله اول: بیات‌سازی گوشت", "مرحله دوم: سیخ گرفتن"),
            preparationTime = "25 دقیقه",
            marinationTime = "12 ساعت",
            cookingTime = "10 دقیقه",
            cookingTemperature = "بالا",
            cookingMethod = "منقل زغالی",
            professionalTips = "نکته تست",
            commonMistakes = "اشتباه تست",
            storageInstructions = "در یخچال",
            recommendedStorageTime = "2 روز",
            storeUsage = "سفارش ویژه",
            searchTags = listOf("برگ", "گوسفندی"),
            foodSafetyAlert = "زنجیره سرد رعایت شود"
        )

        val prepList = PrepList(
            title = "تولید روزانه فرضی",
            items = listOf(
                PrepListItem(recipeId = "R1", targetWeightKg = 10.0), // 10kg R1: Onion 2000g, Saffron 50g
                PrepListItem(recipeId = "R2", targetWeightKg = 5.0)   // 5kg R2: Onion 750g, Saffron 15g
            )
        )

        val plan = PrepListCalculator.calculatePrepPlan(
            prepList,
            mapOf("R1" to sampleRecipe1, "R2" to sampleRecipe2)
        )

        assertEquals("Total planned batches", 2, plan.productionPlans.size)
        assertEquals("Total weight should be 15.0 kg", 15.0, plan.totalTargetWeightKg, 0.001)

        val onion = plan.aggregatedIngredients.find { it.name.contains("پیاز") }
        assertNotNull("Onion should be aggregated", onion)
        assertEquals("Total onion should be 2750g (2.75kg)", 2.75, onion!!.totalAmount, 0.01)

        val saffron = plan.aggregatedIngredients.find { it.name.contains("زعفران") }
        assertNotNull("Saffron should be aggregated", saffron)
        assertEquals("Total saffron should be 65g", 65.0, saffron!!.totalAmount, 0.01)
    }

    @Test
    fun `test RecipeValidation flags empty or low-quality recipes`() {
        val emptyResult = RecipeValidation.validateUserRecipe(
            name = "",
            category = "chicken_joojeh",
            shortDescription = "",
            mainProduct = "",
            baseWeightKg = 0.0,
            ingredients = emptyList(),
            steps = emptyList()
        )
        assertFalse("Empty recipe must fail validation", emptyResult.isValid)
        assertTrue("Must flag missing title", emptyResult.errors.any { it.contains("عنوان") })
        assertTrue("Must flag missing ingredients", emptyResult.errors.any { it.contains("ماده اولیه") })
        assertTrue("Must flag missing steps", emptyResult.errors.any { it.contains("مراحل") })
    }
}
