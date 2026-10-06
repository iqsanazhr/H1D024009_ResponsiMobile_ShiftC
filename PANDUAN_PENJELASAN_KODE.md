# 🎙️ Panduan Rekaman Video Penjelasan Kode (Responsi Shift C)

Dokumen ini disusun untuk membantu Anda merekam video penjelasan kode sesuai kriteria penilaian:
> *"Video berfokus pada penjelasan kode dan implementasi, bukan sekadar demo aplikasi."*

---

## ⏱️ Rundown & Naskah Presentasi (Estimasi 4 - 6 Menit)

### Bagian 1: Pembukaan & Identitas (±45 Detik)
1. **Buka kamera / screen recording** menampilkan Android Studio dengan proyek **PokéDex Explorer**.
2. **Naskah:**
   > *"Halo, selamat pagi/siang/sore. Perkenalkan nama saya **Iqsan Azhar Nuryadi**, dengan NIM **H1D024009**, dari **Shift C** Praktikum Pemrograman Mobile.*  
   > *Pada kesempatan kali ini, saya akan mempresentasikan penjelasan kode dan arsitektur dari proyek responsi saya, yaitu **Aplikasi Katalog dan Eksplorasi Pokémon** yang terintegrasi secara dinamis dengan REST API PokéAPI menggunakan **Jetpack Compose Material 3** dan pola arsitektur **MVVM**."*

---

### Bagian 2: Penjelasan Arsitektur MVVM & Data Layer (±1.5 Menit)
1. **Buka package `data/` di Project Explorer**:
   - Tunjukkan `data/remote/PokeApiService.kt` dan `RetrofitClient.kt`:
     > *"Pertama, pada sisi networking, saya menggunakan **Retrofit 2** dan **Gson Converter**. Di dalam `PokeApiService.kt`, terdapat dua endpoint utama: `getPokemonList` untuk mengambil daftar Pokémon dengan query limit dan offset, serta `getPokemonDetail` untuk mengambil informasi spesifik Pokémon berdasarkan ID atau nama."*
   - Tunjukkan `data/model/`:
     > *"Response dari API ditangkap oleh DTO `PokemonListResponse` dan `PokemonDetailResponse`. Untuk menjaga kebersihan data di layer UI, saya memisahkan DTO tersebut ke dalam UI Model di `PokemonUiModel.kt`, yang mencakup data class `PokemonItem`, `PokemonDetail`, dan `PokemonStatItem` lengkap dengan extension helper seperti format nomor ID `#001` dan kapitalisasi nama."*
   - Tunjukkan `data/repository/PokemonRepository.kt`:
     > *"Sesuai instruksi soal, composable tidak boleh memanggil API secara langsung. Oleh karena itu, saya mengimplementasikan `PokemonRepository`. Di sini pemanggilan jaringan dibungkus dengan Kotlin Coroutines pada thread I/O (`Dispatchers.IO`) dan mengembalikan objek `Result` yang aman."*

---

### Bagian 3: State Management & ViewModel (±1.5 Menit)
1. **Buka `ui/screens/home/HomeViewModel.kt`**:
   - Tunjukkan data class `HomeUiState` dan `StateFlow`:
     > *"Pada ViewModel layer, saya menerapkan arsitektur **state-driven UI**. Di `HomeViewModel`, status UI didefinisikan dalam `HomeUiState` yang mencakup `isLoading`, `pokemonList`, `filteredList`, `searchQuery`, dan `errorMessage`."*
   - Tunjukkan fungsi `onSearchQueryChange`:
     > *"Untuk fitur pencarian (search functionality), saya membuat fungsi `onSearchQueryChange` yang memfilter koleksi Pokémon secara real-time berdasarkan kecocokan nama ataupun nomor ID, tanpa perlu melakukan hit ulang ke server PokéAPI."*
2. **Buka `ui/screens/detail/DetailViewModel.kt`**:
   > *"Sedangkan pada `DetailViewModel`, state mengelola proses pengambilan detail Pokémon saat user berpindah halaman, lengkap dengan penanganan loading dan error."*

---

### Bagian 4: Jetpack Compose UI, Reusable Components, & Navigasi (±1.5 Menit)
1. **Buka `ui/navigation/NavGraph.kt`**:
   > *"Navigasi antar halaman dibangun menggunakan **Navigation Compose** dengan rute `home_screen` dan `detail_screen/{pokemonId}`."*
2. **Buka `ui/screens/home/HomeScreen.kt` & `ui/components/`**:
   > *"Di layar utama (`HomeScreen`), saya menggunakan komponen lazy layout berupa **`LazyVerticalGrid` 2 kolom** untuk menampilkan daftar kartu Pokémon. Gambar dimuat secara asinkron menggunakan library **Coil** (`SubcomposeAsyncImage`) langsung dari official artwork PokéAPI yang tajam.*  
   > *Layar ini juga menangani 4 kemungkinan state secara komprehensif: Loading Spinner, Error View dengan tombol Coba Lagi (Retry), Empty View jika pencarian tidak ditemukan, dan Grid data saat sukses."*
3. **Buka `ui/screens/detail/DetailScreen.kt`**:
   > *"Pada layar detail (`DetailScreen`), latar belakang kartu secara adaptif berubah warna sesuai tipe elemen Pokémon (misalnya oranye untuk Fire, biru untuk Water) menggunakan fungsi `getPokemonTypeColor` di `Color.kt`.*  
   > *Informasi yang ditampilkan mencakup ID resmi, nama, tipe badges, tinggi, berat, kemampuan (abilities), serta **Base Stats** (HP, Attack, Defense, Sp. Atk, Sp. Def, Speed) yang divisualisasikan dengan animasi progress bar (`StatBar`) dan palet warna representatif."*

---

### Bagian 5: Penutup (±30 Detik)
1. **Naskah:**
   > *"Seluruh persyaratan teknis—mulai dari pemanfaatan bahasa Kotlin (data class, null safety, lambda), Material Design 3, state-driven UI, integrasi PokéAPI, arsitektur MVVM, hingga penanganan error—telah terpenuhi dengan baik.*  
   > *Sekian penjelasan kode dari saya, terima kasih atas perhatian Bapak/Ibu dosen dan asisten praktikum."*

---

> [!TIP]
> Saat merekam video, Anda bisa menggunakan **OBS Studio**, **Xbox Game Bar (`Win + G`)**, atau **ShareX** dengan mikrofon yang jernih. Tunjukkan potongan kode yang sedang dijelaskan di Android Studio agar reviewer dapat melihat struktur kodenya dengan jelas.
