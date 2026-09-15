package com.example.data.model

enum class MeatCategory(val id: String, val titleFa: String) {
    ALL("ALL", "همه برش‌ها"),
    BEEF("BEEF", "گاو و گوساله"),
    LAMB("LAMB", "گوسفند و بره"),
    POULTRY("POULTRY", "ماکیان و مرغ"),
    FISH_SEAFOOD("FISH_SEAFOOD", "ماهی و آبزیان"),
    OTHER_PROTEINS("OTHER_PROTEINS", "سایر پروتئین‌ها");

    companion object {
        fun fromId(id: String): MeatCategory =
            entries.find { it.id.equals(id, ignoreCase = true) } ?: ALL
    }
}

data class MeatCut(
    val id: String,
    val persianName: String,
    val englishName: String,
    val category: MeatCategory,
    val animal: String,
    val anatomicalLocation: String,
    val sourceOrigin: String,
    val shapeCharacteristics: String,
    val fatLevel: String,
    val texture: String,
    val muscleStructure: String,
    val recommendedThickness: String,
    val grainDirection: String,
    val trimmingInstructions: String,
    val suitableCookingMethods: String,
    val suitableGrillingMethods: String,
    val suitableMarinades: String,
    val suitableSpiceBlends: String,
    val suitableSauces: String,
    val recommendedDoneness: String,
    val commonMistakes: String,
    val proTips: String,
    val storageGuidance: String,
    val relatedRecipeKeywords: List<String>
) {
    val nameFa: String get() = persianName
    val nameEn: String get() = englishName
    val fatPercentage: String get() = fatLevel
    val tendernessScore: Int
        get() = when {
            persianName.contains("فیله") || persianName.contains("تندرلویین") -> 5
            persianName.contains("ریب‌آی") || persianName.contains("شیشلیک") || persianName.contains("سینه") -> 5
            persianName.contains("راسته") || persianName.contains("سرسیلوین") || persianName.contains("ران") -> 4
            persianName.contains("قلوه‌گاه") || persianName.contains("گردن") -> 3
            else -> 4
        }
    val shelfLifeDisplay: String
        get() = when {
            category == MeatCategory.POULTRY -> "۳ روز سردخانه"
            category == MeatCategory.FISH_SEAFOOD -> "۲ روز سردخانه"
            else -> "۵ تا ۷ روز سردخانه"
        }
    val bestCulinaryUse: String get() = suitableCookingMethods
    val grainDirectionAndCutting: String get() = "$grainDirection. $trimmingInstructions"
    val cookingTemperatureGuideline: String get() = "$recommendedDoneness. $suitableGrillingMethods"
    val agingAndTenderizing: String get() = "$proTips. $suitableMarinades"
    val retailStorageGuidelines: String get() = "$storageGuidance. نگهداری در ویترین با دمای ۱ تا ۳ درجه سانتی‌گراد."
    val suggestedRecipeIds: List<String> get() = emptyList()
}
