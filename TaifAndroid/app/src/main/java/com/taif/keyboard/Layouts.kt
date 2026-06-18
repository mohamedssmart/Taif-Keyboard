package com.taif.keyboard

data class KeyboardKey(
    val label: String,
    val code: Int,
    val weight: Float = 1.0f
) {
    companion object {
        const val CODE_SHIFT = -1
        const val CODE_BACKSPACE = -2
        const val CODE_ENTER = -3
        const val CODE_SYMBOLS = -4
        const val CODE_GLOBE = -5
        const val CODE_SETTINGS = -6
        const val CODE_EMOJI = -7
        const val CODE_SPACE = 32
    }
}

object Layouts {
    val englishNormal = listOf(
        // Row 1
        listOf(
            KeyboardKey("q", 113), KeyboardKey("w", 119), KeyboardKey("e", 101), KeyboardKey("r", 114),
            KeyboardKey("t", 116), KeyboardKey("y", 121), KeyboardKey("u", 117), KeyboardKey("i", 105),
            KeyboardKey("o", 111), KeyboardKey("p", 112)
        ),
        // Row 2
        listOf(
            KeyboardKey("a", 97), KeyboardKey("s", 115), KeyboardKey("d", 100), KeyboardKey("f", 102),
            KeyboardKey("g", 103), KeyboardKey("h", 104), KeyboardKey("j", 106), KeyboardKey("k", 107),
            KeyboardKey("l", 108)
        ),
        // Row 3
        listOf(
            KeyboardKey("⇧", KeyboardKey.CODE_SHIFT, 1.5f),
            KeyboardKey("z", 122), KeyboardKey("x", 120), KeyboardKey("c", 99), KeyboardKey("v", 118),
            KeyboardKey("b", 98), KeyboardKey("n", 110), KeyboardKey("m", 109),
            KeyboardKey("⌫", KeyboardKey.CODE_BACKSPACE, 1.5f)
        )
    )

    val englishShifted = listOf(
        // Row 1
        listOf(
            KeyboardKey("Q", 81), KeyboardKey("W", 87), KeyboardKey("E", 69), KeyboardKey("R", 82),
            KeyboardKey("T", 84), KeyboardKey("Y", 89), KeyboardKey("U", 85), KeyboardKey("I", 73),
            KeyboardKey("O", 79), KeyboardKey("P", 80)
        ),
        // Row 2
        listOf(
            KeyboardKey("A", 65), KeyboardKey("S", 83), KeyboardKey("D", 68), KeyboardKey("F", 70),
            KeyboardKey("G", 71), KeyboardKey("H", 72), KeyboardKey("J", 74), KeyboardKey("K", 75),
            KeyboardKey("L", 76)
        ),
        // Row 3
        listOf(
            KeyboardKey("⇧", KeyboardKey.CODE_SHIFT, 1.5f),
            KeyboardKey("Z", 90), KeyboardKey("X", 88), KeyboardKey("C", 67), KeyboardKey("V", 86),
            KeyboardKey("B", 66), KeyboardKey("N", 78), KeyboardKey("M", 77),
            KeyboardKey("⌫", KeyboardKey.CODE_BACKSPACE, 1.5f)
        )
    )

    val arabicNormal = listOf(
        // Row 1
        listOf(
            KeyboardKey("ض", 1590), KeyboardKey("ص", 1589), KeyboardKey("ث", 1579), KeyboardKey("ق", 1602),
            KeyboardKey("ف", 1601), KeyboardKey("غ", 1594), KeyboardKey("ع", 1593), KeyboardKey("ه", 1607),
            KeyboardKey("خ", 1582), KeyboardKey("ح", 1581), KeyboardKey("ج", 1587)
        ),
        // Row 2
        listOf(
            KeyboardKey("ش", 1588), KeyboardKey("س", 1587), KeyboardKey("ي", 1610), KeyboardKey("ب", 1576),
            KeyboardKey("ل", 1604), KeyboardKey("ا", 1575), KeyboardKey("ت", 1578), KeyboardKey("ن", 1606),
            KeyboardKey("م", 1605), KeyboardKey("ك", 1603), KeyboardKey("ط", 1591)
        ),
        // Row 3
        listOf(
            KeyboardKey("⇧", KeyboardKey.CODE_SHIFT, 1.2f),
            KeyboardKey("ئ", 1574), KeyboardKey("ء", 1569), KeyboardKey("ؤ", 1572), KeyboardKey("ر", 1585),
            KeyboardKey("لا", 1604), KeyboardKey("ى", 1579), KeyboardKey("ة", 1577), KeyboardKey("و", 1608),
            KeyboardKey("ز", 1586), KeyboardKey("ظ", 1592), KeyboardKey("د", 1583), KeyboardKey("ذ", 1580),
            KeyboardKey("⌫", KeyboardKey.CODE_BACKSPACE, 1.2f)
        )
    )

