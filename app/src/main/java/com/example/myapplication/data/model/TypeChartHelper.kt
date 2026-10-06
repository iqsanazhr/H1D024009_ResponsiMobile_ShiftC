package com.example.myapplication.data.model

/**
 * Helper untuk menghitung efektivitas tipe (Type Effectiveness / Advantage)
 * pada simulasi pertarungan Pokémon
 */
object TypeChartHelper {

    // Pemetaan kelemahan tipe (Attacker -> Defender yang menerima damage 2x / Super Effective)
    private val superEffectiveMap: Map<String, List<String>> = mapOf(
        "fire" to listOf("grass", "ice", "bug", "steel"),
        "water" to listOf("fire", "ground", "rock"),
        "grass" to listOf("water", "ground", "rock"),
        "electric" to listOf("water", "flying"),
        "ice" to listOf("grass", "ground", "flying", "dragon"),
        "fighting" to listOf("normal", "ice", "rock", "dark", "steel"),
        "poison" to listOf("grass", "fairy"),
        "ground" to listOf("fire", "electric", "poison", "rock", "steel"),
        "flying" to listOf("grass", "fighting", "bug"),
        "psychic" to listOf("fighting", "poison"),
        "bug" to listOf("grass", "psychic", "dark"),
        "rock" to listOf("fire", "ice", "flying", "bug"),
        "ghost" to listOf("psychic", "ghost"),
        "dragon" to listOf("dragon"),
        "steel" to listOf("ice", "rock", "fairy"),
        "fairy" to listOf("fighting", "dragon", "dark")
    )

    /**
     * Memeriksa apakah attackerType memiliki keunggulan atas defenderType
     */
    fun isSuperEffective(attackerType: String, defenderType: String): Boolean {
        return superEffectiveMap[attackerType.lowercase()]?.contains(defenderType.lowercase()) == true
    }

    /**
     * Menghitung skor keunggulan tipe Pokémon 1 terhadap Pokémon 2
     */
    fun calculateTypeAdvantage(types1: List<String>, types2: List<String>): Pair<Int, Int> {
        var score1 = 0
        var score2 = 0

        for (t1 in types1) {
            for (t2 in types2) {
                if (isSuperEffective(t1, t2)) score1++
                if (isSuperEffective(t2, t1)) score2++
            }
        }

        return Pair(score1, score2)
    }
}
