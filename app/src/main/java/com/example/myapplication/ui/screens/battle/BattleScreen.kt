package com.example.myapplication.ui.screens.battle

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.model.PokemonDetail
import com.example.myapplication.data.model.PokemonItem
import com.example.myapplication.ui.components.TypeBadge
import com.example.myapplication.ui.theme.getPokemonGradient
import com.example.myapplication.ui.theme.getPokemonTypeColor

private val PokemonRed = Color(0xFFE53935)

/**
 * Layar Simulasi Battle & Perbandingan Atribut Head-to-Head Pokémon
 * Desain proporsional, rapi, solid (tidak floating), dengan kartu duel lapang,
 * medali piala Material murni (tanpa emotikon teks), dan komparasi statistik interaktif.
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
            // TopAppBar solid resmi (menyatu utuh dengan status bar, tanpa lengkungan atau floating shadow)
            TopAppBar(
                title = {
                    Text(
                        text = "Battle Arena",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PokemonRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section 1: Matchup Duel Arena (Langsung berdampingan, lapang, tanpa wrapper ganda)
            MatchupDuelSection(
                pokemon1 = uiState.pokemon1,
                pokemon2 = uiState.pokemon2,
                isLoading1 = uiState.isLoading1,
                isLoading2 = uiState.isLoading2,
                onSelectPokemon1 = { selectingForSide = 1 },
                onSelectPokemon2 = { selectingForSide = 2 },
                onSwap = { viewModel.swapPokemon() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Banner Hasil Simulasi & Prediksi Pemenang
            if (uiState.pokemon1 != null && uiState.pokemon2 != null) {
                BattleOutcomeCard(
                    winnerName = uiState.winnerName,
                    isDraw = uiState.isDraw,
                    analysis = uiState.analysisText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Perbandingan Statistik Detail
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
 * Section Arena Duel Head-to-Head Berdampingan yang Proporsional dan Rapi
 */
@Composable
private fun MatchupDuelSection(
    pokemon1: PokemonDetail?,
    pokemon2: PokemonDetail?,
    isLoading1: Boolean,
    isLoading2: Boolean,
    onSelectPokemon1: () -> Unit,
    onSelectPokemon2: () -> Unit,
    onSwap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Fighter Kiri (Pokémon 1)
        Box(modifier = Modifier.weight(1f)) {
            FighterCard(
                pokemon = pokemon1,
                isLoading = isLoading1,
                onSelect = onSelectPokemon1
            )
        }

        // Area Tengah: Badge VS & Tombol Tukar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF263238),
                shadowElevation = 3.dp,
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "VS",
                        color = Color(0xFFFFD54F),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                onClick = onSwap,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Tukar Posisi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Fighter Kanan (Pokémon 2)
        Box(modifier = Modifier.weight(1f)) {
            FighterCard(
                pokemon = pokemon2,
                isLoading = isLoading2,
                onSelect = onSelectPokemon2
            )
        }
    }
}

/**
 * Kartu Individual Tiap Petarung Pokémon
 */
