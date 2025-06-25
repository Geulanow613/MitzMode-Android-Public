package com.beardytop.mitzmode.data

data class TefilatSection(
    val title: String,
    val hebrew: String,
    val english: String? = null
)

object TefilatHaderechData {
    val sections = listOf(
        TefilatSection(
            title = "תפילת הדרך",
            hebrew = """
                יְהִי רָצוֹן מִלְּפָנֶיךָ ה׳ אֱלֹהֵינוּ וֵאלֹהֵי אֲבוֹתֵינוּ שֶׁתּוֹלִיכֵנוּ לְשָׁלוֹם וְתַצְעִידֵנוּ לְשָׁלוֹם וְתַדְרִיכֵנוּ לְשָׁלוֹם׃
                וְתִסְמְכֵנוּ לְשָׁלוֹם וְתַגִּיעֵנוּ לִמְחוֹז חֶפְצֵנוּ לְחַיִּים וּלְשִׂמְחָה וּלְשָׁלוֹם׃
                וְתַצִּילֵנוּ מִכַּף כָּל אוֹיֵב וְאוֹרֵב וְלִסְטִים וְחַיּוֹת רָעוֹת בַּדֶּרֶךְ וּמִכָּל מִינֵי פֻּרְעָנִיּוֹת הַמִּתְרַגְּשׁוֹת לָבוֹא לָעוֹלָם׃
                וְתִשְׁלַח בְּרָכָה בְּכָל מַעֲשֵׂה יָדֵינוּ וְתִתְּנֵנוּ לְחֵן וּלְחֶסֶד וּלְרַחֲמִים בְּעֵינֶיךָ וּבְעֵינֵי כָל רוֹאֵינוּ׃
                וְתִשְׁמַע קוֹל תַּחֲנוּנֵינוּ כִּי אֵל שׁוֹמֵעַ תְּפִלָּה וְתַחֲנוּן אָתָּה׃
                בָּרוּךְ אַתָּה ה׳ שׁוֹמֵעַ תְּפִלָּה׃
            """.trimIndent(),
            english = """
                May it be Your will, Lord our God and God of our ancestors, that You lead us toward peace, guide our footsteps toward peace, and make us reach our desired destination for life, gladness, and peace.
                May You rescue us from the hand of every foe, ambush along the way, and from all manner of punishments that assemble to come to earth.
                May You send blessing in our handiwork, and grant us grace, kindness, and mercy in Your eyes and in the eyes of all who see us.
                May You hear the voice of our supplication because You are God Who hears prayer and supplication.
                Blessed are You, Lord, Who hears prayer.
            """.trimIndent()
        )
    )
} 