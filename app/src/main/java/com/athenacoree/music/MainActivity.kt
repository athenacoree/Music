package com.athenacoree.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class MusicTab(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Default.Home),
    Library("Biblioteca", Icons.Default.LibraryMusic),
    Videos("Vídeos", Icons.Default.VideoLibrary),
    Playlists("Listas", Icons.Default.PlaylistPlay),
    Profile("Perfil", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MusicApp() }
    }
}

@Composable
private fun MusicApp() {
    var darkMode by rememberSaveable { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableStateOf(MusicTab.Home.name) }
    val tab = MusicTab.valueOf(selectedTab)

    val colors = if (darkMode) {
        MusicColors(
            background = Color(0xFF101116),
            surface = Color(0xFF1B1D25),
            surfaceAlt = Color(0xFF252833),
            text = Color(0xFFF5F5F7),
            muted = Color(0xFFA6A9B5),
            accent = Color(0xFFB7F36B),
            accentText = Color(0xFF17200D)
        )
    } else {
        MusicColors(
            background = Color(0xFFF5F6F8),
            surface = Color.White,
            surfaceAlt = Color(0xFFE9EDF2),
            text = Color(0xFF171A20),
            muted = Color(0xFF737B88),
            accent = Color(0xFF5B8E31),
            accentText = Color.White
        )
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colors.background
        ) {
            Scaffold(
                containerColor = colors.background,
                topBar = {
                    MusicTopBar(
                        colors = colors,
                        darkMode = darkMode,
                        onThemeChange = { darkMode = !darkMode }
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = colors.surface) {
                        MusicTab.entries.forEach { item ->
                            NavigationBarItem(
                                selected = tab == item,
                                onClick = { selectedTab = item.name },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label, maxLines = 1) }
                            )
                        }
                    }
                }
            ) { padding ->
                when (tab) {
                    MusicTab.Home -> HomeScreen(colors, padding)
                    MusicTab.Library -> LibraryScreen(colors, padding)
                    MusicTab.Videos -> SimpleSectionScreen(
                        colors, padding, "Vídeos", "Tus vídeos y colecciones en un solo lugar.",
                        Icons.Default.VideoLibrary
                    )
                    MusicTab.Playlists -> SimpleSectionScreen(
                        colors, padding, "Mis listas", "Crea listas personales o compártelas cuando quieras.",
                        Icons.Default.PlaylistPlay
                    )
                    MusicTab.Profile -> ProfileScreen(colors, padding, darkMode, onThemeChange = { darkMode = !darkMode })
                }
            }
        }
    }
}

private data class MusicColors(
    val background: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val text: Color,
    val muted: Color,
    val accent: Color,
    val accentText: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MusicTopBar(colors: MusicColors, darkMode: Boolean, onThemeChange: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(colors.accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = colors.accentText)
                }
                Spacer(Modifier.width(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy((-3).dp)) {
                    Text("music", fontWeight = FontWeight.Bold, fontSize = 21.sp, color = colors.text)
                    Text("TU ESPACIO SONORO", fontSize = 8.sp, letterSpacing = 1.4.sp, color = colors.muted)
                }
            }
        },
        actions = {
            IconButton(onClick = onThemeChange) {
                Icon(
                    if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Cambiar tema",
                    tint = colors.text
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = colors.text)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
    )
}

@Composable
private fun HomeScreen(colors: MusicColors, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Tu música, a tu manera.", color = colors.text, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Text("Todo lo que te gusta, organizado en un solo lugar.", color = colors.muted, fontSize = 14.sp)
            }
        }
        item {
            FeaturedCard(colors)
        }
        item {
            SectionHeader("Escuchado recientemente", "Ver todo", colors)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniCollection("Tus favoritos", "Tu colección personal", "♫", colors, Modifier.weight(1f))
                MiniCollection("Descubrir", "Encuentra algo nuevo", "✦", colors, Modifier.weight(1f))
            }
        }
        item {
            SectionHeader("Explora tu biblioteca", "Abrir", colors)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                LibraryShortcut("Artistas", "Organiza por intérprete", Icons.Default.Person, colors)
                LibraryShortcut("Álbumes", "Discos y colecciones", Icons.Default.Album, colors)
                LibraryShortcut("Canciones", "Toda tu música", Icons.Default.MusicNote, colors)
            }
        }
        item {
            Text(
                "Modo invitado · Tus datos locales permanecen en este dispositivo",
                color = colors.muted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun FeaturedCard(colors: MusicColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF314A24), Color(0xFF6B923B), Color(0xFFB5D47B))
                )
            )
            .padding(22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Text("TU BIBLIOTECA EMPIEZA AQUÍ", color = Color.White.copy(alpha = .8f), fontSize = 10.sp, letterSpacing = 1.5.sp)
            Text("Dale espacio
a lo que te mueve.", color = Color.White, fontSize = 28.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { }
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Explorar música", color = Color(0xFF25351A), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Spacer(Modifier.width(7.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF25351A), modifier = Modifier.size(15.dp))
            }
        }
        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color.White.copy(alpha = .13f),
            modifier = Modifier.align(Alignment.CenterEnd).size(116.dp)
        )
    }
}

