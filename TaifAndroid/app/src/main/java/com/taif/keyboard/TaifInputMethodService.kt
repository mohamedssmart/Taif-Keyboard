package com.taif.keyboard

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

class TaifInputMethodService : InputMethodService(), TaifKeyboardView.OnKeyClickListener {

    private var keyboardView: TaifKeyboardView? = null

    override fun onCreateInputView(): View {
        return try {
            val view = TaifKeyboardView(this)
            view.keyClickListener = this
            keyboardView = view
            view
        } catch (e: Exception) {
            android.util.Log.e("TaifKeyboard", "Error creating keyboard view", e)
            saveCrashLog(e)
            View(this) // Fallback view to prevent system force close
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        try {
            super.onStartInputView(info, restarting)
            
            val kView = keyboardView ?: return
            
            // 1. Refresh theme configuration
            kView.updateTheme()
            
            // 2. Pass IME Options to update the enter key text/label dynamically
            val imeOptions = info?.imeOptions ?: EditorInfo.IME_ACTION_UNSPECIFIED
            kView.updateImeOptions(imeOptions)
            
            // 3. Handle Sensitive Mode (Passwords/PINs)
            val inputClass = info?.inputType?.and(EditorInfo.TYPE_MASK_CLASS)
            val variation = info?.inputType?.and(EditorInfo.TYPE_MASK_VARIATION)
            val isPassword = inputClass == EditorInfo.TYPE_CLASS_NUMBER && variation == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD ||
                    inputClass == EditorInfo.TYPE_CLASS_TEXT && (
                    variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                    variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                    variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
                    )
            kView.setSensitiveMode(isPassword)

            // 4. Handle initial Auto-Capitalization state
            updateAutoCapsState()
        } catch (e: Exception) {
            android.util.Log.e("TaifKeyboard", "Error in onStartInputView", e)
            saveCrashLog(e)
        }
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        try {
            super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
            updateAutoCapsState()
        } catch (e: Exception) {
            android.util.Log.e("TaifKeyboard", "Error in onUpdateSelection", e)
            saveCrashLog(e)
        }
    }

    private fun updateAutoCapsState() {
        try {
            val ic = currentInputConnection ?: return
            val info = currentInputEditorInfo ?: return
            val caps = ic.getCursorCapsMode(info.inputType)
            val shouldShift = caps != 0
            keyboardView?.setAutoShift(shouldShift)
        } catch (e: Exception) {
            android.util.Log.e("TaifKeyboard", "Error in updateAutoCapsState", e)
            saveCrashLog(e)
        }
    }

    private fun saveCrashLog(e: Exception) {
        try {
            val sw = java.io.StringWriter()
            e.printStackTrace(java.io.PrintWriter(sw))
            val logString = sw.toString()
            SettingsManager(this).lastCrashLog = logString
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onKeyClick(code: Int, text: String?) {
        try {
            val ic = currentInputConnection ?: return

            when (code) {
                KeyboardKey.CODE_BACKSPACE -> {
                    // Delete one character
                    val selectedText = ic.getSelectedText(0)
                    if (selectedText.isNullOrEmpty()) {
                        ic.deleteSurroundingText(1, 0)
                    } else {
                        ic.commitText("", 1)
                    }
                }
                KeyboardKey.CODE_ENTER -> {
                    val editorInfo = currentInputEditorInfo
                    if (editorInfo != null) {
                        val action = editorInfo.actionId
                        val actionId = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
                        if (action != 0) {
                            ic.performEditorAction(action)
                        } else if (actionId != EditorInfo.IME_ACTION_NONE && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                            ic.performEditorAction(actionId)
                        } else {
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                        }
                    } else {
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                    }
                }
                KeyboardKey.CODE_SETTINGS -> {
                    // Open Settings/Onboarding App
                    val intent = Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }
                else -> {
                    if (text != null) {
                        ic.commitText(text, 1)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TaifKeyboard", "Error in onKeyClick", e)
            saveCrashLog(e)
        }
    }
}

