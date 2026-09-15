package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CalculatedPrepPlan
import com.example.data.model.MeatCategory
import com.example.data.model.MeatCut
import com.example.data.model.PrepList
import com.example.data.model.PrepListItem
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.data.model.UserRecipeEntity
import com.example.data.repository.MeatKnowledgeData
import com.example.data.repository.PrepListCalculator
import com.example.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(application)

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing: StateFlow<Boolean> = _isInitializing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(RecipeCategory.ALL)
    val selectedCategory: StateFlow<RecipeCategory> = _selectedCategory.asStateFlow()

    private val _selectedRecipeId = MutableStateFlow<String?>(null)
    val selectedRecipeId: StateFlow<String?> = _selectedRecipeId.asStateFlow()

    // Base raw recipes from DB
    private val rawRecipes = repository.getAllRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered recipes
    val displayedRecipes: StateFlow<List<Recipe>> = combine(
        rawRecipes,
        _selectedCategory,
        _searchQuery
    ) { recipes, category, query ->
        var list = recipes
        if (category != RecipeCategory.ALL) {
            list = list.filter { it.category == category }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { r ->
                r.name.lowercase().contains(q) ||
                r.mainProduct.lowercase().contains(q) ||
                r.shortDescription.lowercase().contains(q) ||
                r.searchTags.any { it.lowercase().contains(q) } ||
                r.categoryNameFa.lowercase().contains(q)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<Recipe>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected recipe detail
    private val _currentRecipe = MutableStateFlow<Recipe?>(null)
    val currentRecipe: StateFlow<Recipe?> = _currentRecipe.asStateFlow()

    // Batch scaler state for detail view
    private val _scaleWeightGrams = MutableStateFlow(1000.0)
    val scaleWeightGrams: StateFlow<Double> = _scaleWeightGrams.asStateFlow()

    // ================= USER RECIPES STATE =================
    val userRecipes: StateFlow<List<Recipe>> = repository.getAllUserRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userRecipeCount: StateFlow<Int> = repository.getUserRecipeCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _userRecipeSearchQuery = MutableStateFlow("")
    val userRecipeSearchQuery: StateFlow<String> = _userRecipeSearchQuery.asStateFlow()

    private val _userRecipeCategory = MutableStateFlow(RecipeCategory.ALL)
    val userRecipeCategory: StateFlow<RecipeCategory> = _userRecipeCategory.asStateFlow()

    val displayedUserRecipes: StateFlow<List<Recipe>> = combine(
        userRecipes,
        _userRecipeCategory,
        _userRecipeSearchQuery
    ) { recipes, category, query ->
        var list = recipes
        if (category != RecipeCategory.ALL) {
            list = list.filter { it.category == category }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { r ->
                r.name.lowercase().contains(q) ||
                r.mainProduct.lowercase().contains(q) ||
                r.shortDescription.lowercase().contains(q) ||
                r.searchTags.any { it.lowercase().contains(q) } ||
                r.categoryNameFa.lowercase().contains(q)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All available recipes combined (Core 1000 + User Recipes) for selection lookups
    val allAvailableRecipes: StateFlow<List<Recipe>> = combine(
        rawRecipes,
        userRecipes
    ) { core, user ->
        core + user
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ================= PREP LISTS STATE =================
    val prepLists: StateFlow<List<PrepList>> = repository.getAllPrepLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activePrepListId = MutableStateFlow<Long?>(null)
    val activePrepListId: StateFlow<Long?> = _activePrepListId.asStateFlow()

    val activePrepList: StateFlow<PrepList?> = combine(
        prepLists,
        _activePrepListId
    ) { lists, currentId ->
        if (currentId != null) {
            lists.find { it.id == currentId } ?: lists.firstOrNull()
        } else {
            lists.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val calculatedPrepPlan: StateFlow<CalculatedPrepPlan?> = combine(
        activePrepList,
        allAvailableRecipes
    ) { activeList, allRecipes ->
        if (activeList == null || activeList.items.isEmpty()) {
            null
        } else {
            val recipesMap = allRecipes.associateBy { it.uniqueId }
            PrepListCalculator.calculatePrepPlan(activeList, recipesMap)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ================= MEAT KNOWLEDGE STATE =================
    private val _meatSearchQuery = MutableStateFlow("")
    val meatSearchQuery: StateFlow<String> = _meatSearchQuery.asStateFlow()

    private val _meatSelectedCategory = MutableStateFlow(MeatCategory.ALL)
    val meatSelectedCategory: StateFlow<MeatCategory> = _meatSelectedCategory.asStateFlow()

    val displayedMeatCuts: StateFlow<List<MeatCut>> = combine(
        _meatSelectedCategory,
        _meatSearchQuery
    ) { category, query ->
        MeatKnowledgeData.filterCuts(category, query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MeatKnowledgeData.cuts)

    private val _selectedMeatCut = MutableStateFlow<MeatCut?>(null)
    val selectedMeatCut: StateFlow<MeatCut?> = _selectedMeatCut.asStateFlow()

    init {
        viewModelScope.launch {
            _isInitializing.value = true
            repository.initializeIfNeeded()
            _isInitializing.value = false
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: RecipeCategory) {
        _selectedCategory.value = category
    }

    fun selectRecipe(recipe: Recipe) {
        _currentRecipe.value = recipe
        _scaleWeightGrams.value = recipe.baseWeight.coerceAtLeast(500.0)
    }

    fun selectRecipeById(id: String) {
        viewModelScope.launch {
            val found = repository.getRecipeByIdSync(id)
            if (found != null) {
                selectRecipe(found)
            }
        }
    }

    fun setScaleWeight(grams: Double) {
        _scaleWeightGrams.value = grams.coerceAtLeast(100.0)
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe.uniqueId, recipe.isFavorite)
            // If current recipe is being viewed, update local state as well
            if (_currentRecipe.value?.uniqueId == recipe.uniqueId) {
                _currentRecipe.value = _currentRecipe.value?.copy(isFavorite = !recipe.isFavorite)
            }
        }
    }

    fun saveUserNote(recipeId: String, note: String) {
        viewModelScope.launch {
            repository.updateUserNotes(recipeId, note)
            if (_currentRecipe.value?.uniqueId == recipeId) {
                _currentRecipe.value = _currentRecipe.value?.copy(userNotes = note)
            }
        }
    }

    // ================= USER RECIPE ACTIONS =================
    fun onUserRecipeSearchQueryChanged(q: String) {
        _userRecipeSearchQuery.value = q
    }

    fun onUserRecipeCategorySelected(cat: RecipeCategory) {
        _userRecipeCategory.value = cat
    }

    fun saveUserRecipe(entity: UserRecipeEntity, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveUserRecipe(entity)
            onSaved()
        }
    }

    fun deleteUserRecipe(uniqueId: String) {
        viewModelScope.launch {
            repository.deleteUserRecipe(uniqueId)
            if (_currentRecipe.value?.uniqueId == uniqueId) {
                _currentRecipe.value = null
            }
        }
    }

    fun duplicateRecipeToMyRecipes(
        sourceRecipe: Recipe,
        onComplete: (String) -> Unit
    ) {
        val newId = "MYREC-${System.currentTimeMillis() % 100000}"
        val newName = "${sourceRecipe.name} (شخصی کارگاه)"
        duplicateRecipeToMyRecipes(sourceRecipe, newId, newName, onComplete)
    }

    fun duplicateRecipeToMyRecipes(
        sourceRecipe: Recipe,
        newId: String,
        newName: String,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            val ingsArr = JSONArray()
            for (ing in sourceRecipe.ingredients) {
                val o = org.json.JSONObject()
                o.put("name", ing.name)
                o.put("amount", ing.amount)
                o.put("unit", ing.unit)
                o.put("isScalable", ing.isScalable)
                ingsArr.put(o)
            }

            val stepsArr = JSONArray()
            for (s in sourceRecipe.preparationSteps) {
                stepsArr.put(s)
            }

            val tagsArr = JSONArray()
            for (t in sourceRecipe.searchTags) {
                tagsArr.put(t)
            }

            val entity = UserRecipeEntity(
                uniqueId = newId,
                name = newName,
                category = sourceRecipe.category.id,
                subcategory = sourceRecipe.categoryNameFa,
                shortDescription = sourceRecipe.shortDescription,
                mainProduct = sourceRecipe.mainProduct,
                sourceType = "Chef.M_K Original",
                baseWeight = sourceRecipe.baseWeight,
                expectedFinalWeight = sourceRecipe.baseWeight * 0.9,
                portionWeight = 250.0,
                ingredientsJson = ingsArr.toString(),
                preparationStepsJson = stepsArr.toString(),
                preparationTime = sourceRecipe.preparationTime,
                marinationTime = sourceRecipe.marinationTime,
                cookingTime = sourceRecipe.cookingTime,
                cookingTemperature = sourceRecipe.cookingTemperature,
                cookingMethod = sourceRecipe.cookingMethod,
                equipment = "تجهیزات استاندارد کارگاهی",
                storageMethod = "سردخانه ۱ تا ۳ درجه",
                storageTemperature = "۲°C",
                storageDuration = sourceRecipe.recommendedStorageTime,
                packagingNotes = "بسته‌بندی وکیوم یا اسکین‌پک",
                foodSafetyNotes = sourceRecipe.foodSafetyAlert,
                professionalTips = sourceRecipe.professionalTips,
                commonMistakes = sourceRecipe.commonMistakes,
                storeUsage = sourceRecipe.storeUsage,
                searchTagsJson = tagsArr.toString(),
                isFavorite = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            repository.saveUserRecipe(entity)
            onComplete(newId)
        }
    }

    // ================= PREP LIST ACTIONS =================
    fun setActivePrepList(id: Long) {
        _activePrepListId.value = id
    }

    fun createPrepList(
        title: String,
        initialRecipe: Recipe? = null,
        initialWeightKg: Double = 1.0,
        onCreated: ((Long) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val initialItems = if (initialRecipe != null) {
                listOf(
                    PrepListItem(
                        recipeId = initialRecipe.uniqueId,
                        recipeName = initialRecipe.name,
                        isUserRecipe = initialRecipe.extraDetailsMap["isUserRecipe"] == "true",
                        targetWeightKg = initialWeightKg.coerceAtLeast(0.5),
                        notes = "فرآوری استاندارد"
                    )
                )
            } else {
                emptyList()
            }

            val newPrepList = PrepList(
                title = title.ifBlank { "تدارکات ${System.currentTimeMillis() % 10000}" },
                items = initialItems,
                completedTasks = emptySet()
            )
            val newId = repository.savePrepList(newPrepList)
            _activePrepListId.value = newId
            onCreated?.invoke(newId)
        }
    }

    fun addRecipeToPrepList(
        prepListId: Long,
        recipe: Recipe,
        targetWeightKg: Double,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val current = repository.getPrepListByIdSync(prepListId) ?: return@launch
            val existingItemIndex = current.items.indexOfFirst { it.recipeId == recipe.uniqueId }
            val newItems = current.items.toMutableList()
            if (existingItemIndex >= 0) {
                val existing = newItems[existingItemIndex]
                newItems[existingItemIndex] = existing.copy(
                    targetWeightKg = (existing.targetWeightKg + targetWeightKg * 10.0).toLong() / 10.0,
                    notes = if (notes.isNotBlank()) notes else existing.notes
                )
            } else {
                newItems.add(
                    PrepListItem(
                        recipeId = recipe.uniqueId,
                        recipeName = recipe.name,
                        isUserRecipe = recipe.extraDetailsMap["isUserRecipe"] == "true",
                        targetWeightKg = targetWeightKg.coerceAtLeast(0.5),
                        notes = notes
                    )
                )
            }
            val updated = current.copy(items = newItems, updatedAt = System.currentTimeMillis())
            repository.savePrepList(updated)
            _activePrepListId.value = prepListId
        }
    }

    fun removeRecipeFromPrepList(prepListId: Long, recipeId: String) {
        viewModelScope.launch {
            val current = repository.getPrepListByIdSync(prepListId) ?: return@launch
            val newItems = current.items.filter { it.recipeId != recipeId }
            val updated = current.copy(items = newItems, updatedAt = System.currentTimeMillis())
            repository.savePrepList(updated)
        }
    }

    fun updatePrepItemWeight(prepListId: Long, recipeId: String, deltaKg: Double) {
        viewModelScope.launch {
            val current = repository.getPrepListByIdSync(prepListId) ?: return@launch
            val index = current.items.indexOfFirst { it.recipeId == recipeId }
            if (index >= 0) {
                val newItems = current.items.toMutableList()
                val oldWeight = newItems[index].targetWeightKg
                val newWeight = (oldWeight + deltaKg).coerceAtLeast(0.5)
                newItems[index] = newItems[index].copy(targetWeightKg = (newWeight * 10.0).toLong() / 10.0)
                val updated = current.copy(items = newItems, updatedAt = System.currentTimeMillis())
                repository.savePrepList(updated)
            }
        }
    }

    fun setPrepItemWeight(prepListId: Long, recipeId: String, weightKg: Double) {
        viewModelScope.launch {
            val current = repository.getPrepListByIdSync(prepListId) ?: return@launch
            val index = current.items.indexOfFirst { it.recipeId == recipeId }
            if (index >= 0) {
                val newItems = current.items.toMutableList()
                newItems[index] = newItems[index].copy(targetWeightKg = weightKg.coerceAtLeast(0.5))
                val updated = current.copy(items = newItems, updatedAt = System.currentTimeMillis())
                repository.savePrepList(updated)
            }
        }
    }

    fun clearPrepList(prepListId: Long) {
        viewModelScope.launch {
            val current = repository.getPrepListByIdSync(prepListId) ?: return@launch
            val updated = current.copy(items = emptyList(), completedTasks = emptySet(), updatedAt = System.currentTimeMillis())
            repository.savePrepList(updated)
        }
    }

    fun renamePrepList(prepListId: Long, newTitle: String) {
        viewModelScope.launch {
            repository.renamePrepList(prepListId, newTitle.trim())
        }
    }

    fun duplicatePrepList(prepListId: Long) {
        viewModelScope.launch {
            val newId = repository.duplicatePrepList(prepListId)
            if (newId > 0) {
                _activePrepListId.value = newId
            }
        }
    }

    fun deletePrepList(prepListId: Long) {
        viewModelScope.launch {
            repository.deletePrepList(prepListId)
            if (_activePrepListId.value == prepListId) {
                _activePrepListId.value = null
            }
        }
    }

    fun togglePrepTask(prepListId: Long, taskId: String) {
        viewModelScope.launch {
            repository.togglePrepTaskCompletion(prepListId, taskId)
        }
    }

    fun resetPrepTasks(prepListId: Long) {
        viewModelScope.launch {
            repository.resetPrepListCompletion(prepListId)
        }
    }

    // ================= MEAT KNOWLEDGE ACTIONS =================
    fun onMeatSearchQueryChanged(q: String) {
        _meatSearchQuery.value = q
    }

    fun onMeatCategorySelected(category: MeatCategory) {
        _meatSelectedCategory.value = category
    }

    fun selectMeatCut(cut: MeatCut?) {
        _selectedMeatCut.value = cut
    }

    fun selectMeatCutById(id: String) {
        val cut = MeatKnowledgeData.getCutById(id)
        _selectedMeatCut.value = cut
    }
}
