package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.PrepListDao
import com.example.data.local.RecipeDao
import com.example.data.local.UserRecipeDao
import com.example.data.model.IngredientItem
import com.example.data.model.PrepList
import com.example.data.model.PrepListEntity
import com.example.data.model.PrepListItem
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory
import com.example.data.model.RecipeEntity
import com.example.data.model.UserRecipeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader

class RecipeRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao: RecipeDao = database.recipeDao()
    private val userDao: UserRecipeDao = database.userRecipeDao()
    private val prepDao: PrepListDao = database.prepListDao()

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        try {
            val count = dao.getCount()
            val sample = dao.getRecipeByIdSync("CK-001")
            val needsUpdate = (count < 1000 || sample == null || !sample.preparationStepsJson.contains("مرحله اول - آماده‌سازی و برش دقیق گوشت"))

            if (needsUpdate) {
                Log.d("RecipeRepository", "Pre-populating 1000 professional recipes with exhaustive step instructions from assets...")
                
                // Map existing user customizations to preserve them
                val existingCustoms = mutableMapOf<String, Pair<Boolean, String>>()
                if (count > 0) {
                    val currentList = dao.getAllRecipesSync()
                    for (rec in currentList) {
                        if (rec.isFavorite || rec.userNotes.isNotBlank()) {
                            existingCustoms[rec.uniqueId] = Pair(rec.isFavorite, rec.userNotes)
                        }
                    }
                }

                val inputStream = context.assets.open("recipes.json")
                val reader = InputStreamReader(inputStream, Charsets.UTF_8)
                val jsonString = reader.readText()
                reader.close()
                inputStream.close()

                val jsonArray = JSONArray(jsonString)
                val entities = mutableListOf<RecipeEntity>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val uid = obj.getString("uniqueId")
                    val name = obj.getString("name")
                    val category = obj.getString("category")
                    val categoryNameFa = obj.optString("categoryNameFa", "")
                    val shortDescription = obj.optString("shortDescription", "")
                    val mainProduct = obj.optString("mainProduct", "")
                    val baseWeight = obj.optDouble("baseWeight", 1000.0)
                    val ingredientsJson = obj.optJSONArray("ingredients")?.toString() ?: "[]"
                    val stepsJson = obj.optJSONArray("preparationSteps")?.toString() ?: "[]"
                    val prepTime = obj.optString("preparationTime", "")
                    val marinTime = obj.optString("marinationTime", "")
                    val cookTime = obj.optString("cookingTime", "")
                    val cookTemp = obj.optString("cookingTemperature", "")
                    val cookMethod = obj.optString("cookingMethod", "")
                    val profTips = obj.optString("professionalTips", "")
                    val mistakes = obj.optString("commonMistakes", "")
                    val storageInst = obj.optString("storageInstructions", "")
                    val recStorageTime = obj.optString("recommendedStorageTime", "")
                    val storeUsage = obj.optString("storeUsage", "")
                    val tagsJson = obj.optJSONArray("searchTags")?.toString() ?: "[]"
                    val foodSafety = obj.optString("foodSafetyAlert", "")

                    // Check extra details
                    val extraObj = JSONObject()
                    val detailKeys = listOf(
                        "chickenDetails", "kebabDetails", "steakDetails",
                        "burgerDetails", "sauceDetails", "spiceDetails",
                        "butterDetails", "oilDetails", "saladDetails"
                    )
                    for (dk in detailKeys) {
                        if (obj.has(dk)) {
                            extraObj.put(dk, obj.getJSONObject(dk))
                        }
                    }

                    val relSauces = obj.optJSONArray("relatedSauceIds")?.toString() ?: "[]"
                    val relSpices = obj.optJSONArray("relatedSpiceIds")?.toString() ?: "[]"
                    val relSalads = obj.optJSONArray("relatedSaladIds")?.toString() ?: "[]"

                    val customData = existingCustoms[uid]
                    val isFav = customData?.first ?: false
                    val userNote = customData?.second ?: ""

                    entities.add(
                        RecipeEntity(
                            uniqueId = uid,
                            name = name,
                            category = category,
                            categoryNameFa = categoryNameFa,
                            shortDescription = shortDescription,
                            mainProduct = mainProduct,
                            baseWeight = baseWeight,
                            ingredientsJson = ingredientsJson,
                            preparationStepsJson = stepsJson,
                            preparationTime = prepTime,
                            marinationTime = marinTime,
                            cookingTime = cookTime,
                            cookingTemperature = cookTemp,
                            cookingMethod = cookMethod,
                            professionalTips = profTips,
                            commonMistakes = mistakes,
                            storageInstructions = storageInst,
                            recommendedStorageTime = recStorageTime,
                            storeUsage = storeUsage,
                            searchTagsJson = tagsJson,
                            foodSafetyAlert = foodSafety,
                            extraDetailsJson = extraObj.toString(),
                            relatedSauceIdsJson = relSauces,
                            relatedSpiceIdsJson = relSpices,
                            relatedSaladIdsJson = relSalads,
                            isFavorite = isFav,
                            userNotes = userNote
                        )
                    )
                }

                dao.insertAll(entities)
                Log.d("RecipeRepository", "Successfully inserted ${entities.size} recipes into Room.")
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error initializing recipes: ${e.message}", e)
        }
    }

    fun getAllRecipes(): Flow<List<Recipe>> =
        dao.getAllRecipes().map { list -> list.map { it.toDomain() } }

    fun getRecipesByCategory(category: RecipeCategory): Flow<List<Recipe>> {
        return if (category == RecipeCategory.ALL) {
            getAllRecipes()
        } else {
            dao.getRecipesByCategory(category.id).map { list -> list.map { it.toDomain() } }
        }
    }

    fun getFavorites(): Flow<List<Recipe>> =
        dao.getFavorites().map { list -> list.map { it.toDomain() } }

    fun searchRecipes(query: String): Flow<List<Recipe>> =
        dao.searchRecipes(query).map { list -> list.map { it.toDomain() } }

    fun getRecipeById(uniqueId: String): Flow<Recipe?> =
        dao.getRecipeById(uniqueId).map { coreEntity ->
            if (coreEntity != null) {
                coreEntity.toDomain()
            } else {
                userDao.getUserRecipeByIdSync(uniqueId)?.toUserDomain()
            }
        }

    suspend fun getRecipeByIdSync(uniqueId: String): Recipe? = withContext(Dispatchers.IO) {
        val core = dao.getRecipeByIdSync(uniqueId)?.toDomain()
        if (core != null) return@withContext core
        userDao.getUserRecipeByIdSync(uniqueId)?.toUserDomain()
    }

    suspend fun toggleFavorite(uniqueId: String, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        if (uniqueId.startsWith("MY-") || userDao.getUserRecipeByIdSync(uniqueId) != null) {
            userDao.updateFavorite(uniqueId, !currentStatus)
        } else {
            dao.updateFavorite(uniqueId, !currentStatus)
        }
    }

    suspend fun updateUserNotes(uniqueId: String, notes: String) = withContext(Dispatchers.IO) {
        dao.updateUserNotes(uniqueId, notes)
    }

    // ================= USER RECIPES =================
    fun getAllUserRecipes(): Flow<List<Recipe>> =
        userDao.getAllUserRecipes().map { list -> list.map { it.toUserDomain() } }

    fun getAllUserRecipeEntities(): Flow<List<UserRecipeEntity>> =
        userDao.getAllUserRecipes()

    fun getUserRecipeCount(): Flow<Int> = userDao.getUserRecipeCount()

    suspend fun saveUserRecipe(entity: UserRecipeEntity) = withContext(Dispatchers.IO) {
        userDao.insertUserRecipe(entity)
    }

    suspend fun deleteUserRecipe(uniqueId: String) = withContext(Dispatchers.IO) {
        userDao.deleteUserRecipe(uniqueId)
    }

    suspend fun getUserRecipeByIdSync(uniqueId: String): UserRecipeEntity? = withContext(Dispatchers.IO) {
        userDao.getUserRecipeByIdSync(uniqueId)
    }

    // ================= PREP LISTS =================
    fun getAllPrepLists(): Flow<List<PrepList>> =
        prepDao.getAllPrepLists().map { list -> list.map { it.toDomain() } }

    fun getPrepListById(id: Long): Flow<PrepList?> =
        prepDao.getPrepListById(id).map { it?.toDomain() }

    suspend fun getPrepListByIdSync(id: Long): PrepList? = withContext(Dispatchers.IO) {
        prepDao.getPrepListByIdSync(id)?.toDomain()
    }

    suspend fun savePrepList(prepList: PrepList): Long = withContext(Dispatchers.IO) {
        prepDao.insertPrepList(prepList.toEntity())
    }

    suspend fun deletePrepList(id: Long) = withContext(Dispatchers.IO) {
        prepDao.deletePrepList(id)
    }

    suspend fun togglePrepTaskCompletion(prepListId: Long, taskId: String) = withContext(Dispatchers.IO) {
        val current = prepDao.getPrepListByIdSync(prepListId) ?: return@withContext
        val domain = current.toDomain()
        val newSet = domain.completedTasks.toMutableSet()
        if (newSet.contains(taskId)) {
            newSet.remove(taskId)
        } else {
            newSet.add(taskId)
        }
        val updated = domain.copy(completedTasks = newSet, updatedAt = System.currentTimeMillis())
        prepDao.updatePrepList(updated.toEntity())
    }

    suspend fun resetPrepListCompletion(prepListId: Long) = withContext(Dispatchers.IO) {
        val current = prepDao.getPrepListByIdSync(prepListId) ?: return@withContext
        val domain = current.toDomain()
        val updated = domain.copy(completedTasks = emptySet(), updatedAt = System.currentTimeMillis())
        prepDao.updatePrepList(updated.toEntity())
    }

    suspend fun duplicatePrepList(prepListId: Long): Long = withContext(Dispatchers.IO) {
        val current = prepDao.getPrepListByIdSync(prepListId) ?: return@withContext -1L
        val domain = current.toDomain()
        val copy = domain.copy(
            id = 0,
            title = "${domain.title} (کپی)",
            completedTasks = emptySet(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        prepDao.insertPrepList(copy.toEntity())
    }

    suspend fun renamePrepList(prepListId: Long, newTitle: String) = withContext(Dispatchers.IO) {
        val current = prepDao.getPrepListByIdSync(prepListId) ?: return@withContext
        val domain = current.toDomain()
        val updated = domain.copy(title = newTitle, updatedAt = System.currentTimeMillis())
        prepDao.updatePrepList(updated.toEntity())
    }

    fun UserRecipeEntity.toUserDomain(): Recipe {
        val ingsList = mutableListOf<IngredientItem>()
        try {
            val ingsArr = JSONArray(ingredientsJson)
            for (i in 0 until ingsArr.length()) {
                val item = ingsArr.getJSONObject(i)
                ingsList.add(
                    IngredientItem(
                        name = item.optString("name", ""),
                        amount = item.optDouble("amount", 0.0),
                        unit = item.optString("unit", "گرم"),
                        isScalable = item.optBoolean("isScalable", true)
                    )
                )
            }
        } catch (_: Exception) {}

        val stepsList = mutableListOf<String>()
        try {
            val stepsArr = JSONArray(preparationStepsJson)
            for (i in 0 until stepsArr.length()) {
                stepsList.add(stepsArr.getString(i))
            }
        } catch (_: Exception) {}

        val tagsList = mutableListOf<String>()
        try {
            val tagsArr = JSONArray(searchTagsJson)
            for (i in 0 until tagsArr.length()) {
                tagsList.add(tagsArr.getString(i))
            }
        } catch (_: Exception) {}

        val extrasMap = mutableMapOf<String, String>()
        extrasMap["isUserRecipe"] = "true"
        extrasMap["sourceType"] = sourceType
        extrasMap["subcategory"] = subcategory
        extrasMap["equipment"] = equipment
        extrasMap["packagingNotes"] = packagingNotes
        extrasMap["foodSafetyNotes"] = foodSafetyNotes
        extrasMap["expectedFinalWeight"] = expectedFinalWeight.toString()
        extrasMap["portionWeight"] = portionWeight.toString()

        return Recipe(
            uniqueId = uniqueId,
            name = name,
            category = RecipeCategory.fromId(category),
            categoryNameFa = RecipeCategory.fromId(category).titleFa,
            shortDescription = shortDescription,
            mainProduct = mainProduct,
            baseWeight = baseWeight,
            ingredients = ingsList,
            preparationSteps = stepsList,
            preparationTime = preparationTime,
            marinationTime = marinationTime,
            cookingTime = cookingTime,
            cookingTemperature = cookingTemperature,
            cookingMethod = cookingMethod,
            professionalTips = professionalTips,
            commonMistakes = commonMistakes,
            storageInstructions = if (storageMethod.isNotBlank()) "$storageMethod - $storageTemperature ($storageDuration)" else "",
            recommendedStorageTime = storageDuration,
            storeUsage = storeUsage,
            searchTags = tagsList,
            foodSafetyAlert = foodSafetyNotes,
            extraDetailsMap = extrasMap,
            relatedSauceIds = emptyList(),
            relatedSpiceIds = emptyList(),
            relatedSaladIds = emptyList(),
            isFavorite = isFavorite,
            userNotes = ""
        )
    }

    private fun com.example.data.model.PrepListEntity.toDomain(): com.example.data.model.PrepList {
        val itemsList = mutableListOf<com.example.data.model.PrepListItem>()
        try {
            val arr = JSONArray(itemsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                itemsList.add(
                    com.example.data.model.PrepListItem(
                        recipeId = obj.getString("recipeId"),
                        recipeName = obj.optString("recipeName", ""),
                        isUserRecipe = obj.optBoolean("isUserRecipe", false),
                        targetWeightKg = obj.optDouble("targetWeightKg", 1.0),
                        notes = obj.optString("notes", "")
                    )
                )
            }
        } catch (_: Exception) {}

        val tasksSet = mutableSetOf<String>()
        try {
            val arr = JSONArray(completedTasksJson)
            for (i in 0 until arr.length()) {
                tasksSet.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        return com.example.data.model.PrepList(
            id = id,
            title = title,
            items = itemsList,
            completedTasks = tasksSet,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun com.example.data.model.PrepList.toEntity(): com.example.data.model.PrepListEntity {
        val itemsArr = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("recipeId", item.recipeId)
            obj.put("recipeName", item.recipeName)
            obj.put("isUserRecipe", item.isUserRecipe)
            obj.put("targetWeightKg", item.targetWeightKg)
            obj.put("notes", item.notes)
            itemsArr.put(obj)
        }

        val tasksArr = JSONArray()
        for (taskId in completedTasks) {
            tasksArr.put(taskId)
        }

        return com.example.data.model.PrepListEntity(
            id = id,
            title = title,
            itemsJson = itemsArr.toString(),
            completedTasksJson = tasksArr.toString(),
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun RecipeEntity.toDomain(): Recipe {
        val ingsList = mutableListOf<IngredientItem>()
        try {
            val ingsArr = JSONArray(ingredientsJson)
            for (i in 0 until ingsArr.length()) {
                val item = ingsArr.getJSONObject(i)
                ingsList.add(
                    IngredientItem(
                        name = item.optString("name", ""),
                        amount = item.optDouble("amount", 0.0),
                        unit = item.optString("unit", "گرم"),
                        isScalable = item.optBoolean("isScalable", true)
                    )
                )
            }
        } catch (_: Exception) {}

        val stepsList = mutableListOf<String>()
        try {
            val stepsArr = JSONArray(preparationStepsJson)
            for (i in 0 until stepsArr.length()) {
                stepsList.add(stepsArr.getString(i))
            }
        } catch (_: Exception) {}

        val tagsList = mutableListOf<String>()
        try {
            val tagsArr = JSONArray(searchTagsJson)
            for (i in 0 until tagsArr.length()) {
                tagsList.add(tagsArr.getString(i))
            }
        } catch (_: Exception) {}

        val extrasMap = mutableMapOf<String, String>()
        try {
            val extrasObj = JSONObject(extraDetailsJson)
            val keys = extrasObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val subObj = extrasObj.getJSONObject(k)
                val subKeys = subObj.keys()
                while (subKeys.hasNext()) {
                    val sk = subKeys.next()
                    extrasMap["$k.$sk"] = subObj.optString(sk, "")
                }
            }
        } catch (_: Exception) {}

        fun parseIds(json: String): List<String> {
            val res = mutableListOf<String>()
            try {
                val arr = JSONArray(json)
                for (i in 0 until arr.length()) res.add(arr.getString(i))
            } catch (_: Exception) {}
            return res
        }

        return Recipe(
            uniqueId = uniqueId,
            name = name,
            category = RecipeCategory.fromId(category),
            categoryNameFa = categoryNameFa,
            shortDescription = shortDescription,
            mainProduct = mainProduct,
            baseWeight = baseWeight,
            ingredients = ingsList,
            preparationSteps = stepsList,
            preparationTime = preparationTime,
            marinationTime = marinationTime,
            cookingTime = cookingTime,
            cookingTemperature = cookingTemperature,
            cookingMethod = cookingMethod,
            professionalTips = professionalTips,
            commonMistakes = commonMistakes,
            storageInstructions = storageInstructions,
            recommendedStorageTime = recommendedStorageTime,
            storeUsage = storeUsage,
            searchTags = tagsList,
            foodSafetyAlert = foodSafetyAlert,
            extraDetailsMap = extrasMap,
            relatedSauceIds = parseIds(relatedSauceIdsJson),
            relatedSpiceIds = parseIds(relatedSpiceIdsJson),
            relatedSaladIds = parseIds(relatedSaladIdsJson),
            isFavorite = isFavorite,
            userNotes = userNotes
        )
    }
}
