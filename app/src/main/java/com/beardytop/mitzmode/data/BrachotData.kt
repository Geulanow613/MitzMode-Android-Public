package com.beardytop.mitzmode.data

data class BrachaSection(
    val title: String,
    val hebrew: String,
    val english: String? = null,
    val category: BrachaCategory? = null
)

enum class BrachaCategory {
    FOOD,
    DRINK,
    FRAGRANCE,
    NATURAL_PHENOMENA,
    MITZVOT,
    OTHER,
    AFTER_FOOD
}

object BrachotData {
    val sections = listOf(
        BrachaSection(
            title = "אשר יצר",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם אֲשֶׁר יָצַר אֶת הָאָדָם בְּחָכְמָה וּבָרָא בוֹ נְקָבִים נְקָבִים חֲלוּלִים חֲלוּלִים גָּלוּי וְיָדוּעַ לִפְנֵי כִסֵּא כְבוֹדֶךָ שֶׁאִם יִפָּתֵחַ אֶחָד מֵהֶם אוֹ יִסָּתֵם אֶחָד מֵהֶם אִי אֶפְשַׁר לְהִתְקַיֵּם וְלַעֲמוֹד לְפָנֶיךָ אֲפִילוּ שָׁעָה אֶחָת בָּרוּךְ אַתָּה ה׳ רוֹפֵא כָל בָּשָׂר וּמַפְלִיא לַעֲשׂוֹת׃",
            english = "Blessed are You, Lord our God, King of the universe, Who formed man with wisdom and created within him many openings and many hollow spaces. It is obvious and known before Your Throne of Glory that if even one of them would be opened, or if even one of them would be sealed, it would be impossible to survive and to stand before You even for one hour. Blessed are You, Lord, Who heals all flesh and acts wondrously.",
            category = BrachaCategory.OTHER
        ),
        BrachaSection(
            title = "המוציא",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם הַמּוֹצִיא לֶחֶם מִן הָאָרֶץ׃",
            english = "Blessed are You, Lord our God, King of the universe, Who brings forth bread from the earth.",
            category = BrachaCategory.FOOD
        ),
        BrachaSection(
            title = "מזונות",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא מִינֵי מְזוֹנוֹת׃",
            english = "Blessed are You, Lord our God, King of the universe, Who creates various kinds of sustenance.",
            category = BrachaCategory.FOOD
        ),
        BrachaSection(
            title = "הגפן",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא פְּרִי הַגָּפֶן׃",
            english = "Blessed are You, Lord our God, King of the universe, Who creates the fruit of the vine.",
            category = BrachaCategory.DRINK
        ),
        BrachaSection(
            title = "העץ",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא פְּרִי הָעֵץ׃",
            english = "Blessed are You, Lord our God, King of the universe, Who creates the fruit of the tree.",
            category = BrachaCategory.FOOD
        ),
        BrachaSection(
            title = "האדמה",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא פְּרִי הָאֲדָמָה׃",
            english = "Blessed are You, Lord our God, King of the universe, Who creates the fruit of the earth.",
            category = BrachaCategory.FOOD
        ),
        BrachaSection(
            title = "שהכל",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם שֶׁהַכֹּל נִהְיָה בִּדְבָרוֹ׃",
            english = "Blessed are You, Lord our God, King of the universe, by Whose word all things came to be.",
            category = BrachaCategory.OTHER
        ),
        BrachaSection(
            title = "בורא נפשות",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא נְפָשׁוֹת רַבּוֹת וְחֶסְרוֹנָן עַל כָּל מַה שֶּׁבָּרָאתָ לְהַחֲיוֹת בָּהֶם נֶפֶשׁ כָּל חָי בָּרוּךְ חֵי הָעוֹלָמִים׃",
            english = "Blessed are You, Lord our God, King of the universe, Who creates numerous living things with their deficiencies, for all that You have created with which to sustain the life of every being. Blessed is He who is the Life of the worlds.",
            category = BrachaCategory.AFTER_FOOD
        ),
        BrachaSection(
            title = "בשמים",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא מִינֵי בְשָׂמִים׃",
            english = "Blessed are You, Lord our God, King of the universe, Who creates various kinds of spices.",
            category = BrachaCategory.FRAGRANCE
        ),
        BrachaSection(
            title = "הברק",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם עוֹשֶׂה מַעֲשֵׂה בְרֵאשִׁית׃",
            english = "Blessed are You, Lord our God, King of the universe, Who does the works of creation.",
            category = BrachaCategory.NATURAL_PHENOMENA
        ),
        BrachaSection(
            title = "הרעם",
            hebrew = "בָּרוּךְ אַתָּה ה׳ אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם שֶׁכֹּחוֹ וּגְבוּרָתוֹ מָלֵא עוֹלָם׃",
            english = "Blessed are You, Lord our God, King of the universe, Whose power and might fill the world.",
            category = BrachaCategory.NATURAL_PHENOMENA
        )
    )
} 