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

    data object Battle : Screen("battle_screen?p1={p1}&p2={p2}") {
        const val ARG_P1 = "p1"
        const val ARG_P2 = "p2"

        fun createRoute(p1: Int = 6, p2: Int = 9): String {
            return "battle_screen?p1=$p1&p2=$p2"
        }
    }
}
