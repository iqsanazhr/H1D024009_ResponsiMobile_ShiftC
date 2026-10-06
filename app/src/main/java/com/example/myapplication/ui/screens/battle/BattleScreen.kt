package com.example.myapplication.ui.screens.battle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.model.PokemonDetail
import com.example.myapplication.data.model.PokemonItem
import com.example.myapplication.ui.components.LoadingView
import com.example.myapplication.ui.components.TypeBadge
import com.example.myapplication.ui.theme.getPokemonTypeColor

/**
 * Layar Simulasi Battle & Perbandingan Atribut Head-to-Head Pokémon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    initialP1Id: String = "6",
    initialP2Id: String = "9",
    onBackClick: () -> Unit,
    viewModel: BattleViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectingForSide by remember { mutableStateOf<Int?>(null) } // 1: Pokémon 1, 2: Pokémon 2

    LaunchedEffect(key1 = initialP1Id, key2 = initialP2Id) {
        viewModel.initBattle(initialP1Id, initialP2Id)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Battle Arena & Komparasi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(42.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shadowElevation = 3.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section 1: Head-to-Head Matchup Arena Card
            MatchupArena(
                pokemon1 = uiState.pokemon1,
                pokemon2 = uiState.pokemon2,
                isLoading1 = uiState.isLoading1,
                isLoading2 = uiState.isLoading2,
                onSelectPokemon1 = { selectingForSide = 1 },
                onSelectPokemon2 = { selectingForSide = 2 },
                onSwap = { viewModel.swapPokemon() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Banner Hasil Simulasi & Prediksi Pemenang
            if (uiState.pokemon1 != null && uiState.pokemon2 != null) {
                BattleOutcomeCard(
                    winnerName = uiState.winnerName,
                    isDraw = uiState.isDraw,
                    analysis = uiState.analysisText
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Perbandingan Statistik Detail (Head-to-Head Stats Breakdown)
                StatComparisonSection(
                    p1 = uiState.pokemon1!!,
                    p2 = uiState.pokemon2!!
                )
            }
        }
    }

    // Modal Dialog Pemilihan Pokémon
    selectingForSide?.let { side ->
        PokemonPickerDialog(
            pokemonList = uiState.allPokemonList,
            onDismiss = { selectingForSide = null },
            onSelect = { selectedId ->
                if (side == 1) {
                    viewModel.loadPokemon1(selectedId.toString())
                } else {
                    viewModel.loadPokemon2(selectedId.toString())
                }
                selectingForSide = null
            }
        )
    }
}

/**
 * Kartu Arena Head-to-Head dengan tombol Tukar & Ganti Pokémon
 */
@Composable
private fun MatchupArena(
    pokemon1: PokemonDetail?,
    pokemon2: PokemonDetail?,
    isLoading1: Boolean,
    isLoading2: Boolean,
    onSelectPokemon1: () -> Unit,
    onSelectPokemon2: () -> Unit,
    onSwap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sisi Kiri (Pokémon 1)
            Box(modifier = Modifier.weight(1f)) {
                FighterCard(
                    pokemon = pokemon1,
                    isLoading = isLoading1,
                    onSelect = onSelectPokemon1
                )
            }

            // Bagian Tengah: Badge VS & Tombol Swap
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFD32F2F),
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "VS",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = onSwap,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Tukar Posisi",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Sisi Kanan (Pokémon 2)
            Box(modifier = Modifier.weight(1f)) {
                FighterCard(
                    pokemon = pokemon2,
                    isLoading = isLoading2,
                    onSelect = onSelectPokemon2
                )
            }
        }
    }
}

@Composable
private fun FighterCard(
    pokemon: PokemonDetail?,
    isLoading: Boolean,
    onSelect: () -> Unit
) {
    val typeColor = if (pokemon != null) getPokemonTypeColor(pokemon.primaryType) else MaterialTheme.colorScheme.primary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else if (pokemon != null) {
            // Container Gambar dengan latar warna tipe
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                typeColor.copy(alpha = 0.5f),
                                typeColor.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pokemon.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = pokemon.displayName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(90.dp)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pokemon.formattedId,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Text(
                text = pokemon.displayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            TypeBadge(type = pokemon.primaryType)
        }

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(onClick = onSelect) {
            Icon(
                imageVector = Icons.Default.ChangeCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Ganti", style = MaterialTheme.typography.labelMedium)
        }
    }
}

/**
 * Kartu Hasil Analisis Pertarungan
 */
@Composable
private fun BattleOutcomeCard(
    winnerName: String?,
    isDraw: Boolean,
    analysis: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Piala",
                    tint = if (isDraw) MaterialTheme.colorScheme.secondary else Color(0xFFFFB300),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isDraw) "Hasil Imbang!" else "Prediksi Pemenang: $winnerName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = analysis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Tabel Perbandingan Statistik Head-to-Head
 */
@Composable
private fun StatComparisonSection(
    p1: PokemonDetail,
    p2: PokemonDetail
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Perbandingan Statistik Detail",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Header kolom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = p1.displayName,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "Atribut",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = p2.displayName,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            // Stat baris demi baris
            val statsMap1 = p1.stats.associateBy { it.name.lowercase() }
            val statsMap2 = p2.stats.associateBy { it.name.lowercase() }

            val statKeys = listOf(
                "hp" to "HP",
                "attack" to "Attack",
                "defense" to "Defense",
                "special-attack" to "Sp. Atk",
                "special-defense" to "Sp. Def",
                "speed" to "Speed"
            )

            statKeys.forEach { (key, label) ->
                val val1 = statsMap1[key]?.value ?: 0
                val val2 = statsMap2[key]?.value ?: 0
                StatCompareRow(label = label, val1 = val1, val2 = val2)
            }

            // Total Stats
            val total1 = p1.stats.sumOf { it.value }
            val total2 = p2.stats.sumOf { it.value }
            StatCompareRow(label = "Total Stats", val1 = total1, val2 = total2, isTotal = true)

            // Fisik
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${p1.weightInKg} kg", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                Text(text = "Berat", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.outline)
                Text(text = "${p2.weightInKg} kg", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${p1.heightInMeters} m", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                Text(text = "Tinggi", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.outline)
                Text(text = "${p2.heightInMeters} m", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
private fun StatCompareRow(
    label: String,
    val1: Int,
    val2: Int,
    isTotal: Boolean = false
) {
    val winColor = Color(0xFF2E7D32) // Green accent for higher stat
    val normalColor = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isTotal) 8.dp else 4.dp)
            .background(
                if (isTotal) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = if (isTotal) 8.dp else 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Angka Sisi 1
        Text(
            text = "$val1",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (val1 > val2 || isTotal) FontWeight.Bold else FontWeight.Normal,
                color = if (val1 > val2) winColor else normalColor
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
                color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Angka Sisi 2
        Text(
            text = "$val2",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (val2 > val1 || isTotal) FontWeight.Bold else FontWeight.Normal,
                color = if (val2 > val1) winColor else normalColor
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Dialog Pemilihan Pokémon dengan Pencarian Instan
 */
@Composable
private fun PokemonPickerDialog(
    pokemonList: List<PokemonItem>,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, pokemonList) {
        if (searchQuery.isBlank()) pokemonList
        else pokemonList.filter {
            it.name.contains(searchQuery.trim().lowercase()) ||
                    it.id.toString() == searchQuery.trim() ||
                    it.formattedId.contains(searchQuery.trim())
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Pilih Pokémon",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari Pokémon...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(item.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(item.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.displayName,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = item.formattedId,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.displayName,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                TypeBadge(type = item.primaryType)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}
