package com.taif.keyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.foundation.BorderStroke

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF3B82F6),
                    secondary = Color(0xFFEC4899),
                    background = Color(0xFF0F172A),
                    surface = Color(0xFF1E293B)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OnboardingScreen()
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen() {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    
    var isEnabled by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(settingsManager.isSoundEnabled) }
    var hapticEnabled by remember { mutableStateOf(settingsManager.isHapticEnabled) }
    var currentTheme by remember { mutableStateOf(settingsManager.selectedTheme) }
    var testText by remember { mutableStateOf("") }
    var crashLog by remember { mutableStateOf(settingsManager.lastCrashLog) }
    var customPrimaryColor by remember { mutableStateOf(settingsManager.customPrimaryColor) }
    var customBgColor by remember { mutableStateOf(settingsManager.customBgColor) }

    // Check keyboard status when app opens or resumes from settings
    fun checkStatus() {
        val packageId = context.packageName
        
        val enabledImeIds = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_INPUT_METHODS) ?: ""
        isEnabled = enabledImeIds.contains(packageId)

        val currentImeId = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) ?: ""
        isSelected = currentImeId.contains(packageId)
        
        crashLog = settingsManager.lastCrashLog
        customPrimaryColor = settingsManager.customPrimaryColor
        customBgColor = settingsManager.customBgColor
    }

    // Monitor lifecycle events to refresh status when user returns from settings
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        checkStatus()
    }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF3B82F6), Color(0xFFEC4899))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        if (crashLog.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                border = BorderStroke(1.5.dp, Color(0xFFF87171))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "خطأ في تشغيل الكيبورد (Last Crash):",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Button(
                            onClick = {
                                settingsManager.lastCrashLog = ""
                                crashLog = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("مسح (Clear)", color = Color.White, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = crashLog,
                        color = Color(0xFFFCA5A5),
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        }

        // Styled "ط" logo container matching the user's icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Draw a beautiful "ط" and gradient circle matching the icon
            Box(modifier = Modifier.fillMaxSize()) {
                // Gradient dot
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                        .offset(x = 18.dp, y = (-12).dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFFD946EF))
                            )
                        )
                )
                // Letter "ط"
                Text(
                    text = "ط",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "طيف | Taif",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            text = "لوحة المفاتيح الذكية والجمالية الأولى",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Step 1: Enable Keyboard
        OnboardingStepCard(
            stepNumber = 1,
            title = "تفعيل لوحة المفاتيح",
            description = "قم بتفعيل كيبورد طيف في إعدادات النظام",
            isDone = isEnabled,
            onClick = {
                context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Step 2: Select Keyboard
        OnboardingStepCard(
            stepNumber = 2,
            title = "اختيار لوحة المفاتيح الافتراضية",
            description = "اجعل طيف هي لوحة المفاتيح النشطة لديك",
            isDone = isSelected,
            onClick = {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Settings header
        Text(
            text = "الإعدادات والمظهر (Settings)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Theme Column
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "اختر المظهر (Select Theme)",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Row 1: Spectrum, Dark Glass, Light Warm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeCard(
                    name = "Spectrum",
                    gradient = gradientBrush,
                    isSelected = currentTheme == SettingsManager.THEME_SPECTRUM,
                    onClick = {
                        settingsManager.selectedTheme = SettingsManager.THEME_SPECTRUM
                        currentTheme = SettingsManager.THEME_SPECTRUM
                    },
                    modifier = Modifier.weight(1f)
                )

                ThemeCard(
                    name = "Dark Glass",
                    color = Color(0xFF1E1E24),
                    isSelected = currentTheme == SettingsManager.THEME_DARK,
                    onClick = {
                        settingsManager.selectedTheme = SettingsManager.THEME_DARK
                        currentTheme = SettingsManager.THEME_DARK
                    },
                    modifier = Modifier.weight(1f)
                )

                ThemeCard(
                    name = "Light Warm",
                    color = Color(0xFFF4F4F0),
                    textColor = Color.Black,
                    isSelected = currentTheme == SettingsManager.THEME_LIGHT,
                    onClick = {
                        settingsManager.selectedTheme = SettingsManager.THEME_LIGHT
                        currentTheme = SettingsManager.THEME_LIGHT
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Glassmorphic, Custom Colors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeCard(
                    name = "Glassmorphic",
                    color = Color(0x22FFFFFF),
                    isSelected = currentTheme == SettingsManager.THEME_GLASSMORPHIC,
                    onClick = {
                        settingsManager.selectedTheme = SettingsManager.THEME_GLASSMORPHIC
                        currentTheme = SettingsManager.THEME_GLASSMORPHIC
                    },
                    modifier = Modifier.weight(1f)
                )

                ThemeCard(
                    name = "Custom Color 🎨",
                    gradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6),
                            Color(0xFF10B981),
                            Color(0xFFEC4899),
                            Color(0xFFF97316),
                            Color(0xFFBD93F9)
                        )
                    ),
                    isSelected = currentTheme == SettingsManager.THEME_CUSTOM,
                    onClick = {
                        settingsManager.selectedTheme = SettingsManager.THEME_CUSTOM
                        currentTheme = SettingsManager.THEME_CUSTOM
                    },
                    modifier = Modifier.weight(2f)
                )
            }

            // Custom Theme Configuration
            if (currentTheme == SettingsManager.THEME_CUSTOM) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "اختر لون الكيبورد الجاهز (Preset Colors):",
                    fontSize = 13.sp,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Row of colored circles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorPresets.forEach { preset ->
                        val isPresetSelected = customPrimaryColor.equals(preset.primaryColor, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(preset.displayColor)
                                .border(
                                    2.dp,
                                    if (isPresetSelected) Color.White else Color.Transparent,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    settingsManager.customPrimaryColor = preset.primaryColor
                                    settingsManager.customBgColor = preset.bgColor
                                    customPrimaryColor = preset.primaryColor
                                    customBgColor = preset.bgColor
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPresetSelected) {
                                Text(
                                    text = "✓",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "أو أدخل كود اللون الخاص بك (Custom Hex Colors):",
                    fontSize = 13.sp,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Input hex codes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var primaryInput by remember { mutableStateOf(customPrimaryColor) }
                    var bgInput by remember { mutableStateOf(customBgColor) }

                    OutlinedTextField(
                        value = primaryInput,
                        onValueChange = { primaryInput = it },
                        label = { Text("لون المفاتيح (Hex)") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = bgInput,
                        onValueChange = { bgInput = it },
                        label = { Text("لون الخلفية (Hex)") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Button(
                        onClick = {
                            var cleanPrimary = primaryInput.trim()
                            var cleanBg = bgInput.trim()

                            if (!cleanPrimary.startsWith("#")) {
                                cleanPrimary = "#$cleanPrimary"
                            }
                            if (!cleanBg.startsWith("#")) {
                                cleanBg = "#$cleanBg"
                            }

                            if (cleanPrimary.length in listOf(4, 7, 9)) {
                                settingsManager.customPrimaryColor = cleanPrimary
                                customPrimaryColor = cleanPrimary
                                primaryInput = cleanPrimary
                            }
                            if (cleanBg.length in listOf(4, 7, 9)) {
                                settingsManager.customBgColor = cleanBg
                                customBgColor = cleanBg
                                bgInput = cleanBg
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("تطبيق", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Vibration & Sound Toggles
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "صوت المفاتيح عند النقر", color = Color.White, fontSize = 15.sp)
                Switch(
                    checked = soundEnabled,
                    onCheckedChange = {
                        soundEnabled = it
                        settingsManager.isSoundEnabled = it
                    }
                )
            }

            Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "اهتزاز المفاتيح عند النقر", color = Color.White, fontSize = 15.sp)
                Switch(
                    checked = hapticEnabled,
                    onCheckedChange = {
                        hapticEnabled = it
                        settingsManager.isHapticEnabled = it
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Test typing field
        OutlinedTextField(
            value = testText,
            onValueChange = { testText = it },
            label = { Text("جرب لوحة المفاتيح واكتب هنا...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun OnboardingStepCard(
    stepNumber: Int,
    title: String,
    description: String,
    isDone: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFF1E3A8A).copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.5.dp,
            if (isDone) Color(0xFF3B82F6) else Color(0xFF334155)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDone) Color(0xFF3B82F6) else Color(0xFF334155)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = description,
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )
            }

            if (isDone) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Done",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun ThemeCard(
    name: String,
    color: Color = Color.Transparent,
    gradient: Brush? = null,
    textColor: Color = Color.White,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (gradient != null) Modifier.background(gradient)
                else Modifier.background(color)
            )
            .border(
                2.dp,
                if (isSelected) Color(0xFF3B82F6) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

data class ColorPreset(
    val name: String,
    val primaryColor: String,
    val bgColor: String,
    val displayColor: Color
)

val colorPresets = listOf(
    ColorPreset("سماوي", "#06B6D4", "#083344", Color(0xFF06B6D4)),
    ColorPreset("أخضر", "#10B981", "#064E3B", Color(0xFF10B981)),
    ColorPreset("وردي", "#EC4899", "#1E1B4B", Color(0xFFEC4899)),
    ColorPreset("برتقالي", "#F97316", "#431407", Color(0xFFF97316)),
    ColorPreset("بنفسجي", "#BD93F9", "#282A36", Color(0xFFBD93F9)),
    ColorPreset("أحمر", "#EF4444", "#450A0A", Color(0xFFEF4444)),
    ColorPreset("أزرق", "#3B82F6", "#0F172A", Color(0xFF3B82F6))
)
