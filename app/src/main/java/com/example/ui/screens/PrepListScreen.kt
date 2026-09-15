package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AggregatedIngredient
import com.example.data.model.PrepList
import com.example.data.model.PrepListItem
import com.example.data.model.PrepTask
import com.example.data.model.Recipe
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrepListScreen(
    viewModel: RecipeViewModel,
    onBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToProductCard: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prepLists by viewModel.prepLists.collectAsStateWithLifecycle()
    val activePrepList by viewModel.activePrepList.collectAsStateWithLifecycle()
    val calculatedPlan by viewModel.calculatedPrepPlan.collectAsStateWithLifecycle()
    val allRecipes by viewModel.allAvailableRecipes.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showAddRecipeDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: اقلام و اوزان, 1: تجمیع مواد اولیه, 2: مراحل اجرایی

    // Auto-create default list if none exists
    if (prepLists.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "برنامه‌ریز تدارکات کارگاه (Smart Prep List)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "هنوز هیچ لیست تولیدی ایجاد نشده است. می‌توانید برای تدارکات روزانه، سفارش‌های مهمانی یا آماده‌سازی ویترین، یک برنامه جدید بسازید.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.createPrepList("تدارکات تولید روزانه") },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_create_first_prep")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ایجاد اولین برنامه تولید", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    val currentList = activePrepList ?: prepLists.first()

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
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_prep_back")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تدارکات و تولید کارگاهی",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = currentList.title,
                                style = MaterialTheme.typography.bodySmall.copy(color = SaffronGold, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        IconButton(
                            onClick = { showAddRecipeDialog = true },
                            modifier = Modifier.testTag("btn_prep_add_item")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "افزودن محصول به لیست",
                                tint = SaffronGold
                            )
                        }

                        IconButton(
                            onClick = {
                                if (calculatedPlan != null) {
                                    val summaryText = buildExportText(calculatedPlan!!)
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("برنامه تولید کارگاهی Chef.M_K", summaryText)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "خلاصه برنامه تولید برای واتساپ و چاپ کپی شد.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("btn_prep_share")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "اشتراک‌گذاری خلاصه برنامه",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Prep List Selector & Actions bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick lists chip row or select dropdown
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            prepLists.forEach { pl ->
                                val isSelected = pl.id == currentList.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setActivePrepList(pl.id) },
                                    label = { Text(pl.title, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KurdishCrimson,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        IconButton(onClick = { showCreateDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "لیست جدید",
                                tint = SaffronGold
                            )
                        }
                    }

                    KurdishMotifBorder()

                    // Sub-tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = SaffronGold
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("اقلام تولید (${currentList.items.size})", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("تجمیع مواد اولیه", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                val totalTasks = calculatedPlan?.tasks?.size ?: 0
                                val doneTasks = calculatedPlan?.tasks?.count { it.isCompleted } ?: 0
                                Text("مراحل اجرایی ($doneTasks/$totalTasks)", fontWeight = FontWeight.Bold)
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddRecipeDialog = true },
                    containerColor = SaffronGold,
                    contentColor = DarkCharcoal,
                    modifier = Modifier.testTag("fab_add_prep_item")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن محصول")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Overall Stats Ribbon
            val totalKg = currentList.items.sumOf { it.targetWeightKg }
            val totalTasks = calculatedPlan?.tasks?.size ?: 0
            val doneTasks = calculatedPlan?.tasks?.count { it.isCompleted } ?: 0
            val progressPercent = if (totalTasks > 0) (doneTasks.toFloat() / totalTasks) else 0f

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مجموع وزن تولید: ${(totalKg * 10.0).toLong() / 10.0} کیلوگرم",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "پیشرفت کارگاه: ${(progressPercent * 100).toInt()}٪",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (progressPercent >= 1f) Color(0xFF2E7D32) else SaffronGold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (progressPercent >= 1f) Color(0xFF2E7D32) else SaffronGold,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    // Management actions
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showRenameDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تغییر نام", fontSize = 12.sp)
                        }
                        TextButton(onClick = { viewModel.duplicatePrepList(currentList.id) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کپی لیست", fontSize = 12.sp)
                        }
                        TextButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = KurdishCrimson)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("حذف", fontSize = 12.sp, color = KurdishCrimson)
                        }
                    }
                }
            }

            // Tab Contents
            when (selectedTab) {
                0 -> ItemsTabContent(
                    items = currentList.items,
                    onUpdateWeight = { rId, delta -> viewModel.updatePrepItemWeight(currentList.id, rId, delta) },
                    onSetWeight = { rId, w -> viewModel.setPrepItemWeight(currentList.id, rId, w) },
                    onRemoveItem = { rId -> viewModel.removeRecipeFromPrepList(currentList.id, rId) },
                    onNavigateToRecipe = onNavigateToRecipe,
                    onNavigateToProductCard = onNavigateToProductCard,
                    onOpenAddDialog = { showAddRecipeDialog = true }
                )
                1 -> AggregatedTabContent(
                    aggregated = calculatedPlan?.aggregatedIngredients ?: emptyList()
                )
                2 -> TasksTabContent(
                    tasks = calculatedPlan?.tasks ?: emptyList(),
                    onToggleTask = { taskId -> viewModel.togglePrepTask(currentList.id, taskId) },
                    onResetTasks = { viewModel.resetPrepTasks(currentList.id) }
                )
            }
        }
    }

    // Dialogs
    if (showCreateDialog) {
        var newTitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("ایجاد لیست تدارکات جدید") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("نام برنامه (مثال: تولید آخر هفته)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_new_prep_title")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.createPrepList(newTitle)
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal)
                ) {
                    Text("ایجاد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("انصراف") }
            }
        )
    }

    if (showRenameDialog) {
        var editedTitle by remember { mutableStateOf(currentList.title) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("تغییر نام برنامه") },
            text = {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("نام جدید برنامه") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedTitle.isNotBlank()) {
                            viewModel.renamePrepList(currentList.id, editedTitle)
                            showRenameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal)
                ) {
                    Text("ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("انصراف") }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("حذف برنامه تدارکات") },
            text = { Text("آیا از حذف برنامه '${currentList.title}' اطمینان دارید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePrepList(currentList.id)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KurdishCrimson, contentColor = Color.White)
                ) {
                    Text("حذف قطعی")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("انصراف") }
            }
        )
    }

    if (showAddRecipeDialog) {
        AddRecipeToPrepDialog(
            allRecipes = allRecipes,
            onDismiss = { showAddRecipeDialog = false },
            onAdd = { recipe, targetKg, notes ->
                viewModel.addRecipeToPrepList(currentList.id, recipe, targetKg, notes)
                showAddRecipeDialog = false
            }
        )
    }
}

