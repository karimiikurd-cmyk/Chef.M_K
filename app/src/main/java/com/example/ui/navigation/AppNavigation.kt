package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ui.screens.BatchScalerScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MarinadeBuilderScreen
import com.example.ui.screens.MeatCutDetailScreen
import com.example.ui.screens.MeatKnowledgeScreen
import com.example.ui.screens.MyRecipesScreen
import com.example.ui.screens.PrepListScreen
import com.example.ui.screens.ProductCardScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.RecipeEditorScreen
import com.example.ui.screens.SpiceCalculatorScreen
import com.example.ui.viewmodel.RecipeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
    object BatchScaler : Screen("batch_scaler")
    object MarinadeBuilder : Screen("marinade_builder")
    object SpiceCalculator : Screen("spice_calculator")
    object Favorites : Screen("favorites")
    object PrepList : Screen("prep_list")
    object MeatKnowledge : Screen("meat_knowledge")
    object MeatCutDetail : Screen("meat_cut_detail/{cutId}") {
        fun createRoute(cutId: String) = "meat_cut_detail/$cutId"
    }
    object ProductCard : Screen("product_card/{recipeId}") {
        fun createRoute(recipeId: String) = "product_card/$recipeId"
    }
    object MyRecipes : Screen("my_recipes")
    object RecipeEditor : Screen("recipe_editor?recipeId={recipeId}") {
        fun createRoute(recipeId: String? = null) =
            if (recipeId != null) "recipe_editor?recipeId=$recipeId" else "recipe_editor"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToRecipe = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onNavigateToBatchScaler = {
                    navController.navigate(Screen.BatchScaler.route)
                },
                onNavigateToMarinadeBuilder = {
                    navController.navigate(Screen.MarinadeBuilder.route)
                },
                onNavigateToSpiceCalc = {
                    navController.navigate(Screen.SpiceCalculator.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToPrepList = {
                    navController.navigate(Screen.PrepList.route)
                },
                onNavigateToMeatKnowledge = {
                    navController.navigate(Screen.MeatKnowledge.route)
                },
                onNavigateToMyRecipes = {
                    navController.navigate(Screen.MyRecipes.route)
                },
                onNavigateToProductCard = { rId ->
                    navController.navigate(Screen.ProductCard.createRoute(rId))
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToRecipe = { targetId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(targetId))
                },
                onNavigateToProductCard = { rId ->
                    navController.navigate(Screen.ProductCard.createRoute(rId))
                },
                onNavigateToPrepList = {
                    navController.navigate(Screen.PrepList.route)
                },
                onNavigateToMyRecipes = {
                    navController.navigate(Screen.MyRecipes.route)
                }
            )
        }

        composable(Screen.BatchScaler.route) {
            BatchScalerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToRecipe = { targetId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(targetId))
                }
            )
        }

        composable(Screen.MarinadeBuilder.route) {
            MarinadeBuilderScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SpiceCalculator.route) {
            SpiceCalculatorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToRecipe = { targetId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(targetId))
                }
            )
        }

        composable(Screen.PrepList.route) {
            PrepListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToRecipe = { rId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(rId))
                },
                onNavigateToProductCard = { rId ->
                    navController.navigate(Screen.ProductCard.createRoute(rId))
                }
            )
        }

        composable(Screen.MeatKnowledge.route) {
            MeatKnowledgeScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToCutDetail = { cutId ->
                    navController.navigate(Screen.MeatCutDetail.createRoute(cutId))
                }
            )
        }

        composable(
            route = Screen.MeatCutDetail.route,
            arguments = listOf(navArgument("cutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cutId = backStackEntry.arguments?.getString("cutId") ?: ""
            MeatCutDetailScreen(
                cutId = cutId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToRecipe = { rId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(rId))
                }
            )
        }

        composable(
            route = Screen.ProductCard.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            ProductCardScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToRecipe = { rId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(rId))
                },
                onNavigateToPrepList = {
                    navController.navigate(Screen.PrepList.route)
                }
            )
        }

        composable(Screen.MyRecipes.route) {
            MyRecipesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToCreate = {
                    navController.navigate(Screen.RecipeEditor.createRoute(null))
                },
                onNavigateToEdit = { rId ->
                    navController.navigate(Screen.RecipeEditor.createRoute(rId))
                },
                onNavigateToRecipe = { rId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(rId))
                },
                onNavigateToProductCard = { rId ->
                    navController.navigate(Screen.ProductCard.createRoute(rId))
                },
                onNavigateToPrepList = {
                    navController.navigate(Screen.PrepList.route)
                }
            )
        }

        composable(
            route = Screen.RecipeEditor.route,
            arguments = listOf(navArgument("recipeId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            RecipeEditorScreen(
                editingRecipeId = recipeId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSavedSuccessfully = { savedId ->
                    navController.popBackStack()
                    navController.navigate(Screen.RecipeDetail.createRoute(savedId))
                }
            )
        }
    }
}
