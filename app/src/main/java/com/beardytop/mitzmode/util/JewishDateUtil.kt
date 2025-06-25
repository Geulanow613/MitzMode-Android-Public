import java.util.Calendar

object JewishDateUtil {
    fun shouldInsertYaalehVeyavo(): Boolean {
        // For testing, always return true
        return true
    }

    fun getHolidayInsertion(): String? {
        // Show all options in order
        return """
            ביום ראש החדש הזה
            ביום חג המצות הזה
            ביום חג הסכות הזה
        """.trimIndent()
    }

    fun getHolidayInsertionEnglish(): String? {
        // Show all options in order
        return """
            on this day of the New Moon
            on this day of the Festival of Matzot
            on this day of the Festival of Sukkot
        """.trimIndent()
    }
} 