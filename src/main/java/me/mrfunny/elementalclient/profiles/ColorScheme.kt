package me.mrfunny.elementalclient.profiles

enum class ColorScheme(colorCode: Char='f') {
    ORANGE('6'), GREEN('a');

    companion object {
        val values = values()
            .map { theme -> theme.name.replaceFirstChar { it.uppercaseChar() } }
    }
}