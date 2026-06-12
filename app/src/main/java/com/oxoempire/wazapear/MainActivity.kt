package com.oxoempire.wazapear

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.oxoempire.wazapear.ui.theme.WazapearTheme
import java.net.URLEncoder
import android.graphics.ImageDecoder
import android.provider.MediaStore
import android.os.Build
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.foundation.Image

class MainActivity : ComponentActivity() {

    private val sharedText = mutableStateOf("")
    private val sharedFileUri = mutableStateOf<Uri?>(null)
    private val sharedFileType = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContent {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("wazapear_prefs", Context.MODE_PRIVATE) }

            // State variables persisted in SharedPreferences
            var currentLanguage by remember { mutableStateOf(prefs.getString("lang", "") ?: "") }
            var defaultCountryDialCode by remember { mutableStateOf(prefs.getString("default_country", "56") ?: "56") }
            var selectedTheme by remember { mutableStateOf(prefs.getString("theme", "system") ?: "system") }

            // Active theme calculation
            val darkTheme = when (selectedTheme) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            WazapearTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentLanguage.isEmpty()) {
                        // First run: Show language selection screen
                        LanguageSelectionScreen(
                            onLanguageSelected = { lang ->
                                prefs.edit().putString("lang", lang).apply()
                                currentLanguage = lang
                            }
                        )
                    } else {
                        // Main application UI
                        MainAppScreen(
                            language = currentLanguage,
                            defaultCountryDialCode = defaultCountryDialCode,
                            initialSharedText = sharedText.value,
                            initialSharedFileUri = sharedFileUri.value,
                            initialSharedFileType = sharedFileType.value,
                            selectedTheme = selectedTheme,
                            onLanguageChanged = { lang ->
                                prefs.edit().putString("lang", lang).apply()
                                currentLanguage = lang
                            },
                            onDefaultCountryChanged = { code ->
                                prefs.edit().putString("default_country", code).apply()
                                defaultCountryDialCode = code
                            },
                            onThemeChanged = { theme ->
                                prefs.edit().putString("theme", theme).apply()
                                selectedTheme = theme
                            },
                            onClearSharedText = {
                                sharedText.value = ""
                            },
                            onClearSharedFile = {
                                sharedFileUri.value = null
                                sharedFileType.value = null
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            val type = intent.type ?: ""
            if (type.startsWith("text/") && !intent.hasExtra(Intent.EXTRA_STREAM)) {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                sharedText.value = text
                sharedFileUri.value = null
                sharedFileType.value = null
            } else {
                @Suppress("DEPRECATION")
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }
                sharedFileUri.value = uri
                sharedFileType.value = type
                val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                sharedText.value = text
            }
        }
    }
}

// Custom runtime translation dictionary
private val translations = mapOf(
    "en" to mapOf(
        "title" to "Wazapear",
        "phone_placeholder" to "Phone number",
        "wazapear_btn" to "Wazapear",
        "lang_select" to "Language / Idioma",
        "theme_select" to "Theme / Tema",
        "theme_light" to "Light",
        "theme_dark" to "Dark",
        "theme_system" to "System",
        "default_country" to "Default Country",
        "about_btn" to "About",
        "about_title" to "About Wazapear",
        "author" to "Author: Manu Cabello",
        "version" to "Version: 1.0.0",
        "close" to "Close",
        "shared_msg_label" to "Attached message",
        "shared_msg_hint" to "Enter message to send via WhatsApp...",
        "empty_phone_err" to "Please enter a phone number",
        "phone_length_err" to "Phone number must have %d digits",
        "select_country" to "Select Country",
        "search_country" to "Search country...",
        "set_as_default_country" to "Set as default country",
        "select_lang_title" to "Choose Your Language",
        "settings_title" to "Settings",
        "shared_info" to "Content received from another app",
        "shared_image_info" to "Image received from another app",
        "shared_video_info" to "Video received from another app",
        "shared_file_info" to "File received from another app",
        "clear_btn" to "Clear"
    ),
    "es" to mapOf(
        "title" to "Wazapear",
        "phone_placeholder" to "Número de teléfono",
        "wazapear_btn" to "Wazapear",
        "lang_select" to "Idioma / Language",
        "theme_select" to "Tema / Theme",
        "theme_light" to "Claro",
        "theme_dark" to "Oscuro",
        "theme_system" to "Sistema",
        "default_country" to "País por defecto",
        "about_btn" to "Acerca de",
        "about_title" to "Acerca de Wazapear",
        "author" to "Autor: Manu Cabello",
        "version" to "Versión: 1.0.0",
        "close" to "Cerrar",
        "shared_msg_label" to "Mensaje adjunto",
        "shared_msg_hint" to "Escribe el mensaje para enviar por WhatsApp...",
        "empty_phone_err" to "Por favor ingresa un número de teléfono",
        "phone_length_err" to "El número debe tener %d dígitos",
        "select_country" to "Seleccionar País",
        "search_country" to "Buscar país...",
        "set_as_default_country" to "Establecer como país por defecto",
        "select_lang_title" to "Selecciona tu Idioma",
        "settings_title" to "Configuración",
        "shared_info" to "Contenido recibido desde otra aplicación",
        "shared_image_info" to "Imagen recibida desde otra aplicación",
        "shared_video_info" to "Video recibido desde otra aplicación",
        "shared_file_info" to "Archivo recibido desde otra aplicación",
        "clear_btn" to "Borrar"
    )
)

