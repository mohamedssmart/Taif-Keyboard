package com.taif.keyboard

import android.content.Context
import android.content.SharedPreferences

class DictionaryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Memory cache of learned words and their frequencies
    private val userWords = mutableMapOf<String, Int>()

    // Common preloaded Arabic and English words with a base frequency of 1
    private val commonWords = listOf(
        // Arabic
        "السلام", "عليكم", "ورحمة", "الله", "وبركاته", "كيف", "الحال", "الحمد", "لله", "تمام", 
        "إن", "شاء", "شكراً", "جزيلاً", "صباح", "الخير", "مساء", "نعم", "لا", "أنا", "في", "من", 
        "على", "إلى", "يا", "حبيبي", "أخي", "مع", "هذا", "الذي", "التي", "اليوم", "جميل", "حاضر", 
        "أنت", "هو", "هي", "نحن", "هم", "كان", "يكون", "سيكون", "خير", "كبير", "جديد", "سعيد", "طيب",
        // English
        "the", "and", "you", "that", "was", "for", "are", "with", "his", "they", "this", "have",
        "from", "one", "had", "word", "but", "not", "what", "some", "were", "we", "when", "your",
        "can", "said", "there", "use", "an", "each", "which", "she", "do", "how", "their", "if",
        "will", "up", "other", "about", "out", "many", "then", "them", "these", "so", "some",
        "her", "would", "make", "like", "him", "into", "time", "has", "look", "two", "more", "write",
        "go", "see", "number", "no", "way", "could", "people", "my", "than", "first", "water",
        "been", "call", "who", "oil", "its", "now", "find", "long", "down", "day", "did", "get",
        "come", "made", "may", "part"
    )

    init {
        loadUserWords()
    }

    private fun loadUserWords() {
        try {
            userWords.clear()
            val savedData = prefs.getString(KEY_USER_DICT, "") ?: ""
            if (savedData.isNotEmpty()) {
                // Format: word1:freq1|word2:freq2|...
                val pairs = savedData.split("|")
                for (pair in pairs) {
                    val parts = pair.split(":")
                    if (parts.size == 2) {
                        val word = parts[0]
                        val freq = parts[1].toIntOrNull() ?: 1
                        if (word.isNotEmpty()) {
                            userWords[word] = freq
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveUserWords() {
        try {
            // Encode map to string: word1:freq1|word2:freq2|...
            val sb = java.lang.StringBuilder()
            for ((word, freq) in userWords) {
                if (sb.length > 0) {
                    sb.append("|")
                }
                sb.append(word).append(":").append(freq)
            }
            prefs.edit().putString(KEY_USER_DICT, sb.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Learn a new word or increment its frequency if already learned
     */
    fun learnWord(word: String) {
        val cleanWord = word.trim().lowercase()
        if (cleanWord.length < 2 || cleanWord.any { it.isDigit() }) return
        
        try {
            val currentFreq = userWords[cleanWord] ?: 0
            userWords[cleanWord] = currentFreq + 1
            saveUserWords()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Find top 3 suggestions that start with [prefix]
     */
    fun getSuggestions(prefix: String): List<String> {
        val cleanPrefix = prefix.trim().lowercase()
        if (cleanPrefix.isEmpty()) return emptyList()

        // 1. Gather all candidates that match the prefix
        val candidates = mutableListOf<Pair<String, Int>>()

        // User learned words (higher priority, dynamic frequency)
        for ((word, freq) in userWords) {
            if (word.startsWith(cleanPrefix)) {
                candidates.add(Pair(word, freq + 10)) // boost user learned words
            }
        }

        // Common preloaded words (base frequency = 1)
        for (word in commonWords) {
            val lowercaseWord = word.lowercase()
            if (lowercaseWord.startsWith(cleanPrefix)) {
                // Avoid duplicates
                if (candidates.none { it.first == lowercaseWord }) {
                    candidates.add(Pair(lowercaseWord, 1))
                }
            }
        }

        // 2. Sort candidates by:
        // - Length of prefix (exact matches first)
        // - Frequency count (descending)
        candidates.sortWith(Comparator { p1, p2 ->
            // If one is exact match, put it first
            if (p1.first == cleanPrefix && p2.first != cleanPrefix) return@Comparator -1
            if (p2.first == cleanPrefix && p1.first != cleanPrefix) return@Comparator 1
            
            // Sort by frequency desc
            p2.second.compareTo(p1.second)
        })

        // 3. Return top 3 candidates as simple strings
        return candidates.take(3).map { it.first }
    }

    companion object {
        private const val PREFS_NAME = "taif_dictionary_settings"
        private const val KEY_USER_DICT = "user_dictionary_data"
    }
}