@Composable
private fun SectionHeader(title: String, action: String, colors: MusicColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = colors.text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(action, color = colors.accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MiniCollection(title: String, subtitle: String, symbol: String, colors: MusicColors, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(colors.surfaceAlt),
            contentAlignment = Alignment.Center
        ) {
            Text(symbol, color = colors.accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Text(title, color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(subtitle, color = colors.muted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun LibraryShortcut(title: String, subtitle: String, icon: ImageVector, colors: MusicColors) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.surface).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(colors.surfaceAlt),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = colors.accent) }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = colors.muted, fontSize = 11.sp)
        }
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = colors.muted, modifier = Modifier.size(17.dp))
    }
}

@Composable
private fun LibraryScreen(colors: MusicColors, padding: PaddingValues) {
    var filter by rememberSaveable { mutableStateOf("Todo") }
    val filters = listOf("Todo", "Artistas", "Álbumes", "Canciones")
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        item {
            Text("Biblioteca", color = colors.text, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text("Tu colección, en una sola vista.", color = colors.muted, fontSize = 13.sp)
        }
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { option ->
                    val selected = filter == option
                    Surface(
                        color = if (selected) colors.accent else colors.surface,
                        shape = CircleShape,
                        modifier = Modifier.clickable { filter = option }
                    ) {
                        Text(
                            option,
                            color = if (selected) colors.accentText else colors.muted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(colors.surface).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null, tint = colors.accent, modifier = Modifier.size(30.dp))
                Text("Tu colección está lista para crecer", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(
                    "Añade artistas, álbumes y canciones. Music guardará la información localmente para que puedas consultarla incluso sin conexión.",
                    color = colors.muted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
        item { Text("Accesos rápidos", color = colors.text, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
        items(
            when (filter) {
                "Artistas" -> listOf("Artistas" to Icons.Default.Person)
                "Álbumes" -> listOf("Álbumes" to Icons.Default.Album)
                "Canciones" -> listOf("Canciones" to Icons.Default.MusicNote)
                else -> listOf("Artistas" to Icons.Default.Person, "Álbumes" to Icons.Default.Album, "Canciones" to Icons.Default.MusicNote)
            }
        ) { entry ->
            LibraryShortcut(entry.first, "Todavía no hay elementos añadidos", entry.second, colors)
        }
    }
}

@Composable
private fun SimpleSectionScreen(
    colors: MusicColors,
    padding: PaddingValues,
    title: String,
    description: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(title, color = colors.text, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(colors.surface).padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(36.dp))
            Text("Este espacio es tuyo", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(description, color = colors.muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun ProfileScreen(
    colors: MusicColors,
    padding: PaddingValues,
    darkMode: Boolean,
    onThemeChange: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Perfil y ajustes", color = colors.text, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(colors.surface).padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(colors.surfaceAlt),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Person, contentDescription = null, tint = colors.accent, modifier = Modifier.size(28.dp)) }
            Spacer(Modifier.width(13.dp))
            Column {
                Text("Estás usando Music como invitado", color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("No necesitas una cuenta para empezar", color = colors.muted, fontSize = 11.sp)
            }
        }
        Text("Preferencias", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.surface).clickable { onThemeChange() }.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(if (darkMode) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = colors.accent)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Apariencia", color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(if (darkMode) "Modo oscuro" else "Modo claro", color = colors.muted, fontSize = 11.sp)
            }
            Text("Cambiar", color = colors.accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.surface).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = colors.muted)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Almacenamiento y caché", color = colors.text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("Gestión inteligente próximamente", color = colors.muted, fontSize = 11.sp)
            }
        }
    }
}
