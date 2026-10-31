package com.example.localization

data class LanguageInfo(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val isRtl: Boolean = false
)

object SupportedLanguages {
    val list = listOf(
        LanguageInfo("en", "English", "English"),
        LanguageInfo("ta", "Tamil", "தமிழ்"),
        LanguageInfo("hi", "Hindi", "हिन्दी"),
        LanguageInfo("te", "Telugu", "తెలుగు"),
        LanguageInfo("kn", "Kannada", "ಕನ್ನಡ"),
        LanguageInfo("ml", "Malayalam", "മലയാളം"),
        LanguageInfo("bn", "Bengali", "বাংলা"),
        LanguageInfo("mr", "Marathi", "मराठी"),
        LanguageInfo("gu", "Gujarati", "ગુજરાતી"),
        LanguageInfo("pa", "Punjabi", "ਪੰਜਾਬੀ"),
        LanguageInfo("ur", "Urdu", "اردو", isRtl = true),
        LanguageInfo("or", "Odia", "ଓଡ଼ିଆ"),
        LanguageInfo("as", "Assamese", "অসমীয়া"),
        LanguageInfo("sa", "Sanskrit", "संस्कृतम्"),
        LanguageInfo("ne", "Nepali", "नेपाली"),
        LanguageInfo("kok", "Konkani", "कोंकणी"),
        LanguageInfo("ks", "Kashmiri", "کٲشُر", isRtl = true),
        LanguageInfo("sd", "Sindhi", "سنڌي", isRtl = true),
        LanguageInfo("mni", "Manipuri", "মৈতৈলোন্"),
        LanguageInfo("brx", "Bodo", "बड़ो"),
        LanguageInfo("doi", "Dogri", "डोगरी"),
        LanguageInfo("mai", "Maithili", "मैथिली"),
        LanguageInfo("sat", "Santali", "ᱥᱟᱱᱛᱟᱲᱤ"),
        LanguageInfo("fr", "French", "Français"),
        LanguageInfo("es", "Spanish", "Español"),
        LanguageInfo("de", "German", "Deutsch"),
        LanguageInfo("pt", "Portuguese", "Português"),
        LanguageInfo("it", "Italian", "Italiano"),
        LanguageInfo("nl", "Dutch", "Nederlands"),
        LanguageInfo("ru", "Russian", "Русский"),
        LanguageInfo("ar", "Arabic", "العربية", isRtl = true),
        LanguageInfo("zh-CN", "Chinese Simplified", "简体中文"),
        LanguageInfo("zh-TW", "Chinese Traditional", "繁體中文"),
        LanguageInfo("ja", "Japanese", "日本語"),
        LanguageInfo("ko", "Korean", "한국어"),
        LanguageInfo("th", "Thai", "ไทย"),
        LanguageInfo("vi", "Vietnamese", "Tiếng Việt"),
        LanguageInfo("id", "Indonesian", "Bahasa Indonesia"),
        LanguageInfo("tr", "Turkish", "Türkçe"),
        LanguageInfo("si", "Sinhala", "සිංහල"),
        LanguageInfo("he", "Hebrew", "עברית", isRtl = true),
        LanguageInfo("pl", "Polish", "Polski"),
        LanguageInfo("el", "Greek", "Ελληνικά"),
        LanguageInfo("sv", "Swedish", "Svenska"),
        LanguageInfo("da", "Danish", "Dansk"),
        LanguageInfo("no", "Norwegian", "Norsk"),
        LanguageInfo("fi", "Finnish", "Suomi"),
        LanguageInfo("ro", "Romanian", "Română"),
        LanguageInfo("uk", "Ukrainian", "Українська"),
        LanguageInfo("fa", "Persian", "فارسی", isRtl = true)
    )

    fun find(code: String): LanguageInfo =
        list.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: list[0]
}
