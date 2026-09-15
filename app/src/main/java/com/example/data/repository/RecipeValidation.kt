package com.example.data.repository

import com.example.data.model.IngredientItem
import com.example.data.model.UserRecipeEntity

enum class ValidationStatus {
    GREEN,  // Valid & complete
    YELLOW, // Valid with warnings/incomplete non-critical fields
    RED     // Fatal error, cannot be saved
}

data class ValidationResult(
    val status: ValidationStatus,
    val summaryMessage: String,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

object RecipeValidation {

    fun validateUserRecipe(
        recipe: UserRecipeEntity,
        ingredients: List<IngredientItem>,
        steps: List<String>,
        existingIds: Set<String>,
        isEditingExisting: Boolean = false
    ): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        // Critical Checks (RED)
        if (recipe.name.trim().isBlank()) {
            errors.add("نام رسپی مشخص نشده است.")
        }
        if (recipe.uniqueId.trim().isBlank()) {
            errors.add("شناسه اختصاصی (ID) رسپی نمی‌تواند خالی باشد.")
        } else if (!isEditingExisting && existingIds.contains(recipe.uniqueId.trim())) {
            errors.add("شناسه '${recipe.uniqueId}' قبلاً ثبت شده و تکراری است. لطفاً شناسه یکتای دیگری وارد کنید.")
        }
        if (recipe.category.trim().isBlank() || recipe.category == "ALL") {
            errors.add("دسته‌بندی اصلی محصول انتخاب نشده است.")
        }
        if (ingredients.isEmpty()) {
            errors.add("حداقل باید یک ماده اولیه با مقدار مشخص ثبت گردد.")
        } else {
            ingredients.forEachIndexed { idx, ing ->
                if (ing.name.trim().isBlank()) {
                    errors.add("ماده اولیه شماره ${idx + 1} بدون نام است.")
                }
                if (ing.amount <= 0.0) {
                    errors.add("مقدار ماده اولیه '${ing.name.ifBlank { (idx + 1).toString() }}' باید عددی بزرگتر از صفر باشد.")
                }
                if (ing.unit.trim().isBlank()) {
                    errors.add("واحد اندازه‌گیری برای '${ing.name.ifBlank { (idx + 1).toString() }}' مشخص نشده است.")
                }
            }
        }
        if (steps.isEmpty() || steps.all { it.trim().isBlank() }) {
            errors.add("حداقل یک مرحله تفصیلی آماده‌سازی و پخت باید تعریف گردد.")
        }

        if (recipe.baseWeight <= 0.0) {
            errors.add("وزن پایه بچ تولیدی باید مقداری مثبت باشد.")
        }

        if (errors.isNotEmpty()) {
            return ValidationResult(
                status = ValidationStatus.RED,
                summaryMessage = "رسپی دارای خطای ضروری است و قابل ذخیره نیست.",
                errors = errors,
                warnings = warnings
            )
        }

        // Warnings / Incomplete Non-Critical Fields (YELLOW)
        if (recipe.shortDescription.trim().isBlank()) {
            warnings.add("توضیح کوتاه و توصیف مشخصات ویترینی نوشته نشده است.")
        }
        if (recipe.mainProduct.trim().isBlank()) {
            warnings.add("برش پایه گوشت یا پروتئین اصلی مشخص نشده است.")
        }
        if (recipe.preparationTime.trim().isBlank()) {
            warnings.add("مدت زمان آماده‌سازی کارگاهی ذکر نشده است.")
        }
        if (recipe.marinationTime.trim().isBlank() && (recipe.category == "CHICKEN_JOOJEH" || recipe.category == "KEBAB" || recipe.category == "STEAK")) {
            warnings.add("مدت زمان استراحت در مرینیت برای این دسته محصول ذکر نشده است.")
        }
        if (recipe.cookingTemperature.trim().isBlank() && (recipe.category == "STEAK" || recipe.category == "BURGER" || recipe.category == "KEBAB")) {
            warnings.add("دمای استاندارد یا وضعیت زغال مشخص نشده است.")
        }
        if (recipe.storageMethod.trim().isBlank()) {
            warnings.add("روش و شرایط دمایی نگهداری در سردخانه ثبت نشده است.")
        }
        if (steps.size < 2) {
            warnings.add("دستور آماده‌سازی فقط دارای یک مرحله است؛ تفکیک چندمرحله‌ای برای کارگاه توصیه می‌شود.")
        }

        return if (warnings.isNotEmpty()) {
            ValidationResult(
                status = ValidationStatus.YELLOW,
                summaryMessage = "رسپی ذخیره می‌شود، اما چند بخش ناقص است.",
                errors = emptyList(),
                warnings = warnings
            )
        } else {
            ValidationResult(
                status = ValidationStatus.GREEN,
                summaryMessage = "رسپی کامل و آماده ذخیره است.",
                errors = emptyList(),
                warnings = emptyList()
            )
        }
    }
}