@Composable
private fun ItemsTabContent(
    items: List<PrepListItem>,
    onUpdateWeight: (String, Double) -> Unit,
    onSetWeight: (String, Double) -> Unit,
    onRemoveItem: (String) -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToProductCard: (String) -> Unit,
    onOpenAddDialog: () -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = null,
                    tint = SaffronGold,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "هیچ رسپی یا محصولی در این برنامه ثبت نشده است.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onOpenAddDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("افزودن محصول از بین ۱۰۰۰ فرمول یا رسپی‌های من")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.recipeId }) { item ->
            PrepItemCard(
                item = item,
                onUpdateWeight = { delta -> onUpdateWeight(item.recipeId, delta) },
                onSetWeight = { w -> onSetWeight(item.recipeId, w) },
                onRemove = { onRemoveItem(item.recipeId) },
                onNavigateToRecipe = { onNavigateToRecipe(item.recipeId) },
                onNavigateToProductCard = { onNavigateToProductCard(item.recipeId) }
            )
        }
    }
}

@Composable
private fun PrepItemCard(
    item: PrepListItem,
    onUpdateWeight: (Double) -> Unit,
    onSetWeight: (Double) -> Unit,
    onRemove: () -> Unit,
    onNavigateToRecipe: () -> Unit,
    onNavigateToProductCard: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prep_item_${item.recipeId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.recipeName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item.isUserRecipe) {
                            Spacer(modifier = Modifier.width(6.dp))
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
                    }
                    Text(
                        text = "کد: ${item.recipeId}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف از لیست",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weight Adjuster Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "وزن بچ تولید:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onUpdateWeight(-1.0) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "کاهش ۱ کیلوگرم", tint = KurdishCrimson)
                    }

                    Text(
                        text = "${item.targetWeightKg} کیلوگرم",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaffronGold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    IconButton(
                        onClick = { onUpdateWeight(1.0) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "افزایش ۱ کیلوگرم", tint = Color(0xFF2E7D32))
                    }
                }
            }

            // Quick weight shortcut buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(2.0, 5.0, 10.0, 20.0, 50.0).forEach { w ->
                    OutlinedButton(
                        onClick = { onSetWeight(w) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${w.toInt()}kg", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Links to Detail and Product Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateToRecipe) {
                    Text("مشاهده رسپی کامل", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onNavigateToProductCard) {
                    Text("کارت مشخصات محصول", fontSize = 12.sp, color = SaffronGold, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AggregatedTabContent(
    aggregated: List<AggregatedIngredient>
) {
    if (aggregated.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "هنوز ماده اولیه‌ای برای تجمیع وجود ندارد.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(
                    text = "لیست تجمیعی اقلام مصرفی: این لیست کلیه مواد اولیه مشترک در بچ‌های انتخابی را با هم جمع نموده تا انبارداری و توزین سریع‌تر انجام گیرد.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        items(aggregated) { ing ->
            AggregatedIngredientCard(ing)
        }
    }
}

@Composable
private fun AggregatedIngredientCard(ing: AggregatedIngredient) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SaffronGold)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = ing.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${ing.totalAmount} ${ing.unit}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = KurdishCrimson
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "سهم در محصولات:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ing.contributingRecipes.forEach { c ->
                        Text(
                            text = "• $c",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksTabContent(
    tasks: List<PrepTask>,
    onToggleTask: (String) -> Unit,
    onResetTasks: () -> Unit
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "هیچ مرحله اجرایی برای این لیست تولید تعریف نشده است.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val groupedTasks = tasks.groupBy { it.category }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "توالی مراحل استاندارد کارگاهی",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onResetTasks) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ریست کردن وضعیت مراحل", fontSize = 12.sp)
                }
            }
        }

        groupedTasks.forEach { (category, categoryTasks) ->
            item {
                Surface(
                    color = KurdishCrimson.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = KurdishCrimson
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            items(categoryTasks, key = { it.id }) { task ->
                TaskItemRow(task = task, onToggle = { onToggleTask(task.id) })
            }
        }
    }
}

