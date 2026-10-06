package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Pokémon Element Type Colors
val TypeNormal = Color(0xFFA8A878)
val TypeFire = Color(0xFFF08030)
val TypeWater = Color(0xFF6890F0)
val TypeGrass = Color(0xFF78C850)
val TypeElectric = Color(0xFFF8D030)
val TypeIce = Color(0xFF98D8D8)
val TypeFighting = Color(0xFFC03028)
val TypePoison = Color(0xFFA040A0)
val TypeGround = Color(0xFFE0C068)
val TypeFlying = Color(0xFFA890F0)
val TypePsychic = Color(0xFFF85888)
val TypeBug = Color(0xFFA8B820)
val TypeRock = Color(0xFFB8A038)
val TypeGhost = Color(0xFF705898)
val TypeDragon = Color(0xFF7038F8)
val TypeSteel = Color(0xFFB8B8D0)
val TypeFairy = Color(0xFFEE99AC)
val TypeDark = Color(0xFF705848)

// Stat Progress Bar Colors
val StatHpColor = Color(0xFFFF5959)
val StatAttackColor = Color(0xFFF5AC78)
val StatDefenseColor = Color(0xFFFAE078)
val StatSpAtkColor = Color(0xFF9DB7F5)
val StatSpDefColor = Color(0xFFA7DB8D)
val StatSpeedColor = Color(0xFFFA92B2)

/**
 * Mendapatkan warna khas berdasarkan nama tipe Pokémon
 */
fun getPokemonTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "normal" -> TypeNormal
        "fire" -> TypeFire
        "water" -> TypeWater
        "grass" -> TypeGrass
        "electric" -> TypeElectric
        "ice" -> TypeIce
        "fighting" -> TypeFighting
        "poison" -> TypePoison
        "ground" -> TypeGround
        "flying" -> TypeFlying
        "psychic" -> TypePsychic
        "bug" -> TypeBug
        "rock" -> TypeRock
        "ghost" -> TypeGhost
        "dragon" -> TypeDragon
        "steel" -> TypeSteel
        "fairy" -> TypeFairy
        "dark" -> TypeDark
        else -> Color(0xFF888888)
    }
}

/**
 * Mendapatkan warna bar statistik Pokémon
 */
fun getStatColor(statName: String): Color {
    return when (statName.lowercase()) {
        "hp" -> StatHpColor
        "attack" -> StatAttackColor
        "defense" -> StatDefenseColor
        "special-attack" -> StatSpAtkColor
        "special-defense" -> StatSpDefColor
        "speed" -> StatSpeedColor
        else -> Color(0xFF4FC3F7)
    }
}