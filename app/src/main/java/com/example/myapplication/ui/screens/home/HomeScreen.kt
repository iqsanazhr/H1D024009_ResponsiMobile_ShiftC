package com.example.myapplication.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.EmptyView
import com.example.myapplication.ui.components.ErrorView
import com.example.myapplication.ui.components.LoadingView
import com.example.myapplication.ui.components.PokemonCard
import com.example.myapplication.ui.components.PokemonSearchBar

private val PokemonRed = Color(0xFFE53935)

/**
 * Layar utama Katalog dan Eksplorasi Pokémon
 * Menggunakan tema header merah resmi Pokédex dengan ikon pertempuran murni tanpa emotikon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPokemonClick: (Int) -> Unit,
    onBattleClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PokéDex Explorer",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black
                        )
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.CatchingPokemon,
                        contentDescription = "Pokeball Logo",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 4.dp)
                            .size(28.dp)
                    )
                },
                actions = {
                    // Tombol Battle berupa IconButton murni dengan ikon SportsKabaddi
                    IconButton(
                        onClick = onBattleClick,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(42.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.22f),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SportsKabaddi,
                                    contentDescription = "Battle Arena",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PokemonRed
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Banner Merah Pokédex melengkung yang menaungi Search Bar
            Surface(
                color = PokemonRed,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(bottom = 12.dp, top = 2.dp)) {
                    PokemonSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onClearQuery = { viewModel.clearSearchQuery() }
                    )
                }
            }

            // Area Konten Daftar Pokémon (Multi-State Handling)
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    // Loading State
                    uiState.isLoading && uiState.pokemonList.isEmpty() -> {
                        LoadingView(message = "Sedang mengambil data Pokémon...")
                    }

                    // Error State
                    uiState.errorMessage != null && uiState.pokemonList.isEmpty() -> {
                        ErrorView(
                            message = uiState.errorMessage ?: "Terjadi kesalahan.",
                            onRetry = { viewModel.loadPokemonList() }
                        )
                    }

                    // Empty Search State
                    uiState.filteredList.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                        EmptyView(query = uiState.searchQuery)
                    }

                    // Success State: Menampilkan LazyVerticalGrid dengan kartu modern
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.filteredList,
                                key = { it.id }
                            ) { pokemon ->
                                PokemonCard(
                                    pokemon = pokemon,
                                    onClick = { onPokemonClick(pokemon.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