@Composable
private fun FighterCard(
    pokemon: PokemonDetail?,
    isLoading: Boolean,
    onSelect: () -> Unit
) {
    val typeColor = if (pokemon != null) getPokemonTypeColor(pokemon.primaryType) else MaterialTheme.colorScheme.primary
    val gradientColors = if (pokemon != null) {
        listOf(typeColor.copy(alpha = 0.2f), typeColor.copy(alpha = 0.05f))
    } else {
        listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, typeColor.copy(alpha = 0.45f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(gradientColors))
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = typeColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else if (pokemon != null) {
                    // ID Badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = typeColor.copy(alpha = 0.22f)
                    ) {
                        Text(
                            text = pokemon.formattedId,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lingkaran Aura Putih Bersih + Gambar Pokémon
                    Box(
                        modifier = Modifier.size(86.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(78.dp)
                        ) {}

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pokemon.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = pokemon.displayName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(78.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nama Pokémon
                    Text(
                        text = pokemon.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Type Badge
                    TypeBadge(type = pokemon.primaryType)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tombol Ganti Rapi
                    Surface(
                        onClick = onSelect,
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CatchingPokemon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ganti",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Kartu Hasil Analisis Pertarungan dengan Medali Ikon Piala Resmi Material Murni (Tanpa Emoticon)
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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, if (isDraw) Color(0xFFB0BEC5) else Color(0xFFFFB300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDraw) {
                        Brush.linearGradient(
                            listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC))
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(Color(0xFFFFF9C4), Color(0xFFFFECB3), Color(0xFFFFE082))
                        )
                    }
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Medali Ikon Piala Murni (Material Icon EmojiEvents, bukan emot teks)
                Surface(
                    shape = CircleShape,
                    color = if (isDraw) Color(0xFF78909C) else Color(0xFFFFB300),
                    shadowElevation = 5.dp,
                    border = BorderStroke(2.dp, Color.White),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isDraw) Icons.Default.Shield else Icons.Default.EmojiEvents,
                            contentDescription = "Piala Kemenangan",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isDraw) "HASIL AKHIR" else "PREDIKSI PEMENANG",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = if (isDraw) Color(0xFF455A64) else Color(0xFFB78103)
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isDraw) "Pertarungan Imbang!" else winnerName.orEmpty(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = if (isDraw) Color(0xFF263238) else Color(0xFF3E2723)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Kotak Analisis Penjelasan
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Analisis",
                            tint = if (isDraw) Color(0xFF607D8B) else Color(0xFFFF8F00),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = analysis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF37474F),
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tabel Perbandingan Statistik Detail Head-to-Head Berbasis Grafik Batang Dua Sisi
 */
@Composable
private fun StatComparisonSection(
    p1: PokemonDetail,
    p2: PokemonDetail
) {
    val p1Color = getPokemonTypeColor(p1.primaryType)
    val p2Color = getPokemonTypeColor(p2.primaryType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Judul Komparasi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Komparasi Statistik Detail",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Baris Nama Fighter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sisi Kiri
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = p1Color,
                        modifier = Modifier.size(10.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = p1.displayName,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Tengah: Badge Label
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Sisi Kanan
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = p2.displayName,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = p2Color,
                        modifier = Modifier.size(10.dp)
                    ) {}
                }
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
                StatCompareBarRow(
                    label = label,
                    val1 = val1,
                    val2 = val2,
                    p1Color = p1Color,
                    p2Color = p2Color
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total Base Stats Bar
            val total1 = p1.stats.sumOf { it.value }
            val total2 = p2.stats.sumOf { it.value }
            TotalStatsSummaryCard(
                total1 = total1,
                total2 = total2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dimensi Fisik (Berat & Tinggi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dimensi Berat
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Berat Tubuh",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${p1.weightInKg} kg  vs  ${p2.weightInKg} kg",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Dimensi Tinggi
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Height,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tinggi Tubuh",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${p1.heightInMeters} m  vs  ${p2.heightInMeters} m",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Komparasi Baris Stat Individual dengan Visual Bar
 */
@Composable
private fun StatCompareBarRow(
    label: String,
    val1: Int,
    val2: Int,
    p1Color: Color,
    p2Color: Color
) {
    val winColor = Color(0xFF2E7D32)
    val maxStatValue = 200f
    val progress1 by animateFloatAsState(
        targetValue = (val1 / maxStatValue).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progress1"
    )
    val progress2 by animateFloatAsState(
        targetValue = (val2 / maxStatValue).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progress2"
    )

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Angka Kiri
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (val1 > val2) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Lebih Unggul",
                        tint = winColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "$val1",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (val1 > val2) FontWeight.Black else FontWeight.Normal,
                        color = if (val1 > val2) winColor else MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Label Atribut
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.width(76.dp),
                textAlign = TextAlign.Center
            )

            // Angka Kanan
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$val2",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (val2 > val1) FontWeight.Black else FontWeight.Normal,
                        color = if (val2 > val1) winColor else MaterialTheme.colorScheme.onSurface
                    )
                )
                if (val2 > val1) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Lebih Unggul",
                        tint = winColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Dual Visual Progress Bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bar Kiri
            LinearProgressIndicator(
                progress = { progress1 },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (val1 > val2) winColor else p1Color.copy(alpha = 0.8f),
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Bar Kanan
            LinearProgressIndicator(
                progress = { progress2 },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (val2 > val1) winColor else p2Color.copy(alpha = 0.8f),
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

/**
 * Ringkasan Total Statistik Dasar
 */
@Composable
private fun TotalStatsSummaryCard(
    total1: Int,
    total2: Int
) {
    val winColor = Color(0xFF2E7D32)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total Kiri
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "$total1",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (total1 > total2) winColor else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Total Base Stat",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                // Badge Keunggulan Poin
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    val diff = kotlin.math.abs(total1 - total2)
                    Text(
                        text = if (total1 == total2) "Stat Seimbang" else "Selisih $diff Pts",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Total Kanan
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$total2",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (total2 > total1) winColor else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Total Base Stat",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }
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
                .height(560.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CatchingPokemon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pilih Pokémon Petarung",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama Pokémon...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        val itemGradient = getPokemonGradient(item.primaryType)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(item.id) },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = itemGradient.first().copy(alpha = 0.25f),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(item.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = item.displayName,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = item.formattedId,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.outline,
                                        fontWeight = FontWeight.Bold
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
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
