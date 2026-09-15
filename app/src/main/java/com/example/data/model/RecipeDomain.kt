package com.example.data.model

data class IngredientItem(
    val name: String,
    val amount: Double,
    val unit: String,
    val isScalable: Boolean = true
)

data class Recipe(
    val uniqueId: String,
    val name: String,
    val category: RecipeCategory,
    val categoryNameFa: String,
    val shortDescription: String,
    val mainProduct: String,
    val baseWeight: Double,
    val ingredients: List<IngredientItem>,
    val preparationSteps: List<String>,
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
    val searchTags: List<String>,
    val foodSafetyAlert: String,
    val extraDetailsMap: Map<String, String>,
    val relatedSauceIds: List<String>,
    val relatedSpiceIds: List<String>,
    val relatedSaladIds: List<String>,
    val isFavorite: Boolean = false,
    val userNotes: String = ""
) {
    fun scaleForWeight(targetWeightGrams: Double): List<IngredientItem> {
        val factor = if (baseWeight > 0) targetWeightGrams / baseWeight else 1.0
        return ingredients.map { ing ->
            if (ing.isScalable) {
                ing.copy(amount = (ing.amount * factor * 10.0).toLong() / 10.0)
            } else {
                ing
            }
        }
    }
}

enum class RecipeCategory(val id: String, val titleFa: String, val iconName: String) {
    ALL("ALL", "همه محصولات", "AllInclusive"),
    CHICKEN_JOOJEH("CHICKEN_JOOJEH", "مرغ و جوجه کباب", "Egg"),
    KEBAB("KEBAB", "کباب‌های تخصصی", "OutdoorGrill"),
    STEAK("STEAK", "استیک‌های پرایم", "Restaurant"),
    BURGER("BURGER", "برگرهای دست‌ساز", "LunchDining"),
    SAUCE("SAUCE", "سس‌های تخصصی", "Liquor"),
    SPICE("SPICE", "ادویه‌جات ترکیبی", "Grain"),
    COMPOUND_BUTTER("COMPOUND_BUTTER", "کره‌های مرکب", "Cookie"),
    INFUSED_OIL("INFUSED_OIL", "روغن‌های طعم‌دار", "Opacity"),
    SALAD("SALAD", "سالادهای همراه", "LocalFlorist"),
    APPETIZER("APPETIZER", "پیش‌غذاهای گرم/سرد", "TapAndPlay"),
    FINGER_FOOD("FINGER_FOOD", "فینگرفود مجالس", "BakeryDining"),
    DESSERT("DESSERT", "دسرهای کانتر", "Icecream");

    companion object {
        fun fromId(id: String): RecipeCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: ALL
        }
    }
}
