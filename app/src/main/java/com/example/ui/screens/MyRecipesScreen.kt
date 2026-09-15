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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodel.RecipeViewModel

@Composable
fun MyRecipesScreen(
    viewModel: RecipeViewModel,
    onBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToProductCard: (String) -> Unit,
    onNavigateToPrepList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recipes by viewModel.displayedUserRecipes.collectAsStateWithLifecycle()
    val totalCount by viewModel.userRecipeCount.collectAsStateWithLifecycle()
    val searchQuery by viewModel.userRecipeSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.userRecipeCategory.collectAsStateWithLifecycle()

    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_my_recipes_back")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "رسپی‌های اختصاصی کارگاه (My Recipes)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "فرمول‌ها و ابداعات شخصی شما (مجزا از ۱۰۰۰ رسپی مرجع)",
                                style = MaterialTheme.typography.bodySmall.copy(color = SaffronGold, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Button(
                            onClick = onNavigateToCreate,
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_create_user_recipe")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("رسپی جدید", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onUserRecipeSearchQueryChanged(it) },
                        placeholder = { Text("جستجو در رسپی‌های اختصاصی شما...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = SaffronGold)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onUserRecipeSearchQueryChanged("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "پاک کردن")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("input_my_recipe_search"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        singleLine = true
                    )

                    // Category Chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(RecipeCategory.values()) { cat ->
                            val isSelected = cat == selectedCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onUserRecipeCategorySelected(cat) },
                                label = { Text(cat.titleFa, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KurdishCrimson,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    KurdishMotifBorder()
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = SaffronGold,
                contentColor = DarkCharcoal,
                modifier = Modifier.testTag("fab_create_user_recipe")
            ) {
                Icon(Icons.Default.Add, contentDescription = "ثبت رسپی جدید")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Count Banner
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تعداد رسپی‌های اختصاصی شما: $totalCount فرمول",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "۱۰۰۰ رسپی مرجع بدون تغییر حفظ شده‌اند",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        fontSize = 11.sp
                    )
                }
            }

            if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PostAdd,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "رسپی اختصاصی با این نام یافت نشد." else "هنوز رسپی اختصاصی ایجاد نکرده‌اید.",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "شما می‌توانید فرمول‌ها و مرینیت‌های اختصاصی فروشگاه خود را از صفر بسازید، یا از صفحه هر کدام از ۱۰۰۰ رسپی مرجع، گزینه 'کپی در رسپی‌های من' را بزنید و آن را ویرایش کنید.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNavigateToCreate,
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ثبت رسپی اختصاصی جدید", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes, key = { it.uniqueId }) { recipe ->
                        UserRecipeCard(
                            recipe = recipe,
                            onEdit = { onNavigateToEdit(recipe.uniqueId) },
                            onDelete = { recipeToDelete = recipe },
                            onNavigateToRecipe = { onNavigateToRecipe(recipe.uniqueId) },
                            onNavigateToProductCard = { onNavigateToProductCard(recipe.uniqueId) },
                            onAddToPrep = {
                                viewModel.createPrepList("تولید ${recipe.name}", recipe, 5.0) {
                                    onNavigateToPrepList()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (recipeToDelete != null) {
        val r = recipeToDelete!!
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            title = { Text("حذف رسپی اختصاصی") },
            text = { Text("آیا از حذف رسپی '${r.name}' اطمینان دارید؟ این عملیات قابل بازگشت نیست.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUserRecipe(r.uniqueId)
                        recipeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KurdishCrimson, contentColor = Color.White)
                ) {
                    Text("حذف قطعی")
                }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDelete = null }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun UserRecipeCard(
    recipe: Recipe,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onNavigateToRecipe: () -> Unit,
    onNavigateToProductCard: () -> Unit,
    onAddToPrep: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToRecipe() }
            .testTag("user_recipe_card_${recipe.uniqueId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = KurdishCrimson.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "رسپی شخصی",
                                color = KurdishCrimson,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "شناسه: ${recipe.uniqueId} • دسته: ${recipe.categoryNameFa} • گوشت: ${recipe.mainProduct}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = SaffronGold, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (recipe.shortDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spec Summary Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${recipe.ingredients.size} قلم ماده اولیه",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${recipe.preparationSteps.size} مرحله آماده‌سازی",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "بچ پایه: ${(recipe.baseWeight / 1000.0 * 10).toLong() / 10.0}kg",
                        fontSize = 11.sp,
                        color = SaffronGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateToProductCard) {
                    Text("کارت محصول", fontSize = 12.sp, color = SaffronGold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(onClick = onAddToPrep) {
                    Text("افزودن به تدارکات", fontSize = 12.sp, color = KurdishCrimson, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