    val arabicShifted = listOf(
        // Row 1
        listOf(
            KeyboardKey("َ", 1614), KeyboardKey("ً", 1611), KeyboardKey("ُ", 1615), KeyboardKey("ٌ", 1612),
            KeyboardKey("إ", 1573), KeyboardKey("`", 96), KeyboardKey("~", 126), KeyboardKey("أ", 1571),
            KeyboardKey("آ", 1570), KeyboardKey("]", 93), KeyboardKey("[", 91)
        ),
        // Row 2
        listOf(
            KeyboardKey("ِ", 1616), KeyboardKey("ٍ", 1613), KeyboardKey("ْ", 1618), KeyboardKey("ّ", 1617),
            KeyboardKey("لإ", 1604), KeyboardKey("لأ", 1604), KeyboardKey("لآ", 1604), KeyboardKey("ي", 1610),
            KeyboardKey("؛", 1563), KeyboardKey(":", 58), KeyboardKey("\"", 34)
        ),
        // Row 3
        listOf(
            KeyboardKey("⇧", KeyboardKey.CODE_SHIFT, 1.2f),
            KeyboardKey("ـ", 1600), KeyboardKey("ئ", 1574), KeyboardKey("ء", 1569), KeyboardKey("ؤ", 1572),
            KeyboardKey("<", 60), KeyboardKey(">", 62), KeyboardKey("ة", 1577), KeyboardKey(",", 44),
            KeyboardKey(".", 46), KeyboardKey("؟", 1567), KeyboardKey("!", 33), KeyboardKey("ذ", 1580),
            KeyboardKey("⌫", KeyboardKey.CODE_BACKSPACE, 1.2f)
        )
    )

    val symbolsNormal = listOf(
        listOf(
            KeyboardKey("1", 49), KeyboardKey("2", 50), KeyboardKey("3", 51), KeyboardKey("4", 52),
            KeyboardKey("5", 53), KeyboardKey("6", 54), KeyboardKey("7", 55), KeyboardKey("8", 56),
            KeyboardKey("9", 57), KeyboardKey("0", 48)
        ),
        listOf(
            KeyboardKey("-", 45), KeyboardKey("/", 47), KeyboardKey(":", 58), KeyboardKey(";", 59),
            KeyboardKey("(", 40), KeyboardKey(")", 41), KeyboardKey("$", 36), KeyboardKey("&", 38),
            KeyboardKey("@", 64), KeyboardKey("\"", 34)
        ),
        listOf(
            KeyboardKey("⇧", KeyboardKey.CODE_SHIFT, 1.5f),
            KeyboardKey(".", 46), KeyboardKey(",", 44), KeyboardKey("?", 63), KeyboardKey("!", 33),
            KeyboardKey("'", 39), KeyboardKey("_", 95), KeyboardKey("\\", 92),
            KeyboardKey("⌫", KeyboardKey.CODE_BACKSPACE, 1.5f)
        )
    )

    val symbolsShifted = listOf(
        listOf(
            KeyboardKey("[", 91), KeyboardKey("]", 93), KeyboardKey("{", 123), KeyboardKey("}", 125),
            KeyboardKey("#", 35), KeyboardKey("%", 37), KeyboardKey("^", 94), KeyboardKey("*", 42),
            KeyboardKey("+", 43), KeyboardKey("=", 61)
        ),
        listOf(
            KeyboardKey("_", 95), KeyboardKey("\\", 92), KeyboardKey("|", 124), KeyboardKey("~", 126),
            KeyboardKey("<", 60), KeyboardKey(">", 62), KeyboardKey("€", 8364), KeyboardKey("£", 163),
            KeyboardKey("¥", 165), KeyboardKey("•", 8226)
        ),
        listOf(
            KeyboardKey("⇧", KeyboardKey.CODE_SHIFT, 1.5f),
            KeyboardKey(".", 46), KeyboardKey(",", 44), KeyboardKey("?", 63), KeyboardKey("!", 33),
            KeyboardKey("'", 39), KeyboardKey("¿", 191), KeyboardKey("¡", 161),
            KeyboardKey("⌫", KeyboardKey.CODE_BACKSPACE, 1.5f)
        )
    )

    val emojis = listOf(
        // Row 1: Smileys & Expressions (66 emojis)
        listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", 
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", 
            "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", 
            "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", 
            "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", 
            "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😈", "👿", "💀", "☠️", 
            "👻", "💩", "🤡", "👽", "👾", "🤖"
        ),
        // Row 2: Hands, Body & Hearts (74 emojis)
        listOf(
            "👋", "🤚", "🖐", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞", 
            "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍", 
            "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", 
            "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂", 
            "🦻", "👃", "🧠", "🫀", "🫁", "🦷", "🦴", "👀", "👁", "👅", 
            "👄", "💋", "🩸", "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", 
            "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", 
            "💖", "💘", "💝", "💟"
        ),
        // Row 3: Animals & Nature (65 emojis)
        listOf(
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", 
            "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", 
            "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", 
            "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", 
            "🦟", "🦗", "🕷", "🕸", "🦂", "🐢", "🐍", "🦎", "🐙", "🦑", 
            "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🐋", "🦈", 
            "🐊", "🐅", "🐆", "🦓", "🦍"
        ),
        // Row 4: Food, Drink, Activities & Objects (65 emojis)
        listOf(
            "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", 
            "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", 
            "🥬", "🥒", "🌶", "🫑", "🌽", "🥕", "🥐", "🥯", "🍞", "🥖", 
            "🥨", "🧀", "🍳", "🍔", "🍟", "🍕", "🌭", "🥪", "🌮", "🌯", 
            "🍿", "🍩", "🍪", "🎂", "🍰", "🧁", "🍫", "🍬", "🍭", "🍯", 
            "☕", "🍺", "🍻", "🥤", "⚽", "🏀", "🏈", "🎮", "🚗", "✈️", 
            "🚀", "🎈", "🎉", "🎁", "📱"
        )
    )
}