@Composable
private fun TaskItemRow(
    task: PrepTask,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp).padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = task.stageTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = task.recipeName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SaffronGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.instructions,
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddRecipeToPrepDialog(
    allRecipes: List<Recipe>,
    onDismiss: () -> Unit,
    onAdd: (Recipe, Double, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var weightText by remember { mutableStateOf("5.0") }
    var notes by remember { mutableStateOf("") }

    val filtered = remember(allRecipes, searchQuery) {
        if (searchQuery.isBlank()) {
            allRecipes.take(20)
        } else {
            val q = searchQuery.trim().lowercase()
            allRecipes.filter { it.name.lowercase().contains(q) || it.mainProduct.lowercase().contains(q) }.take(25)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن محصول به برنامه تولید") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (selectedRecipe == null) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("جستجوی محصول یا گوشت پایه...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(filtered) { r ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRecipe = r }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(r.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(r.categoryNameFa, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                }
                                Text("انتخاب", color = SaffronGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            HorizontalDivider()
                        }
                    }
                } else {
                    val r = selectedRecipe!!
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(r.name, fontWeight = FontWeight.Bold)
                                Text(r.categoryNameFa, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { selectedRecipe = null }) {
                                Text("تغییر", color = KurdishCrimson)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("وزن بچ هدف (کیلوگرم)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("یادداشت تولید (اختیاری)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedRecipe != null) {
                        val w = weightText.toDoubleOrNull() ?: 1.0
                        onAdd(selectedRecipe!!, w, notes)
                    }
                },
                enabled = selectedRecipe != null,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal)
            ) {
                Text("افزودن به برنامه")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

private fun buildExportText(plan: com.example.data.model.CalculatedPrepPlan): String {
    val sb = StringBuilder()
    sb.appendLine("📋 برنامه تولید و تدارکات کارگاهی — Chef.M_K")
    sb.appendLine("🏷️ عنوان برنامه: ${plan.prepList.title}")
    sb.appendLine("⚖️ مجموع بچ‌های انتخابی:")
    plan.plans.forEach { p ->
        sb.appendLine("  • ${p.recipeName}: ${p.targetWeightKg} کیلوگرم")
    }
    sb.appendLine("\n📦 اقلام مصرفی تجمیع‌شده کارگاه:")
    plan.aggregatedIngredients.forEach { ing ->
        sb.appendLine("  - ${ing.name}: ${ing.totalAmount} ${ing.unit}")
    }
    sb.appendLine("\n🛠️ توالی مراحل آماده‌سازی:")
    plan.tasks.forEachIndexed { i, t ->
        val status = if (t.isCompleted) "[✓]" else "[ ]"
        sb.appendLine("$status ${i + 1}. [${t.category}] ${t.recipeName} - ${t.stageTitle}")
    }
    sb.appendLine("\n--- تهیه شده توسط سیستم تدارکات هوشمند Chef.M_K ---")
    return sb.toString()
}
