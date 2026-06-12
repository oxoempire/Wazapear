package com.oxoempire.wazapear

data class Country(
    val code: String,       // e.g., "+56"
    val dialCode: String,   // e.g., "56" (without the plus)
    val nameEn: String,
    val nameEs: String,
    val flag: String,       // Emoji flag
    val maxDigits: Int      // Expected phone number length
)

object Countries {
    val list = listOf(
        Country("+56", "56", "Chile", "Chile", "🇨🇱", 9),
        Country("+34", "34", "Spain", "España", "🇪🇸", 9),
        Country("+54", "54", "Argentina", "Argentina", "🇦🇷", 10),
        Country("+52", "52", "Mexico", "México", "🇲🇽", 10),
        Country("+57", "57", "Colombia", "Colombia", "🇨🇴", 10),
        Country("+51", "51", "Peru", "Perú", "🇵🇪", 9),
        Country("+1", "1", "USA / Canada", "EE.UU. / Canadá", "🇺🇸", 10),
        Country("+55", "55", "Brazil", "Brasil", "🇧🇷", 11),
        Country("+58", "58", "Venezuela", "Venezuela", "🇻🇪", 10),
        Country("+593", "593", "Ecuador", "Ecuador", "🇪🇨", 9),
        Country("+591", "591", "Bolivia", "Bolivia", "🇧🇴", 8),
        Country("+598", "598", "Uruguay", "Uruguay", "🇺🇾", 8),
        Country("+595", "595", "Paraguay", "Paraguay", "🇵🇾", 9),
        Country("+506", "506", "Costa Rica", "Costa Rica", "🇨🇷", 8),
        Country("+507", "507", "Panama", "Panamá", "🇵🇦", 8),
        Country("+44", "44", "United Kingdom", "Reino Unido", "🇬🇧", 10),
        Country("+49", "49", "Germany", "Alemania", "🇩🇪", 11),
        Country("+33", "33", "France", "Francia", "🇫🇷", 9),
        Country("+39", "39", "Italy", "Italia", "🇮🇹", 10),
        Country("+351", "351", "Portugal", "Portugal", "🇵🇹", 9),
        Country("+81", "81", "Japan", "Japón", "🇯🇵", 10),
        Country("+86", "86", "China", "China", "🇨🇳", 11),
        Country("+91", "91", "India", "India", "🇮🇳", 10)
    ).sortedBy { it.nameEs } // Sort alphabetically by default

    // Helper to get country by dial code or return Chile as default
    fun getByDialCode(dialCode: String): Country {
        return list.find { it.dialCode == dialCode } ?: list.first { it.dialCode == "56" }
    }
}
