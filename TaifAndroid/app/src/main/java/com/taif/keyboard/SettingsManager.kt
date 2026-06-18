package com.taif.keyboard

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var isHapticEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC, value).apply()

    var selectedTheme: String
        get() = prefs.getString(KEY_THEME, THEME_SPECTRUM) ?: THEME_SPECTRUM
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var selectedLanguage: String
        get() = prefs.getString(KEY_LANG, LANG_ARABIC) ?: LANG_ARABIC
        set(value) = prefs.edit().putString(KEY_LANG, value).apply()

    companion object {
        private const val PREFS_NAME = "taif_settings"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_HAPTIC = "haptic_enabled"
        private const val KEY_THEME = "keyboard_theme"
        private const val KEY_LANG = "keyboard_lang"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SPECTRUM = "spectrum"
        const val THEME_GLASSMORPHIC = "glassmorphic"

        const val LANG_ARABIC = "arabic"
        const val LANG_ENGLISH = "english"
    }
}
