package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val nativeLabel: String) {
    ENGLISH("en", "English", "English"),
    URDU("ur", "Urdu", "اردو"),
    ROMAN_URDU("roman_ur", "Roman Urdu", "Roman Urdu");

    val isRtl: Boolean
        get() = this == URDU

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

fun localized(language: AppLanguage, en: String, ur: String, roman: String): String =
    when (language) {
        AppLanguage.ENGLISH -> en
        AppLanguage.URDU -> ur
        AppLanguage.ROMAN_URDU -> roman
    }
