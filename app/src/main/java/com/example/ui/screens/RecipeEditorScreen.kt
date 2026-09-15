package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IngredientItem
import com.example.data.model.RecipeCategory
import com.example.data.model.UserRecipeEntity
import com.example.data.repository.RecipeValidation
import com.example.data.repository.ValidationResult
import com.example.data.repository.ValidationStatus
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodel.RecipeViewModel
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEditorScreen(
    editingRecipeId: String?,
    viewModel: RecipeViewModel,
    onBack: () -> Unit,
    onSavedSuccessfully: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allRecipes by viewModel.allAvailableRecipes.collectAsStateWithLifecycle()
    val isEditing = !editingRecipeId.isNullOrBlank()

    // Form fields
    var uniqueId by remember {
        mutableStateOf(editingRecipeId ?: "MY-${System.currentTimeMillis() % 100000}")
    }
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(RecipeCategory.CHICKEN_JOOJEH) }
    var subcategory by remember { mutableStateOf("") }
    var mainProduct by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var baseWeightKgText by remember { mutableStateOf("1.0") }
    var portionWeightText by remember { mutableStateOf("250") }

    // Ingredients
    val ingredients = remember {
        mutableStateListOf(
            IngredientItem("گوشت سینه مرغ بی‌استخوان", 1000.0, "گرم", true),
            IngredientItem("پیاز رنده‌شده آب‌گرفته", 150.0, "گرم", true),
            IngredientItem("زعفران دم‌کرده غلیظ", 10.0, "گرم", true),
            IngredientItem("روغن مایع خوراکی", 40.0, "گرم", true),
            IngredientItem("نمک تصفیه‌شده", 12.0, "گرم", true)
        )
    }

    // Steps
    val steps = remember {
        mutableStateListOf(
            "مرحله اول - آماده‌سازی گوشت: گوشت را پس از شستشو و خشک کردن کامل با دستمال حوله‌ای، به قطعات یکدست ۳۰ تا ۳۵ گرمی برش بزنید.",
            "مرحله دوم - طعم‌دار کردن اولیه: ابتدا زعفران دم‌کرده غلیظ را مستقیماً روی گوشت ماساژ دهید تا رنگ و عطر تثبیت گردد.",
            "مرحله سوم - افزودن چاشنی‌ها و استراحت: پیاز، نمک و روغن را اضافه کرده و به مدت ۳ دقیقه ورز دهید. سپس در ظرف دربسته به مدت ۶ تا ۱۲ ساعت در دمای ۱ تا ۳ درجه استراحت دهید."
        )
    }

    // Production times & instructions
    var prepTime by remember { mutableStateOf("۲۵ دقیقه") }
    var marinationTime by remember { mutableStateOf("۸ ساعت") }
    var cookingTime by remember { mutableStateOf("۱۵ دقیقه") }
    var cookingTemp by remember { mutableStateOf("زغال متوسط روشن و خاکسترپوش") }
    var cookingMethod by remember { mutableStateOf("منقل زغالی سنتی") }
    var storageMethod by remember { mutableStateOf("سردخانه بالای صفر") }
    var storageTemp by remember { mutableStateOf("۲°C") }
    var storageDuration by remember { mutableStateOf("۴۸ تا ۷۲ ساعت") }
    var foodSafetyNotes by remember { mutableStateOf("حفظ زنجیره سرد زیر ۴ درجه سانتی‌گراد و عدم استفاده از ظروف آلومینیومی") }
    var professionalTips by remember { mutableStateOf("ورز دادن یکنواخت با دستکش تمیز و عدم افزودن آبلیمو در ساعات اولیه مرینیت جهت جلوگیری از سفتی بافت.") }
    var storeUsage by remember { mutableStateOf("چیدمان شکیل در سینی استیل با گارنیش فلفل دلمه‌ای رنگی و لیمو") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<ValidationResult?>(null) }
    var showConfirmSaveWarningDialog by remember { mutableStateOf(false) }

    // Load initial values if editing existing
    LaunchedEffect(editingRecipeId) {
        if (isEditing) {
            val existing = allRecipes.find { it.uniqueId == editingRecipeId }
            if (existing != null) {
                name = existing.name
                uniqueId = existing.uniqueId
                selectedCategory = existing.category
                subcategory = existing.categoryNameFa
                mainProduct = existing.mainProduct
                shortDescription = existing.shortDescription
                baseWeightKgText = (existing.baseWeight / 1000.0).toString()
                prepTime = existing.preparationTime
                marinationTime = existing.marinationTime
                cookingTime = existing.cookingTime
                cookingTemp = existing.cookingTemperature
                cookingMethod = existing.cookingMethod
                foodSafetyNotes = existing.foodSafetyAlert
                professionalTips = existing.professionalTips
                storeUsage = existing.storeUsage

                ingredients.clear()
                ingredients.addAll(existing.ingredients)

                steps.clear()
                steps.addAll(existing.preparationSteps)
            }
        }
    }

    fun performValidation(): ValidationResult {
        val baseWeightKg = baseWeightKgText.toDoubleOrNull() ?: 1.0
        val entity = UserRecipeEntity(
            uniqueId = uniqueId.trim(),
            name = name.trim(),
            category = selectedCategory.id,
            subcategory = subcategory.trim().ifBlank { selectedCategory.titleFa },
            shortDescription = shortDescription.trim(),
            mainProduct = mainProduct.trim(),
            sourceType = "User Custom",
            baseWeight = baseWeightKg * 1000.0,
            expectedFinalWeight = baseWeightKg * 900.0,
            portionWeight = portionWeightText.toDoubleOrNull() ?: 250.0,
            ingredientsJson = "[]",
            preparationStepsJson = "[]",
            preparationTime = prepTime.trim(),
            marinationTime = marinationTime.trim(),
            cookingTime = cookingTime.trim(),
            cookingTemperature = cookingTemp.trim(),
            cookingMethod = cookingMethod.trim(),
            equipment = "تجهیزات استاندارد کارگاهی",
            storageMethod = storageMethod.trim(),
            storageTemperature = storageTemp.trim(),
            storageDuration = storageDuration.trim(),
            packagingNotes = "بسته‌بندی وکیوم یا سینی استیل",
            foodSafetyNotes = foodSafetyNotes.trim(),
            professionalTips = professionalTips.trim(),
            commonMistakes = "",
            storeUsage = storeUsage.trim(),
            searchTagsJson = "[]",
            isFavorite = false
        )

        val existingIds = allRecipes.map { it.uniqueId }.toSet()
        return RecipeValidation.validateUserRecipe(
            recipe = entity,
            ingredients = ingredients,
            steps = steps,
            existingIds = existingIds,
            isEditingExisting = isEditing
        )
    }

    fun executeSave() {
        val baseWeightKg = baseWeightKgText.toDoubleOrNull() ?: 1.0
        val ingsArr = JSONArray()
        for (ing in ingredients) {
            val o = JSONObject()
            o.put("name", ing.name.trim())
            o.put("amount", ing.amount)
            o.put("unit", ing.unit.trim())
            o.put("isScalable", ing.isScalable)
            ingsArr.put(o)
        }

        val stepsArr = JSONArray()
        for (s in steps) {
            if (s.isNotBlank()) stepsArr.put(s.trim())
        }

        val entity = UserRecipeEntity(
            uniqueId = uniqueId.trim(),
            name = name.trim(),
            category = selectedCategory.id,
            subcategory = subcategory.trim().ifBlank { selectedCategory.titleFa },
            shortDescription = shortDescription.trim(),
            mainProduct = mainProduct.trim(),
            sourceType = "کارگاه اختصاصی",
            baseWeight = baseWeightKg * 1000.0,
            expectedFinalWeight = baseWeightKg * 900.0,
            portionWeight = portionWeightText.toDoubleOrNull() ?: 250.0,
            ingredientsJson = ingsArr.toString(),
            preparationStepsJson = stepsArr.toString(),
            preparationTime = prepTime.trim(),
            marinationTime = marinationTime.trim(),
            cookingTime = cookingTime.trim(),
            cookingTemperature = cookingTemp.trim(),
            cookingMethod = cookingMethod.trim(),
            equipment = "تجهیزات کارگاهی",
            storageMethod = storageMethod.trim(),
            storageTemperature = storageTemp.trim(),
            storageDuration = storageDuration.trim(),
            packagingNotes = "سینی استیل وکیوم",
            foodSafetyNotes = foodSafetyNotes.trim(),
            professionalTips = professionalTips.trim(),
            commonMistakes = "",
            storeUsage = storeUsage.trim(),
            searchTagsJson = "[]",
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        viewModel.saveUserRecipe(entity) {
            Toast.makeText(context, "رسپی اختصاصی '${entity.name}' با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show()
            onSavedSuccessfully(entity.uniqueId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 3.dp) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_editor_back")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = MaterialTheme.colorScheme.primary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isEditing) "ویرایش رسپی اختصاصی" else "ثبت رسپی اختصاصی کارگاه",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "شناسه: $uniqueId",
                                style = MaterialTheme.typography.bodySmall.copy(color = SaffronGold)
                            )
                        }

                        Button(
                            onClick = {
                                val res = performValidation()
                                validationResult = res
                                when (res.status) {
                                    ValidationStatus.GREEN -> executeSave()
                                    ValidationStatus.YELLOW -> showConfirmSaveWarningDialog = true
                                    ValidationStatus.RED -> {
                                        Toast.makeText(context, res.summaryMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_save_recipe")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اعتبارسنجی و ذخیره", fontWeight = FontWeight.Bold)
                        }
                    }
                    KurdishMotifBorder()
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Real-Time Validation Banner
            if (validationResult != null) {
                item {
                    val res = validationResult!!
                    val bannerColor = when (res.status) {
                        ValidationStatus.GREEN -> Color(0xFF2E7D32)
                        ValidationStatus.YELLOW -> SaffronGold
                        ValidationStatus.RED -> KurdishCrimson
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("validation_banner"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = bannerColor.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (res.status) {
                                        ValidationStatus.GREEN -> Icons.Default.CheckCircle
                                        ValidationStatus.YELLOW -> Icons.Default.Warning
                                        ValidationStatus.RED -> Icons.Default.Error
                                    },
                                    contentDescription = null,
                                    tint = bannerColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = res.summaryMessage,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = bannerColor
                                )
                            }

                            if (res.errors.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("خطاهای ضروری:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KurdishCrimson)
                                res.errors.forEach { err ->
                                    Text("• $err", fontSize = 12.sp, color = KurdishCrimson, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }

                            if (res.warnings.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("توصیه‌های تکمیلی کارگاه:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                res.warnings.forEach { wrn ->
                                    Text("• $wrn", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // General Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "مشخصات هویتی و ویترینی محصول",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KurdishCrimson
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("نام محصول / رسپی *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_recipe_name")
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = uniqueId,
                                onValueChange = { uniqueId = it },
                                label = { Text("شناسه یکتا (ID) *") },
                                singleLine = true,
                                enabled = !isEditing,
                                modifier = Modifier.weight(1f).testTag("input_recipe_id")
                            )

                            // Category selector
                            ExposedDropdownMenuBox(
                                expanded = categoryDropdownExpanded,
                                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                                modifier = Modifier.weight(1.2f)
                            ) {
                                OutlinedTextField(
                                    value = selectedCategory.titleFa,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("دسته‌بندی *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false }
                                ) {
                                    RecipeCategory.values().filter { it != RecipeCategory.ALL }.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.titleFa) },
                                            onClick = {
                                                selectedCategory = cat
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = mainProduct,
                                onValueChange = { mainProduct = it },
                                label = { Text("برش یا گوشت پایه (مثال: سینه مرغ، راسته)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = baseWeightKgText,
                                onValueChange = { baseWeightKgText = it },
                                label = { Text("وزن بچ پایه (کیلوگرم) *") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = shortDescription,
                            onValueChange = { shortDescription = it },
                            label = { Text("توصیف کوتاه و مشخصات ویترینی") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Ingredients Builder
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "فرمول و مواد اولیه (${ingredients.size} قلم) *",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = KurdishCrimson
                            )

                            TextButton(
                                onClick = {
                                    ingredients.add(IngredientItem("", 100.0, "گرم", true))
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("افزودن ماده", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ingredients.forEachIndexed { index, ing ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = ing.name,
                                    onValueChange = { newName ->
                                        ingredients[index] = ing.copy(name = newName)
                                    },
                                    placeholder = { Text("نام ماده") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1.8f)
                                )

                                OutlinedTextField(
                                    value = if (ing.amount == 0.0) "" else ing.amount.toString(),
                                    onValueChange = { newAmt ->
                                        val amt = newAmt.toDoubleOrNull() ?: 0.0
                                        ingredients[index] = ing.copy(amount = amt)
                                    },
                                    placeholder = { Text("مقدار") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = ing.unit,
                                    onValueChange = { newUnit ->
                                        ingredients[index] = ing.copy(unit = newUnit)
                                    },
                                    placeholder = { Text("واحد") },
                                    singleLine = true,
                                    modifier = Modifier.weight(0.9f)
                                )

                                IconButton(
                                    onClick = { ingredients.removeAt(index) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            // Steps Builder
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مراحل تفصیلی آماده‌سازی و فرآوری *",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = KurdishCrimson
                            )

                            TextButton(
                                onClick = {
                                    steps.add("مرحله ${steps.size + 1}: ")
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("افزودن مرحله", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        steps.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                OutlinedTextField(
                                    value = step,
                                    onValueChange = { newStep -> steps[index] = newStep },
                                    label = { Text("مرحله ${index + 1}") },
                                    maxLines = 4,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { if (steps.size > 1) steps.removeAt(index) },
                                    modifier = Modifier.padding(top = 8.dp).size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "حذف مرحله", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            // Production & Storage Parameters Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "پارامترهای فنی پخت، سردخانه و نگهداری",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KurdishCrimson
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = prepTime,
                                onValueChange = { prepTime = it },
                                label = { Text("زمان آماده‌سازی") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = marinationTime,
                                onValueChange = { marinationTime = it },
                                label = { Text("زمان مرینیت / استراحت") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cookingMethod,
                                onValueChange = { cookingMethod = it },
                                label = { Text("روش پخت (گریل، فر، منقل)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cookingTemp,
                                onValueChange = { cookingTemp = it },
                                label = { Text("دمای پخت / دمای زغال") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = storageTemp,
                                onValueChange = { storageTemp = it },
                                label = { Text("دمای سردخانه (مثال: ۲°C)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = storageDuration,
                                onValueChange = { storageDuration = it },
                                label = { Text("مدت ماندگاری ویترین") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = foodSafetyNotes,
                            onValueChange = { foodSafetyNotes = it },
                            label = { Text("هشدارهای بهداشتی و ایمنی غذا (آلرژن‌ها)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = professionalTips,
                            onValueChange = { professionalTips = it },
                            label = { Text("فوت‌های کوزه‌گری سرآشپز (Professional Tips)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = storeUsage,
                            onValueChange = { storeUsage = it },
                            label = { Text("راهنمای چیدمان ویترین و ترغیب مشتری") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showConfirmSaveWarningDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmSaveWarningDialog = false },
            title = { Text("ذخیره با هشدارهای تکمیلی") },
            text = {
                Column {
                    Text("رسپی شما دارای چند بخش تکمیلی ناقص است، اما قابل ذخیره می‌باشد. آیا مایلید ذخیره شود؟")
                    Spacer(modifier = Modifier.height(8.dp))
                    validationResult?.warnings?.forEach { w ->
                        Text("• $w", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmSaveWarningDialog = false
                        executeSave()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal)
                ) {
                    Text("بله، ذخیره شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSaveWarningDialog = false }) { Text("ویرایش بیشتر") }
            }
        )
    }
}
