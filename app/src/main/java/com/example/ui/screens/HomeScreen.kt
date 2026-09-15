package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodel.RecipeViewModel

@Composable
fun HomeScreen(
    viewModel: RecipeViewModel,
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToBatchScaler: () -> Unit,
    onNavigateToMarinadeBuilder: () -> Unit,
    onNavigateToSpiceCalc: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToPrepList: () -> Unit = {},
    onNavigateToMeatKnowledge: () -> Unit = {},
    onNavigateToMyRecipes: () -> Unit = {},
    onNavigateToProductCard: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isInitializing by viewModel.isInitializing.collectAsStateWithLifecycle()
    val recipes by viewModel.displayedRecipes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header with Kurdish Styling
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chef.M_K",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = SaffronGold
                            )
                        )
                        Text(
                            text = "دستیار حرفه‌ای و فرمولاسیون فروشگاه پروتئینی",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Favorites Shortcut Button
                    IconButton(
                        onClick = onNavigateToFavorites,
                        modifier = Modifier
                            .testTag("btn_favorites")
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "علاقه‌مندی‌ها",
                                tint = if (favorites.isNotEmpty()) SaffronGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 4 Major Systems Main Navigation Tabs
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { },
                            leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text("۱۰۰۰ محصول مرجع", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KurdishCrimson,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = onNavigateToPrepList,
                            leadingIcon = { Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp), tint = SaffronGold) },
                            label = { Text("تدارکات تولید (Prep List)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = onNavigateToMeatKnowledge,
                            leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp), tint = SaffronGold) },
                            label = { Text("دانشنامه قصابی", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = onNavigateToMyRecipes,
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = SaffronGold) },
                            label = { Text("رسپی‌های من", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                // Kurdish Motif Border
                KurdishMotifBorder()
            }
        }

        if (isInitializing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = SaffronGold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "در حال بارگذاری ۱۰۰۰ محصول و فرمول پروتئینی...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Quick Tools Navigation Row
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            text = "ابزارهای تخصصی آماده‌سازی",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickToolCard(
                                title = "تدارکات تولید",
                                subtitle = "برنامه‌ریز کارگاه",
                                icon = Icons.Default.Checklist,
                                accentColor = SaffronGold,
                                onClick = onNavigateToPrepList,
                                modifier = Modifier.weight(1f)
                            )
                            QuickToolCard(
                                title = "دانشنامه قصابی",
                                subtitle = "شناخت لاشه و برش",
                                icon = Icons.Default.MenuBook,
                                accentColor = KurdishCrimson,
                                onClick = onNavigateToMeatKnowledge,
                                modifier = Modifier.weight(1f)
                            )
                            QuickToolCard(
                                title = "رسپی‌های من",
                                subtitle = "فرمول‌های شخصی",
                                icon = Icons.Default.PostAdd,
                                accentColor = Color(0xFF1976D2),
                                onClick = onNavigateToMyRecipes,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickToolCard(
                                title = "محاسبه‌گر بچ",
                                subtitle = "تناژ و ترازو",
                                icon = Icons.Default.Calculate,
                                accentColor = SaffronGold,
                                onClick = onNavigateToBatchScaler,
                                modifier = Modifier.weight(1f)
                            )
                            QuickToolCard(
                                title = "مرینیت‌ساز",
                                subtitle = "فرمول طلایی",
                                icon = Icons.Default.Science,
                                accentColor = KurdishCrimson,
                                onClick = onNavigateToMarinadeBuilder,
                                modifier = Modifier.weight(1f)
                            )
                            QuickToolCard(
                                title = "ادویه‌سنج",
                                subtitle = "تناسب گرم",
                                icon = Icons.Default.Tune,
                                accentColor = Color(0xFF3F5E48),
                                onClick = onNavigateToSpiceCalc,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("input_search"),
                        placeholder = {
                            Text(
                                "جستجو در ۱۰۰۰ محصول، برش، طعم و ادویه...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "پاک کردن",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                // Category Chips Row
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(RecipeCategory.entries.toTypedArray()) { cat ->
                            val isSelected = cat == selectedCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onCategorySelected(cat) },
                                label = {
                                    Text(
                                        text = cat.titleFa,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SaffronGold,
                                    selectedLabelColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                // Results Count
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${recipes.size} دستور فرمولاسیون یافته",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (selectedCategory != RecipeCategory.ALL) {
                            Text(
                                text = selectedCategory.titleFa,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = SaffronGold
                            )
                        }
                    }
                }

                // Recipe Cards
                if (recipes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "موردی با این مشخصات یافت نشد.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(
                        items = recipes,
                        key = { it.uniqueId }
                    ) { recipe ->
                        RecipeItemCard(
                            recipe = recipe,
                            onRecipeClick = { onNavigateToRecipe(recipe.uniqueId) },
                            onToggleFavorite = { viewModel.toggleFavorite(recipe) },
                            onProductCardClick = { onNavigateToProductCard(recipe.uniqueId) },
                            onAddToPrepClick = {
                                viewModel.createPrepList("تولید ${recipe.name}", recipe, 5.0) {
                                    onNavigateToPrepList()
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickToolCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun RecipeItemCard(
    recipe: Recipe,
    onRecipeClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onProductCardClick: (() -> Unit)? = null,
    onAddToPrepClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onRecipeClick() }
            .testTag("card_recipe_${recipe.uniqueId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top row: ID badge, Category tag, Favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ID Badge
                    Surface(
                        color = KurdishCrimson,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = recipe.uniqueId,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    // Category Pill
                    Surface(
                        color = SaffronGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = recipe.categoryNameFa,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = SaffronGold
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "نشان کردن",
                        tint = if (recipe.isFavorite) SaffronGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recipe Name
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Cut / Component
            if (recipe.mainProduct.isNotBlank()) {
                Text(
                    text = "برش و پایه: ${recipe.mainProduct}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Short Description
            Text(
                text = recipe.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer metrics: Prep time, Marination, Cooking temp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (recipe.marinationTime.isNotBlank()) {
                    MetricChip(
                        icon = Icons.Default.Timer,
                        text = recipe.marinationTime
                    )
                }
                if (recipe.cookingMethod.isNotBlank()) {
                    MetricChip(
                        icon = Icons.Default.OutdoorGrill,
                        text = recipe.cookingMethod
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(4.dp))

            // Action links for Product Card and Prep List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onProductCardClick?.invoke() },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp), tint = SaffronGold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("کارت محصول ویترینی", fontSize = 11.sp, color = SaffronGold, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = { onAddToPrepClick?.invoke() },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.PlaylistAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = KurdishCrimson)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("افزودن به تدارکات", fontSize = 11.sp, color = KurdishCrimson, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
