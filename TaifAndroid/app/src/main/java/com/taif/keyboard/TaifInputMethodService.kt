package com.taif.keyboard

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

class TaifInputMethodService : InputMethodService(), TaifKeyboardView.OnKeyClickListener {

    private var keyboardView: TaifKeyboardView? = null

    override fun onCreateInputView(): View {
        val view = TaifKeyboardView(this)
        view.keyClickListener = this
        keyboardView = view
        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        // Refresh theme configuration when the keyboard is displayed
        keyboardView?.updateTheme()
    }

    override fun onKeyClick(code: Int, text: String?) {
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
                val action = currentInputEditorInfo.actionId
                if (action != 0) {
                    ic.performEditorAction(action)
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
    }
}
