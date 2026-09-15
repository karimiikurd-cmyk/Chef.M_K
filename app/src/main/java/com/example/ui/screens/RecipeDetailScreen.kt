package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Recipe
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SafetyAlertContainer
import com.example.ui.theme.SafetyAlertRed
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: RecipeViewModel,
    onBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToProductCard: (String) -> Unit = {},
    onNavigateToPrepList: () -> Unit = {},
    onNavigateToMyRecipes: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentRecipe by viewModel.currentRecipe.collectAsStateWithLifecycle()
    val scaleWeightGrams by viewModel.scaleWeightGrams.collectAsStateWithLifecycle()

    LaunchedEffect(recipeId) {
        viewModel.selectRecipeById(recipeId)
    }

    val recipe = currentRecipe
    if (recipe == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("در حال بارگذاری دستور...")
        }
        return
    }

    // Step completion checklist state
    val checkedSteps = remember(recipe.uniqueId) { mutableStateListOf<Int>() }

    // User note editing state
    var isEditingNotes by remember { mutableStateOf(false) }
    var noteText by remember(recipe.userNotes) { mutableStateOf(recipe.userNotes) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigateToProductCard(recipe.uniqueId) },
                        modifier = Modifier.testTag("btn_product_card")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "کارت محصول ویترینی",
                            tint = SaffronGold
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.createPrepList("تولید ${recipe.name}", recipe, (scaleWeightGrams / 1000.0).coerceAtLeast(1.0)) {
                                onNavigateToPrepList()
                            }
                        },
                        modifier = Modifier.testTag("btn_add_to_prep")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "افزودن به تدارکات",
                            tint = SaffronGold
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleFavorite(recipe) },
                        modifier = Modifier.testTag("btn_favorite_toggle")
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "نشان‌گذاری",
                            tint = if (recipe.isFavorite) SaffronGold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kurdish Motif Accent Strip
            item {
                KurdishMotifBorder()
            }

            // Recipe Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = KurdishCrimson,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = recipe.uniqueId,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            Surface(
                                color = SaffronGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = recipe.categoryNameFa,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronGold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                        )

                        if (recipe.mainProduct.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "برش / ماده اصلی: ${recipe.mainProduct}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = recipe.shortDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick System Actions
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onNavigateToProductCard(recipe.uniqueId) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp), tint = SaffronGold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("کارت محصول ویترینی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.createPrepList("تولید ${recipe.name}", recipe, (scaleWeightGrams / 1000.0).coerceAtLeast(1.0)) {
                                        onNavigateToPrepList()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold)
                            ) {
                                Icon(Icons.Default.PlaylistAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تدارکات تولید", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.duplicateRecipeToMyRecipes(recipe) {
                                        onNavigateToMyRecipes()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("کپی در رسپی‌های من", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Interactive Batch Scaler Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = SaffronGold
                                )
                                Text(
                                    text = "مقیاس‌سنج و محاسبه‌گر بچ کارگاهی",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "وزن مبنای این فرمول ${recipe.baseWeight.toInt()} گرم است. برای محاسبه تناژ تولیدی فروشگاه، وزن مورد نظر را انتخاب کنید:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preset weights
                        val presets = listOf(
                            500.0 to "۵۰۰ گرم",
                            1000.0 to "۱ کیلو",
                            2000.0 to "۲ کیلو",
                            5000.0 to "۵ کیلو",
                            10000.0 to "۱۰ کیلو",
                            20000.0 to "۲۰ کیلو",
                            50000.0 to "۵۰ کیلو"
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(presets) { (weight, label) ->
                                val isSelected = (scaleWeightGrams == weight)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setScaleWeight(weight) },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SaffronGold,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val displayKg = scaleWeightGrams / 1000.0
                        Text(
                            text = "فرمول محاسبه شده برای بچ ${if (displayKg >= 1) "%.1f کیلوگرم".format(displayKg) else "${scaleWeightGrams.toInt()} گرم"}:",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SaffronGold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Ingredients Scaled Table
                        val scaledIngredients = recipe.scaleForWeight(scaleWeightGrams)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            scaledIngredients.forEachIndexed { index, ing ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}. ${ing.name}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (ing.amount == ing.amount.toLong().toDouble()) {
                                                "${ing.amount.toLong()} ${ing.unit}"
                                            } else {
                                                "%.1f %s".format(ing.amount, ing.unit)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }
                                }
                                if (index < scaledIngredients.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Preparation Steps with interactive checklist
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SaffronGold
                            )
                            Text(
                                text = "مراحل استاندارد آماده‌سازی و ترکیب کارگاهی",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        recipe.preparationSteps.forEachIndexed { index, step ->
                            val isChecked = checkedSteps.contains(index)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (isChecked) checkedSteps.remove(index) else checkedSteps.add(index)
                                    }
                                    .background(if (isChecked) SaffronGold.copy(alpha = 0.1f) else Color.Transparent)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isChecked) SaffronGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    val colonIndex = step.indexOf(':')
                                    val hasColon = colonIndex > 0 && colonIndex < 80
                                    val stageTitle = if (hasColon) step.substring(0, colonIndex).trim() else "مرحله ${index + 1}"
                                    val stageBody = if (hasColon) step.substring(colonIndex + 1).trim() else step

                                    Text(
                                        text = stageTitle,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else SaffronGold,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stageBody,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                            if (index < recipe.preparationSteps.size - 1) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }

            // Cooking & Technical Parameters Grid
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "پارامترهای فنی و شرایط پخت / عمل‌آوری",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = SaffronGold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        DetailParamRow("زمان آماده‌سازی اولیه", recipe.preparationTime)
                        DetailParamRow("زمان مرینیت / استراحت", recipe.marinationTime)
                        DetailParamRow("زمان پخت استاندارد", recipe.cookingTime)
                        DetailParamRow("دما و شدت حرارت", recipe.cookingTemperature)
                        DetailParamRow("متد و روش پخت", recipe.cookingMethod)
                        DetailParamRow("ماندگاری در شرایط استاندارد", recipe.recommendedStorageTime)
                    }
                }
            }

            // Category Specific Anatomy & Specs (if present)
            if (recipe.extraDetailsMap.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "مشخصات آناتومیک و پارامترهای اختصاصی برش",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            recipe.extraDetailsMap.forEach { (key, value) ->
                                val cleanKey = key.substringAfter(".")
                                    .replace("yieldPercentage", "درصد بازدهی و افت وزن")
                                    .replace("skewerStyle", "روش و زاویه سیخ‌گیری")
                                    .replace("cutThickness", "ضخامت استاندارد برش")
                                    .replace("marblingScore", "درجه چربی بین‌عضلانی (ماربلینگ)")
                                    .replace("fatRatio", "نسبت چربی به گوشت خالص")
                                    .replace("smokePoint", "نقطه دود روغن")
                                    .replace("emulsionType", "نوع امولسیون و غلظت")
                                    .replace("grindSize", "سایز پنجره چرخ‌گوشت")
                                    .replace("onionPrep", "فرآوری و آب‌گیری پیاز")
                                    .replace("safeInternalTemp", "دمای امن مغزپخت")
                                    .replace("shelfLifeDressed", "ماندگاری پس از ترکیب سس")
                                    .replace("texturePreservationTips", "حفظ تردی و بافت")
                                    .replace("proteinPairing", "پروتئین سازگار")
                                    .replace("meltingBehavior", "رفتار ذوب روی گوشت داغ")
                                    .replace("meatPairing", "بهترین جفت گوشت")
                                    .replace("botulismSafetyNote", "پروتکل پیشگیری از بوتولیسم")

                                DetailParamRow(cleanKey, value)
                            }
                        }
                    }
                }
            }

            // Professional Tips Card
            if (recipe.professionalTips.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SaffronGold.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = SaffronGold
                                )
                                Text(
                                    text = "نکات طلایی سرآشپز (Professional Tips)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronGold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recipe.professionalTips,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Common Mistakes to Avoid Card
            if (recipe.commonMistakes.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = KurdishCrimson.copy(alpha = 0.12f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = KurdishCrimson
                                )
                                Text(
                                    text = "اشتباهات رایج و خطاهای مخرب بافت",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = KurdishCrimson
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recipe.commonMistakes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Critical Food Safety Alert Card
            if (recipe.foodSafetyAlert.isNotBlank()) {
                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SafetyAlertRed),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = SafetyAlertContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = SafetyAlertRed
                                )
                                Text(
                                    text = "هشدار حیاتی ایمنی بهداشتی و میکروبی",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SafetyAlertRed
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recipe.foodSafetyAlert,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }

            // Store Usage & Display Advice
            if (recipe.storeUsage.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Store,
                                    contentDescription = null,
                                    tint = SaffronGold
                                )
                                Text(
                                    text = "راهنمای چیدمان ویترین و فروشگاهی",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recipe.storeUsage,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Related Products (Paired Sauces, Spices, Salads)
            val allRelated = recipe.relatedSauceIds + recipe.relatedSpiceIds + recipe.relatedSaladIds
            if (allRelated.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "محصولات مکمل و سس/ادویه پیشنهادی",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allRelated.forEach { relId ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.clickable { onNavigateToRecipe(relId) }
                                    ) {
                                        Text(
                                            text = "مشاهده محصول $relId",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SaffronGold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Store Kitchen Notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = SaffronGold
                                )
                                Text(
                                    text = "یادداشت‌های اختصاصی کارگاه قصابی",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            if (!isEditingNotes) {
                                IconButton(onClick = { isEditingNotes = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "ویرایش")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isEditingNotes) {
                            OutlinedTextField(
                                value = noteText,
                                onValueChange = { noteText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("نکات خاص مشتریان، تامین‌کننده گوشت یا ادویه...") },
                                minLines = 3
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.saveUserNote(recipe.uniqueId, noteText)
                                    isEditingNotes = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ذخیره یادداشت", color = Color.Black)
                            }
                        } else {
                            Text(
                                text = if (recipe.userNotes.isBlank()) "هنوز یادداشتی برای این محصول ثبت نشده است. با لمس دکمه ویرایش یادداشت خود را ثبت کنید." else recipe.userNotes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (recipe.userNotes.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailParamRow(
    label: String,
    value: String
) {
    if (value.isBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(0.6f)
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
        thickness = 1.dp
    )
}
