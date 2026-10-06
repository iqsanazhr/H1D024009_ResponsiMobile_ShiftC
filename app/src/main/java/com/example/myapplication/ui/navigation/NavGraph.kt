package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
                }
            )
        }
    }
}
