package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.battle.BattleScreen
import com.example.myapplication.ui.screens.detail.DetailScreen
import com.example.myapplication.ui.screens.home.HomeScreen

/**
 * NavGraph yang mengelola navigasi antar screen dalam aplikasi
 */
@Composable
fun PokemonNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Layar Katalog Utama
        composable(route = Screen.Home.route) {
            HomeScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate(Screen.Detail.createRoute(pokemonId))
                },
                onBattleClick = {
                    navController.navigate(Screen.Battle.createRoute(6, 9))
                }
            )
        }

        // Layar Detail Pokémon
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument(Screen.Detail.ARG_POKEMON_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getString(Screen.Detail.ARG_POKEMON_ID) ?: "1"
            DetailScreen(
                pokemonId = pokemonId,
                onBackClick = {
                    navController.popBackStack()
                },
                onBattleClick = { id ->
                    val rivalId = if (id == 6) 9 else if (id == 9) 3 else if (id == 3) 6 else if (id == 25) 133 else 6
                    navController.navigate(Screen.Battle.createRoute(id, rivalId))
                }
            )
        }

        // Layar Simulasi Battle & Komparasi Pokémon
        composable(
            route = Screen.Battle.route,
            arguments = listOf(
                navArgument(Screen.Battle.ARG_P1) {
                    type = NavType.StringType
                    defaultValue = "6"
                },
                navArgument(Screen.Battle.ARG_P2) {
                    type = NavType.StringType
                    defaultValue = "9"
                }
            )
        ) { backStackEntry ->
            val p1 = backStackEntry.arguments?.getString(Screen.Battle.ARG_P1) ?: "6"
            val p2 = backStackEntry.arguments?.getString(Screen.Battle.ARG_P2) ?: "9"
            BattleScreen(
                initialP1Id = p1,
                initialP2Id = p2,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
