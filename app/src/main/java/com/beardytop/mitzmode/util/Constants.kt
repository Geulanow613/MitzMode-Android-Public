package com.beardytop.mitzmode.util

object Constants {
    val MALE_REQUIRED_ITEMS = listOf(
        "Ritual hand washing",
        "Have Mezuzot on your doorposts",
        "Wear a Kippah (head covering)",
        "Put on Tefillin during morning prayers (except Shabbat/Festivals)",
        "Wear Tzitzit (recommended for divine protection)",
        "Keep Kosher",
        "Morning Blessings (Birchot HaShachar)",
        "Torah Blessings + minimal Torah study",
        "Weekly Parsha reading (twice in Hebrew, once in Targum)",
        "Minimum Pesukei D'Zimra (Baruch She'amar, Ashrei, Yishtabach)",
        "Morning Shema with its blessings",
        "Shemoneh Esrei",
        "Mincha - Shemoneh Esrei",
        "Evening Shema with its blessings",
        "Maariv Shemoneh Esrei",
        "Bedtime Shema (first paragraph - though recommended to say entire Shema for spiritual protection)",
        "Hamapil blessing (according to many opinions)",
        "Prepare for and observe Shabbat and Festivals",
        "Say 100 brachot (blessings) today"
    )

    val FEMALE_REQUIRED_ITEMS = listOf(
        "Ritual hand washing",
        "Have Mezuzot on your doorposts",
        "At least one prayer daily (typically morning)",
        "Cover hair in public (if married)",
        "Modesty (Tznius)",
        "Blessings before food",
        "Blessings after food",
        "Asher Yatzar after using bathroom",
        "Keep Kosher",
        "Family Purity Laws (if married)",
        "Prepare for and observe Shabbat and Festivals",
        "Torah Study"
    )

    val HEAD_COVERING_EXPLANATIONS = mapOf(
        "male" to """
            Jewish men wear a kippah (skullcap) as a sign of respect and acknowledgment that G-d is above us. 
            
            While some authorities consider it a custom rather than a strict requirement, wearing a kippah is universally practiced by observant Jewish men and boys.
            
            It serves as a constant reminder of G-d's presence and our duty to live according to His will.
        """.trimIndent(),
        
        "female" to """
            Married Jewish women cover their hair when in public as a sign of modesty (tzniut) and to preserve the special nature of marriage.
            
            The manner of covering varies by community - some use wigs, others use scarves, hats, or a combination.
            
            This practice is based on biblical and rabbinic sources and is considered a fundamental aspect of Jewish law for married women.
        """.trimIndent()
    )
} 