fun getTxt(lang: String, key: String): String {
    return translations[lang]?.get(key) ?: translations["en"]?.get(key) ?: key
}

@Composable
fun rememberUriImage(uri: Uri?, context: Context): ImageBitmap? {
    if (uri == null) return null
    return remember(uri) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun LanguageSelectionScreen(onLanguageSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wazapear",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Select your language / Selecciona tu idioma",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { onLanguageSelected("es") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("🇪🇸 Español", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
            onClick = { onLanguageSelected("en") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("🇺🇸 English", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    language: String,
    defaultCountryDialCode: String,
    initialSharedText: String,
    initialSharedFileUri: Uri?,
    initialSharedFileType: String?,
    selectedTheme: String,
    onLanguageChanged: (String) -> Unit,
    onDefaultCountryChanged: (String) -> Unit,
    onThemeChanged: (String) -> Unit,
    onClearSharedText: () -> Unit,
    onClearSharedFile: () -> Unit
) {
    val context = LocalContext.current
    var selectedCountry by remember { mutableStateOf(Countries.getByDialCode(defaultCountryDialCode)) }

    // When the default country preference changes, we sync the selected country
    LaunchedEffect(defaultCountryDialCode) {
        selectedCountry = Countries.getByDialCode(defaultCountryDialCode)
    }

    var phoneNumber by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf(initialSharedText) }

    // Sync state if initialSharedText changes (i.e. app is opened with a new share intent)
    LaunchedEffect(initialSharedText) {
        if (initialSharedText.isNotEmpty()) {
            messageText = initialSharedText
        }
    }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCountrySelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = getTxt(language, "title"),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = getTxt(language, "about_btn"),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = getTxt(language, "settings_title"),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shared Message Banner (if any shared content was received)
                if (initialSharedText.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = getTxt(language, "shared_info"),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = initialSharedText,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            TextButton(onClick = {
                                onClearSharedText()
                                messageText = ""
                            }) {
                                Text(getTxt(language, "close"), color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Shared File Banner (if any shared file was received)
                if (initialSharedFileUri != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                val fileType = initialSharedFileType ?: "*/*"
                                if (fileType.startsWith("image/")) {
                                    val bitmap = rememberUriImage(initialSharedFileUri, context)
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = "Preview",
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .padding(end = 8.dp)
                                        )
                                    } else {
                                        Text("🖼️", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                    }
                                } else if (fileType.startsWith("video/")) {
                                    Text("🎥", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                } else if (fileType.startsWith("audio/")) {
                                    Text("🎵", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                } else {
                                    Text("📄", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                }

                                val bannerTextKey = when {
                                    fileType.startsWith("image/") -> "shared_image_info"
                                    fileType.startsWith("video/") -> "shared_video_info"
                                    else -> "shared_file_info"
                                }
                                Text(
                                    text = getTxt(language, bannerTextKey),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            TextButton(onClick = {
                                onClearSharedFile()
                            }) {
                                Text(getTxt(language, "close"), color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Core Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Country Selector Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .clickable { showCountrySelector = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedCountry.flag,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(
                                    text = if (language == "es") selectedCountry.nameEs else selectedCountry.nameEn,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = selectedCountry.code,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Phone Number Input
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { input ->
                                // Only allow digits
                                val digits = input.filter { it.isDigit() }
                                if (digits.length <= selectedCountry.maxDigits) {
                                    phoneNumber = digits
                                }
                            },
                            label = { Text(getTxt(language, "phone_placeholder")) },
                            leadingIcon = {
                                Text(
                                    text = selectedCountry.code,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                )
                            },
                            suffix = {
                                Text(
                                    text = "${phoneNumber.length}/${selectedCountry.maxDigits}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (phoneNumber.length == selectedCountry.maxDigits) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Custom Message Box
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            label = { Text(getTxt(language, "shared_msg_label")) },
                            placeholder = { Text(getTxt(language, "shared_msg_hint")) },
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // WA ZAPEAR BUTTON
                Button(
                    onClick = {
                        if (phoneNumber.isEmpty()) {
                            Toast.makeText(context, getTxt(language, "empty_phone_err"), Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (phoneNumber.length != selectedCountry.maxDigits) {
                            val errMsg = String.format(getTxt(language, "phone_length_err"), selectedCountry.maxDigits)
                            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Full number structure: dial code + phone number
                        val fullNumber = "${selectedCountry.dialCode}$phoneNumber"

                        if (initialSharedFileUri != null) {
                            val targetPackages = listOf("com.whatsapp", "com.whatsapp.w4b")
                            var launched = false
                            val fileType = initialSharedFileType ?: "*/*"
                            for (pkg in targetPackages) {
                                try {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = fileType
                                        putExtra(Intent.EXTRA_STREAM, initialSharedFileUri)
                                        if (messageText.trim().isNotEmpty()) {
                                            putExtra(Intent.EXTRA_TEXT, messageText.trim())
                                        }
                                        setPackage(pkg)
                                        putExtra("jid", "$fullNumber@s.whatsapp.net")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                    launched = true
                                    break
                                } catch (e: Exception) {
                                    // Try next package
                                }
                            }
                            if (!launched) {
                                try {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = fileType
                                        putExtra(Intent.EXTRA_STREAM, initialSharedFileUri)
                                        if (messageText.trim().isNotEmpty()) {
                                            putExtra(Intent.EXTRA_TEXT, messageText.trim())
                                        }
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share File"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp not installed / Error sending file", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            // Build WhatsApp Link
                            val url = StringBuilder("https://wa.me/$fullNumber")
                            if (messageText.trim().isNotEmpty()) {
                                val encodedMessage = URLEncoder.encode(messageText.trim(), "UTF-8")
                                url.append("?text=$encodedMessage")
                            }

                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp not installed / Error opening browser", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366), // Standard WhatsApp Green
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = getTxt(language, "wazapear_btn").uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = {
                        phoneNumber = ""
                        messageText = ""
                        onClearSharedFile()
                        selectedCountry = Countries.getByDialCode(defaultCountryDialCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = getTxt(language, "clear_btn").uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }

    // ABOUT DIALOG (Info Modal)
    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = getTxt(language, "about_title"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Description of both items (Frog + WhatsApp wrapper)
                    Text(
                        text = "🐸 💬",
                        fontSize = 48.sp
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = getTxt(language, "author"),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = getTxt(language, "version"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    // Clickable GitHub Link
                    Text(
                        text = "GitHub: https://github.com/oxoempire",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .clickable {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/oxoempire"))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open browser", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(8.dp)
                    )

                    Button(
                        onClick = { showAboutDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(getTxt(language, "close"))
                    }
                }
            }
        }
    }

    // SETTINGS DIALOG
    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = getTxt(language, "settings_title"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Language settings
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = getTxt(language, "lang_select"),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onLanguageChanged("es") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (language == "es") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (language == "es") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Español")
                            }
                            Button(
                                onClick = { onLanguageChanged("en") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (language == "en") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (language == "en") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("English")
                            }
                        }
                    }

                    // Default Country settings
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = getTxt(language, "default_country"),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    showCountrySelector = true
                                }
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row {
                                    Text(selectedCountry.flag, modifier = Modifier.padding(end = 8.dp))
                                    Text(
                                        text = if (language == "es") selectedCountry.nameEs else selectedCountry.nameEn,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(selectedCountry.code, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Theme selector settings
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = getTxt(language, "theme_select"),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("light", "dark", "system").forEach { themeOption ->
                                Button(
                                    onClick = { onThemeChanged(themeOption) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedTheme == themeOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedTheme == themeOption) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = when (themeOption) {
                                            "light" -> getTxt(language, "theme_light")
                                            "dark" -> getTxt(language, "theme_dark")
                                            else -> getTxt(language, "theme_system")
                                        },
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showSettingsDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(getTxt(language, "close"))
                    }
                }
            }
        }
    }

    // COUNTRY SELECTOR BOTTOM SHEET DIALOG
    if (showCountrySelector) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredCountries = remember(searchQuery) {
            if (searchQuery.isEmpty()) {
                Countries.list
            } else {
                Countries.list.filter {
                    it.nameEs.contains(searchQuery, ignoreCase = true) ||
                            it.nameEn.contains(searchQuery, ignoreCase = true) ||
                            it.code.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        Dialog(onDismissRequest = { showCountrySelector = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = getTxt(language, "select_country"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(getTxt(language, "search_country")) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Countries list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCountries) { country ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedCountry = country
                                        // Save as default in settings automatically if settings dialog is open, or update defaultCountry
                                        if (showSettingsDialog) {
                                            onDefaultCountryChanged(country.dialCode)
                                        }
                                        showCountrySelector = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = country.flag,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Text(
                                        text = if (language == "es") country.nameEs else country.nameEn,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Text(
                                    text = country.code,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = { showCountrySelector = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(getTxt(language, "close"))
                    }
                }
            }
        }
    }
}
