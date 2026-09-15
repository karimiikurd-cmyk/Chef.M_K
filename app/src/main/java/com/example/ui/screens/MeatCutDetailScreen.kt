package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MeatCut
import com.example.data.repository.MeatKnowledgeData
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodel.RecipeViewModel

@Composable
fun MeatCutDetailScreen(
    cutId: String,
    viewModel: RecipeViewModel,
    onBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cut = remember(cutId) { MeatKnowledgeData.getCutById(cutId) }
    val allRecipes by viewModel.allAvailableRecipes.collectAsStateWithLifecycle()

    if (cut == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("اطلاعات برش مورد نظر یافت نشد.")
        }
        return
    }

    // Find recipes that match this cut
    val matchingRecipes = remember(cut, allRecipes) {
        val cutKeywords = listOf(cut.nameFa, cut.nameEn, cut.id)
        allRecipes.filter { r ->
            cut.suggestedRecipeIds.contains(r.uniqueId) ||
            cutKeywords.any { kw -> r.mainProduct.contains(kw) || r.name.contains(kw) || r.searchTags.contains(kw) }
        }.take(10)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_cut_detail_back")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cut.nameFa,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${cut.nameEn} • ${cut.category.titleFa}",
                                style = MaterialTheme.typography.bodySmall.copy(color = SaffronGold, fontWeight = FontWeight.SemiBold)
                            )
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
            // Header Card with Anatomical Summary
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "موقعیت آناتومیکی و خصوصیات بافت",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KurdishCrimson
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cut.anatomicalLocation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Metric Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricItem(
                                title = "نرمی بافت",
                                value = "${cut.tendernessScore} از ۵",
                                icon = Icons.Default.Star,
                                tint = SaffronGold
                            )
                            MetricItem(
                                title = "درصد چربی",
                                value = cut.fatPercentage,
                                icon = Icons.Default.Restaurant,
                                tint = KurdishCrimson
                            )
                            MetricItem(
                                title = "ماندگاری ویترین",
                                value = cut.shelfLifeDisplay,
                                icon = Icons.Default.AcUnit,
                                tint = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }

            // Culinary Use & Cooking Instructions
            item {
                KnowledgeSectionCard(
                    title = "کاربرد تخصصی در فرآوری و فروشگاه",
                    icon = Icons.Default.OutdoorGrill,
                    content = cut.bestCulinaryUse
                )
            }

            // Cutting & Grain Direction
            item {
                KnowledgeSectionCard(
                    title = "جهت الیاف و نحوه برش و قصابی",
                    icon = Icons.Default.ContentCut,
                    content = cut.grainDirectionAndCutting
                )
            }

            // Doneness & Temperature
            item {
                KnowledgeSectionCard(
                    title = "دمای ایده‌آل پخت و شاخص مغزپخت (Core Temp)",
                    icon = Icons.Default.DeviceThermostat,
                    content = cut.cookingTemperatureGuideline
                )
            }

            // Aging & Tenderization
            item {
                KnowledgeSectionCard(
                    title = "روش بیات کردن (Aging) و نرم‌سازی کارگاهی",
                    icon = Icons.Default.Timer,
                    content = cut.agingAndTenderizing
                )
            }

            // Packaging & Storage
            item {
                KnowledgeSectionCard(
                    title = "اصول بسته‌بندی، چیدمان در ویترین و سردخانه",
                    icon = Icons.Default.Inventory,
                    content = cut.retailStorageGuidelines
                )
            }

            // Matching Recipes in Chef.M_K
            if (matchingRecipes.isNotEmpty()) {
                item {
                    Text(
                        text = "فرمول‌های هماهنگ با این برش در Chef.M_K (${matchingRecipes.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(matchingRecipes) { recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToRecipe(recipe.uniqueId) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recipe.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${recipe.categoryNameFa} • گوشت پایه: ${recipe.mainProduct}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Text(
                                text = "مشاهده رسپی ›",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SaffronGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun KnowledgeSectionCard(
    title: String,
    icon: ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
