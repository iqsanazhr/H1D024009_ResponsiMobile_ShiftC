package com.example.myapplication.ui.screens.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.PokemonDetail
import com.example.myapplication.data.model.PokemonItem
import com.example.myapplication.data.model.TypeChartHelper
import com.example.myapplication.data.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State untuk Layar Simulasi Battle & Perbandingan Atribut Pokémon
 */
data class BattleUiState(
    val isLoading1: Boolean = false,
    val isLoading2: Boolean = false,
    val pokemon1: PokemonDetail? = null,
    val pokemon2: PokemonDetail? = null,
    val allPokemonList: List<PokemonItem> = emptyList(),
    val winnerName: String? = null,
    val isDraw: Boolean = false,
    val analysisText: String = "",
    val errorMessage: String? = null
)

class BattleViewModel(
    private val repository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BattleUiState())
    val uiState: StateFlow<BattleUiState> = _uiState.asStateFlow()

    init {
        loadAllPokemonForPicker()
    }

    /**
     * Memuat daftar seluruh Pokémon untuk dialog pemilihan
     */
    private fun loadAllPokemonForPicker() {
        viewModelScope.launch {
            val result = repository.getPokemonList()
            result.onSuccess { list ->
                _uiState.update { it.copy(allPokemonList = list) }
            }
        }
    }

    /**
     * Memuat kedua Pokémon untuk battle (default: Charizard #6 vs Blastoise #9)
     */
    fun initBattle(p1Id: String = "6", p2Id: String = "9") {
        loadPokemon1(p1Id)
        loadPokemon2(p2Id)
    }

    fun loadPokemon1(idOrName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading1 = true) }
            val result = repository.getPokemonDetail(idOrName)
            result.fold(
                onSuccess = { detail ->
                    _uiState.update { current ->
                        val updated = current.copy(isLoading1 = false, pokemon1 = detail)
                        calculateBattleOutcome(updated)
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading1 = false, errorMessage = error.localizedMessage) }
                }
            )
        }
    }

    fun loadPokemon2(idOrName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading2 = true) }
            val result = repository.getPokemonDetail(idOrName)
            result.fold(
                onSuccess = { detail ->
                    _uiState.update { current ->
                        val updated = current.copy(isLoading2 = false, pokemon2 = detail)
                        calculateBattleOutcome(updated)
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading2 = false, errorMessage = error.localizedMessage) }
                }
            )
        }
    }

    /**
     * Menukar posisi Pokémon A dan Pokémon B
     */
    fun swapPokemon() {
        _uiState.update { current ->
            val p1 = current.pokemon1
            val p2 = current.pokemon2
            val updated = current.copy(pokemon1 = p2, pokemon2 = p1)
            calculateBattleOutcome(updated)
        }
    }

    /**
     * Menghitung hasil perbandingan statistik dan simulasi kemenangan
     */
    private fun calculateBattleOutcome(state: BattleUiState): BattleUiState {
        val p1 = state.pokemon1 ?: return state
        val p2 = state.pokemon2 ?: return state

        val total1 = p1.stats.sumOf { it.value }
        val total2 = p2.stats.sumOf { it.value }

        // Keunggulan Tipe
        val (typeAdv1, typeAdv2) = TypeChartHelper.calculateTypeAdvantage(p1.types, p2.types)

        // Kalkulasi Skor Total Pertarungan: Total Stats + Bonus Tipe (50 poin per keunggulan tipe)
        val score1 = total1 + (typeAdv1 * 60)
        val score2 = total2 + (typeAdv2 * 60)

        val winner: String?
        val isDraw: Boolean
        val analysis: String

        if (score1 > score2) {
            winner = p1.displayName
            isDraw = false
            val reasons = mutableListOf<String>()
            if (typeAdv1 > typeAdv2) reasons.add("keunggulan efektivitas tipe (${p1.types.joinToString()})")
            if (total1 > total2) reasons.add("total statistik dasar lebih tinggi ($total1 vs $total2)")
            val reasonStr = if (reasons.isNotEmpty()) reasons.joinToString(" dan ") else "performa atribut seimbang"
            analysis = "$winner diprediksi memenangkan pertarungan berkat $reasonStr!"
        } else if (score2 > score1) {
            winner = p2.displayName
            isDraw = false
            val reasons = mutableListOf<String>()
            if (typeAdv2 > typeAdv1) reasons.add("keunggulan efektivitas tipe (${p2.types.joinToString()})")
            if (total2 > total1) reasons.add("total statistik dasar lebih tinggi ($total2 vs $total1)")
            val reasonStr = if (reasons.isNotEmpty()) reasons.joinToString(" dan ") else "performa atribut seimbang"
            analysis = "$winner diprediksi memenangkan pertarungan berkat $reasonStr!"
        } else {
            winner = null
            isDraw = true
            analysis = "Pertarungan sangat sengit dan berakhir imbang! Kedua Pokémon memiliki kekuatan statistik dan tipe yang setara."
        }

        return state.copy(
            winnerName = winner,
            isDraw = isDraw,
            analysisText = analysis
        )
    }
}
