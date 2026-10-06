package com.example.myapplication.ui.navigation

/**
 * Rute navigasi aplikasi
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")

    data object Detail : Screen("detail_screen/{pokemonId}") {
        const val ARG_POKEMON_ID = "pokemonId"

        fun createRoute(pokemonId: Int): String {
            return "detail_screen/$pokemonId"
        }
    }
}
