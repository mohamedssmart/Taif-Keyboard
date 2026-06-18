package com.taif.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView

class TaifKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface OnKeyClickListener {
        fun onKeyClick(code: Int, text: String?)
    }

    var keyClickListener: OnKeyClickListener? = null
    private val settings = SettingsManager(context)

    // Current state
    private var isShifted = false
    private var currentMode = Mode.ARABIC // Default to Arabic as requested by logo/concept
    private var isEmojiVisible = false
    private var currentImeOptions: Int = EditorInfo.IME_ACTION_UNSPECIFIED
    private var isSensitiveMode: Boolean = false

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    // Main layout container (LinearLayout) and Floating Preview Overlay
    private val keyboardLayout: LinearLayout
    private val previewOverlay: TextView

    // Continuous backspace repeat runnable
    private val mainHandler = Handler(Looper.getMainLooper())
    private var deleteRunnable: Runnable? = null

    enum class Mode {
        ARABIC, ENGLISH, SYMBOLS, SYMBOLS_SHIFT
    }

    init {
        // Prevent clipping of preview bubbles
        clipChildren = false
        clipToPadding = false

        setPadding(dpToPx(6), dpToPx(8), dpToPx(6), dpToPx(8))

        // 1. Initialize main keyboard vertical layout
        keyboardLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            clipChildren = false
            clipToPadding = false
        }
        addView(keyboardLayout)

        // 2. Initialize single shared preview overlay (View Pooling)
        previewOverlay = TextView(context).apply {
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            visibility = View.INVISIBLE

            // Custom speech-bubble gradient background for popup
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#3B82F6"), Color.parseColor("#EC4899"))
            ).apply {
                cornerRadius = dpToPx(12).toFloat()
            }
            background = gradient
        }
        val previewParams = FrameLayout.LayoutParams(dpToPx(55), dpToPx(65))
        addView(previewOverlay, previewParams)

        applyThemeBackground()
        buildKeyboardLayout()
    }

    private fun getRowHeight(): Int {
        val orientation = context.resources.configuration.orientation
        return if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            dpToPx(34)
        } else {
            dpToPx(46)
        }
    }

    private fun getToolbarHeight(): Int {
        val orientation = context.resources.configuration.orientation
        return if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            dpToPx(32)
        } else {
            dpToPx(42)
        }
    }

    private fun getRowVerticalMargin(): Int {
        val orientation = context.resources.configuration.orientation
        return if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            dpToPx(2)
        } else {
            dpToPx(3)
        }
    }

    private fun applyThemeBackground() {
        val theme = settings.selectedTheme
        background = when (theme) {
            SettingsManager.THEME_LIGHT -> GradientDrawable().apply {
                setColor(Color.parseColor("#F4F4F0"))
            }
            SettingsManager.THEME_DARK -> GradientDrawable().apply {
                setColor(Color.parseColor("#1C1C22"))
            }
            SettingsManager.THEME_SPECTRUM -> GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.parseColor("#1D4ED8"), // Deep Blue
                    Color.parseColor("#701A75"), // Purple
                    Color.parseColor("#EC4899")  // Pink
                )
            )
            SettingsManager.THEME_GLASSMORPHIC -> GradientDrawable().apply {
                setColor(Color.parseColor("#CC101016")) // Dark glass
            }
            SettingsManager.THEME_CUSTOM -> GradientDrawable().apply {
                val color = try {
                    Color.parseColor(settings.customBgColor)
                } catch (e: Exception) {
                    Color.parseColor("#0F172A")
                }
                setColor(color)
            }
            else -> GradientDrawable().apply {
                setColor(Color.parseColor("#1C1C22"))
            }
        }
    }

    fun updateTheme() {
        applyThemeBackground()
        buildKeyboardLayout()
    }

    fun updateImeOptions(imeOptions: Int) {
        currentImeOptions = imeOptions
        buildKeyboardLayout()
    }

    fun setSensitiveMode(sensitive: Boolean) {
        isSensitiveMode = sensitive
    }

    fun setAutoShift(shouldShift: Boolean) {
        if (currentMode == Mode.ENGLISH) {
            // Auto shift layout only if not in manual cap lock
            if (isShifted != shouldShift) {
                isShifted = shouldShift
                buildKeyboardLayout()
            }
        }
    }

    private fun buildKeyboardLayout() {
        keyboardLayout.removeAllViews()

        // 1. Build Toolbar / Control Bar
        keyboardLayout.addView(createToolbar())

        // 2. Build Key Rows
        if (isEmojiVisible) {
            keyboardLayout.addView(createEmojiLayout())
        } else {
            val rows = when (currentMode) {
                Mode.ARABIC -> if (isShifted) Layouts.arabicShifted else Layouts.arabicNormal
                Mode.ENGLISH -> if (isShifted) Layouts.englishShifted else Layouts.englishNormal
                Mode.SYMBOLS -> Layouts.symbolsNormal
                Mode.SYMBOLS_SHIFT -> Layouts.symbolsShifted
            }

            for (row in rows) {
                keyboardLayout.addView(createRowLayout(row))
            }
        }

        // 3. Build Bottom Action Row (Space, Enter, Layout Switches)
        keyboardLayout.addView(createBottomActionRow())
    }

    private fun createToolbar(): View {
        val toolbar = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getToolbarHeight()).apply {
                bottomMargin = dpToPx(6)
            }
            gravity = Gravity.CENTER_VERTICAL
            clipChildren = false
            clipToPadding = false
        }

        // Toolbar Icons (Search, Clipboard, Theme, Emoji Switcher, etc.)
        val tools = listOf("🔍", "📋", "🎨", "⚙️", "💬")
        val toolsLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        }

        for (tool in tools) {
            val toolBtn = TextView(context).apply {
                text = tool
                textSize = 18f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dpToPx(36), dpToPx(36)).apply {
                    leftMargin = dpToPx(4)
                }
                setOnClickListener {
                    playFeedback()
                    when (tool) {
                        "🎨" -> {
                            val themes = listOf(
                                SettingsManager.THEME_LIGHT,
                                SettingsManager.THEME_DARK,
                                SettingsManager.THEME_SPECTRUM,
                                SettingsManager.THEME_GLASSMORPHIC
                            )
                            val nextIndex = (themes.indexOf(settings.selectedTheme) + 1) % themes.size
                            settings.selectedTheme = themes[nextIndex]
                            updateTheme()
                        }
                        "⚙️" -> {
                            keyClickListener?.onKeyClick(KeyboardKey.CODE_SETTINGS, null)
                        }
                        "📋" -> {
                            // Clipboard placeholder
                        }
                        "🔍" -> {
                            // Search placeholder
                        }
                        "💬" -> {
                            // Quick phrases placeholder
                        }
                    }
                }
            }
            toolsLayout.addView(toolBtn)
        }
        toolbar.addView(toolsLayout)

        // Languages Toggle Buttons (العربية | English)
        val langLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        val btnArabic = TextView(context).apply {
            text = "العربية"
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(if (currentMode == Mode.ARABIC && !isEmojiVisible) Color.WHITE else Color.GRAY)
            setKeyBackground(currentMode == Mode.ARABIC && !isEmojiVisible)
            layoutParams = LinearLayout.LayoutParams(dpToPx(60), dpToPx(30)).apply {
                rightMargin = dpToPx(4)
            }
            setOnClickListener {
                playFeedback()
                isEmojiVisible = false
                currentMode = Mode.ARABIC
                buildKeyboardLayout()
            }
        }

        val btnEnglish = TextView(context).apply {
            text = "English"
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(if (currentMode == Mode.ENGLISH && !isEmojiVisible) Color.WHITE else Color.GRAY)
            setKeyBackground(currentMode == Mode.ENGLISH && !isEmojiVisible)
            layoutParams = LinearLayout.LayoutParams(dpToPx(60), dpToPx(30)).apply {
                rightMargin = dpToPx(4)
            }
            setOnClickListener {
                playFeedback()
                isEmojiVisible = false
                currentMode = Mode.ENGLISH
                buildKeyboardLayout()
            }
        }

        langLayout.addView(btnArabic)
        langLayout.addView(btnEnglish)
        toolbar.addView(langLayout)

        return toolbar
    }

    private fun TextView.setKeyBackground(selected: Boolean) {
        val theme = settings.selectedTheme
        val activeBgColor = when (theme) {
            SettingsManager.THEME_LIGHT -> "#E5E5DB"
            SettingsManager.THEME_CUSTOM -> settings.customPrimaryColor
            else -> "#3D3D48"
        }

        background = GradientDrawable().apply {
            cornerRadius = dpToPx(15).toFloat()
            setColor(if (selected) {
                try {
                    Color.parseColor(activeBgColor)
                } catch (e: Exception) {
                    Color.parseColor("#3B82F6")
                }
            } else {
                Color.TRANSPARENT
            })
        }
        if (selected) {
            val isCustomLight = theme == SettingsManager.THEME_CUSTOM && isColorLight(settings.customPrimaryColor)
            setTextColor(if (theme == SettingsManager.THEME_LIGHT || isCustomLight) Color.BLACK else Color.WHITE)
        } else {
            setTextColor(Color.GRAY)
        }
    }

    private fun createRowLayout(keys: List<KeyboardKey>): LinearLayout {
        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getRowHeight()).apply {
                topMargin = getRowVerticalMargin()
                bottomMargin = getRowVerticalMargin()
            }
            gravity = Gravity.CENTER_HORIZONTAL
            clipChildren = false
            clipToPadding = false
        }

        for (key in keys) {
            rowLayout.addView(createKeyView(key))
        }

        return rowLayout
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createKeyView(key: KeyboardKey): View {
        val frame = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, key.weight).apply {
                leftMargin = dpToPx(3)
                rightMargin = dpToPx(3)
            }
            clipChildren = false
            clipToPadding = false
        }

        val textView = TextView(context).apply {
            text = key.label
            textSize = if (key.code < 0) 15f else 19f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            applyKeyThemeStyles(this, key)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }

        textView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.isPressed = true
                    playHaptic()

                    // Synchronously position and display floating preview bubble above the key
                    if (key.code >= 0) {
                        previewOverlay.text = key.label
                        previewOverlay.visibility = View.VISIBLE

                        val parentLocation = IntArray(2)
                        this@TaifKeyboardView.getLocationInWindow(parentLocation)

                        val keyLocation = IntArray(2)
                        v.getLocationInWindow(keyLocation)

                        val relativeX = keyLocation[0] - parentLocation[0]
                        val relativeY = keyLocation[1] - parentLocation[1]

                        val previewWidth = dpToPx(55)
                        val previewHeight = dpToPx(65)

                        val xOffset = relativeX + (v.width - previewWidth) / 2
                        val yOffset = relativeY - previewHeight + dpToPx(8)

                        previewOverlay.x = xOffset.toFloat()
                        previewOverlay.y = yOffset.toFloat()
                    }

                    // Handle accelerated continuous deletion
                    if (key.code == KeyboardKey.CODE_BACKSPACE) {
                        handleKeyAction(key)
                        
                        deleteRunnable?.let { mainHandler.removeCallbacks(it) }
                        deleteRunnable = object : Runnable {
                            var count = 0
                            override fun run() {
                                handleKeyAction(key)
                                count++
                                val delay = if (count > 15) 35L else 70L
                                mainHandler.postDelayed(this, delay)
                            }
                        }
                        mainHandler.postDelayed(deleteRunnable!!, 400)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    v.isPressed = false
                    if (key.code >= 0) {
                        previewOverlay.visibility = View.INVISIBLE
                    }

                    if (key.code == KeyboardKey.CODE_BACKSPACE) {
                        deleteRunnable?.let { mainHandler.removeCallbacks(it) }
                        deleteRunnable = null
                    } else {
                        playClickSound()
                        handleKeyAction(key)
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    v.isPressed = false
                    if (key.code >= 0) {
                        previewOverlay.visibility = View.INVISIBLE
                    }
                    if (key.code == KeyboardKey.CODE_BACKSPACE) {
                        deleteRunnable?.let { mainHandler.removeCallbacks(it) }
                        deleteRunnable = null
                    }
                }
            }
            true
        }

        frame.addView(textView)
        return frame
    }

    private fun applyKeyThemeStyles(textView: TextView, key: KeyboardKey) {
        val theme = settings.selectedTheme

        val lightKeyColor = "#FFFFFF"
        val lightFuncColor = "#E5E5DB"
        val darkKeyColor = "#2D2D35"
        val darkFuncColor = "#3D3D48"

        val gradientSpace = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.parseColor("#3B82F6"), Color.parseColor("#EC4899"))
        ).apply {
            cornerRadius = dpToPx(6).toFloat()
        }

        when (theme) {
            SettingsManager.THEME_LIGHT -> {
                textView.setTextColor(Color.parseColor("#1A1A1A"))
                if (key.code == KeyboardKey.CODE_SPACE || key.code == KeyboardKey.CODE_ENTER) {
                    textView.background = gradientSpace
                    textView.setTextColor(Color.WHITE)
                } else {
                    textView.background = GradientDrawable().apply {
                        setColor(Color.parseColor(if (key.code < 0) lightFuncColor else lightKeyColor))
                        cornerRadius = dpToPx(6).toFloat()
                    }
                }
            }
            SettingsManager.THEME_DARK -> {
                textView.setTextColor(Color.WHITE)
                if (key.code == KeyboardKey.CODE_SPACE || key.code == KeyboardKey.CODE_ENTER) {
                    textView.background = gradientSpace
                } else {
                    textView.background = GradientDrawable().apply {
                        setColor(Color.parseColor(if (key.code < 0) darkFuncColor else darkKeyColor))
                        cornerRadius = dpToPx(6).toFloat()
                    }
                }
            }
            SettingsManager.THEME_SPECTRUM -> {
                textView.setTextColor(Color.WHITE)
                if (key.code == KeyboardKey.CODE_SPACE) {
                    textView.background = GradientDrawable().apply {
                        setColor(Color.parseColor("#FFFFFF"))
                        cornerRadius = dpToPx(6).toFloat()
                    }
                    textView.setTextColor(Color.parseColor("#EC4899"))
                } else {
                    textView.background = GradientDrawable().apply {
                        setColor(Color.parseColor(if (key.code < 0) "#40000000" else "#20FFFFFF"))
                        cornerRadius = dpToPx(6).toFloat()
                    }
                }
            }
            SettingsManager.THEME_GLASSMORPHIC -> {
                textView.setTextColor(Color.WHITE)
                if (key.code == KeyboardKey.CODE_SPACE || key.code == KeyboardKey.CODE_ENTER) {
                    textView.background = gradientSpace
                } else {
                    textView.background = GradientDrawable().apply {
                        setColor(Color.parseColor(if (key.code < 0) "#30FFFFFF" else "#15FFFFFF"))
                        cornerRadius = dpToPx(6).toFloat()
                        setStroke(dpToPx(1), Color.parseColor("#20FFFFFF"))
                    }
                }
            }
            SettingsManager.THEME_CUSTOM -> {
                val isBgLight = isColorLight(settings.customBgColor)
                val textColor = if (isBgLight) Color.BLACK else Color.WHITE
                textView.setTextColor(textColor)

                if (key.code == KeyboardKey.CODE_SPACE || key.code == KeyboardKey.CODE_ENTER) {
                    val customPrimaryDrawable = GradientDrawable().apply {
                        val primColor = try {
                            Color.parseColor(settings.customPrimaryColor)
                        } catch (e: Exception) {
                            Color.parseColor("#3B82F6")
                        }
                        setColor(primColor)
                        cornerRadius = dpToPx(6).toFloat()
                    }
                    textView.background = customPrimaryDrawable
                    val isPrimaryLight = isColorLight(settings.customPrimaryColor)
                    textView.setTextColor(if (isPrimaryLight) Color.BLACK else Color.WHITE)
                } else {
                    textView.background = GradientDrawable().apply {
                        val keyColor = if (isBgLight) {
                            if (key.code < 0) "#30000000" else "#15000000"
                        } else {
                            if (key.code < 0) "#30FFFFFF" else "#15FFFFFF"
                        }
                        setColor(Color.parseColor(keyColor))
                        cornerRadius = dpToPx(6).toFloat()
                        if (!isBgLight) {
                            setStroke(dpToPx(1), Color.parseColor("#10FFFFFF"))
                        } else {
                            setStroke(dpToPx(1), Color.parseColor("#10000000"))
                        }
                    }
                }
            }
        }
    }

    private fun getEnterKeyLabel(): String {
        val actionId = currentImeOptions and EditorInfo.IME_MASK_ACTION
        val isArabic = settings.selectedLanguage == SettingsManager.LANG_ARABIC

        return when (actionId) {
            EditorInfo.IME_ACTION_GO -> if (isArabic) "ذهاب" else "Go"
            EditorInfo.IME_ACTION_SEARCH -> if (isArabic) "بحث" else "Search"
            EditorInfo.IME_ACTION_SEND -> if (isArabic) "إرسال" else "Send"
            EditorInfo.IME_ACTION_NEXT -> if (isArabic) "التالي" else "Next"
            EditorInfo.IME_ACTION_DONE -> if (isArabic) "تم" else "Done"
            else -> "⏎"
        }
    }

    private fun createBottomActionRow(): LinearLayout {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getRowHeight()).apply {
                topMargin = dpToPx(6)
            }
            gravity = Gravity.CENTER_HORIZONTAL
            clipChildren = false
            clipToPadding = false
        }

        // 1. Symbols Switcher (123)
        val btnSymbols = KeyboardKey(if (currentMode == Mode.SYMBOLS) "ABC" else "123", KeyboardKey.CODE_SYMBOLS, 1.5f)
        row.addView(createKeyView(btnSymbols))

        // 2. Language Switcher (Globe icon)
        val btnGlobe = KeyboardKey("🌐", KeyboardKey.CODE_GLOBE, 1.0f)
        row.addView(createKeyView(btnGlobe))

        // 3. Space Bar
        val spaceText = if (settings.selectedLanguage == SettingsManager.LANG_ARABIC) "طيف" else "Taif"
        val btnSpace = KeyboardKey(spaceText, KeyboardKey.CODE_SPACE, 5.0f)
        row.addView(createKeyView(btnSpace))

        // 4. Emoji Button
        val btnEmoji = KeyboardKey("😊", KeyboardKey.CODE_EMOJI, 1.0f)
        row.addView(createKeyView(btnEmoji))

        // 5. Enter / Return Button
        val btnEnter = KeyboardKey(getEnterKeyLabel(), KeyboardKey.CODE_ENTER, 1.5f)
        row.addView(createKeyView(btnEnter))

        return row
    }

    private fun createEmojiLayout(): View {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(186))
        }

        val scroll = HorizontalScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        val emojiContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
        }

        for (rowEmojis in Layouts.emojis) {
            val rowLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dpToPx(40))
            }
            for (emoji in rowEmojis) {
                val emojiBtn = TextView(context).apply {
                    text = emoji
                    textSize = 24f
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(dpToPx(45), dpToPx(40))
                    setOnClickListener {
                        playFeedback()
                        keyClickListener?.onKeyClick(0, emoji)
                    }
                }
                rowLayout.addView(emojiBtn)
            }
            emojiContainer.addView(rowLayout)
        }

        scroll.addView(emojiContainer)
        root.addView(scroll)
        return root
    }

    private fun handleKeyAction(key: KeyboardKey) {
        when (key.code) {
            KeyboardKey.CODE_SHIFT -> {
                if (currentMode == Mode.SYMBOLS) {
                    currentMode = Mode.SYMBOLS_SHIFT
                } else if (currentMode == Mode.SYMBOLS_SHIFT) {
                    currentMode = Mode.SYMBOLS
                } else {
                    isShifted = !isShifted
                }
                buildKeyboardLayout()
            }
            KeyboardKey.CODE_BACKSPACE -> {
                keyClickListener?.onKeyClick(KeyboardKey.CODE_BACKSPACE, null)
            }
            KeyboardKey.CODE_ENTER -> {
                keyClickListener?.onKeyClick(KeyboardKey.CODE_ENTER, null)
            }
            KeyboardKey.CODE_SPACE -> {
                keyClickListener?.onKeyClick(KeyboardKey.CODE_SPACE, " ")
            }
            KeyboardKey.CODE_SYMBOLS -> {
                isEmojiVisible = false
                currentMode = if (currentMode == Mode.SYMBOLS || currentMode == Mode.SYMBOLS_SHIFT) {
                    if (settings.selectedLanguage == SettingsManager.LANG_ARABIC) Mode.ARABIC else Mode.ENGLISH
                } else {
                    Mode.SYMBOLS
                }
                buildKeyboardLayout()
            }
            KeyboardKey.CODE_GLOBE -> {
                isEmojiVisible = false
                val nextLang = if (settings.selectedLanguage == SettingsManager.LANG_ARABIC) {
                    SettingsManager.LANG_ENGLISH
                } else {
                    SettingsManager.LANG_ARABIC
                }
                settings.selectedLanguage = nextLang
                currentMode = if (nextLang == SettingsManager.LANG_ARABIC) Mode.ARABIC else Mode.ENGLISH
                buildKeyboardLayout()
            }
            KeyboardKey.CODE_EMOJI -> {
                isEmojiVisible = !isEmojiVisible
                buildKeyboardLayout()
            }
            KeyboardKey.CODE_SETTINGS -> {
                keyClickListener?.onKeyClick(KeyboardKey.CODE_SETTINGS, null)
            }
            else -> {
                keyClickListener?.onKeyClick(key.code, key.label)
            }
        }
    }

    private fun playFeedback() {
        playClickSound()
        playHaptic()
    }

    private fun playClickSound() {
        if (settings.isSoundEnabled) {
            try {
                audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun playHaptic() {
        if (settings.isHapticEnabled) {
            try {
                vibrator?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(15)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun isColorLight(colorStr: String): Boolean {
        return try {
            val color = Color.parseColor(colorStr)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            val luminance = (0.299 * r + 0.587 * g + 0.114 * b)
            luminance > 160
        } catch (e: Exception) {
            false
        }
    }
}
