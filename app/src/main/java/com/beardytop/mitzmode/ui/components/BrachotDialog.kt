package com.beardytop.mitzmode.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.withStyle

data class Bracha(
    val name: String,
    val hebrew: AnnotatedString,
    val english: AnnotatedString,
    val description: String
)

@Composable
fun BrachotDialog(
    onDismiss: () -> Unit
) {
    var fontScale by remember { mutableStateOf(1f) }
    
    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        fontScale = (fontScale * zoomChange).coerceIn(0.5f, 3f)
    }

    val scaledFontSize = (MaterialTheme.typography.bodyLarge.fontSize.value * fontScale).sp

    val brachot = listOf(
        // Body Functions Blessing
        Bracha(
            name = "Asher Yatzar",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם אֲשֶׁר יָצַר אֶת הָאָדָם בְּחָכְמָה וּבָרָא בוֹ נְקָבִים נְקָבִים חֲלוּלִים חֲלוּלִים גָּלוּי וְיָדוּעַ לִפְנֵי כִסֵּא כְבוֹדֶךָ שֶׁאִם יִפָּתֵחַ אֶחָד מֵהֶם אוֹ יִסָּתֵם אֶחָד מֵהֶם אִי אֶפְשַׁר לְהִתְקַיֵּם וְלַעֲמוֹד לְפָנֶיךָ אֲפִילוּ שָׁעָה אֶחָת בָּרוּךְ אַתָּה ה' רוֹפֵא כָל בָּשָׂר וּמַפְלִיא לַעֲשׂוֹת") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, who formed man with wisdom and created within him many openings and many hollow spaces. It is obvious and known before Your Throne of Glory that if even one of them would be opened, or if even one of them would be sealed, it would be impossible to survive and to stand before You even for one hour. Blessed are You, Lord, who heals all flesh and acts wondrously.") },
            description = "After using the bathroom"
        ),
        
        // Before Food Blessings
        Bracha(
            name = "Hamotzi",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם הַמּוֹצִיא לֶחֶם מִן הָאָרֶץ") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, who brings forth bread from the earth") },
            description = "For bread"
        ),
        Bracha(
            name = "Mezonot",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא מִינֵי מְזוֹנוֹת") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, who creates various kinds of sustenance") },
            description = "For grain products (not bread)"
        ),
        Bracha(
            name = "Hagafen",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא פְּרִי הַגָּפֶן") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, who creates the fruit of the vine") },
            description = "For wine and grape juice"
        ),
        Bracha(
            name = "Ha'etz",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא פְּרִי הָעֵץ") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, who creates the fruit of the tree") },
            description = "For tree fruits"
        ),
        Bracha(
            name = "Ha'adama",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא פְּרִי הָאֲדָמָה") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, who creates the fruit of the earth") },
            description = "For vegetables and ground fruits"
        ),
        Bracha(
            name = "Shehakol",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם שֶׁהַכֹּל נִהְיָה בִּדְבָרוֹ") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, by whose word all things came to be") },
            description = "For everything else (meat, dairy, drinks)"
        ),
        
        // After Food Blessing
        Bracha(
            name = "Borei Nefashot",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם בּוֹרֵא נְפָשׁוֹת רַבּוֹת וְחֶסְרוֹנָן עַל כָּל מַה שֶּׁבָּרָאתָ לְהַחֲיוֹת בָּהֶם נֶפֶשׁ כָּל חָי בָּרוּךְ חֵי הָעוֹלָמִים") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the universe, Who creates numerous living things with their deficiencies, for all that You have created with which to sustain the life of every being. Blessed is He who is the Life of the worlds") },
            description = "After-blessing for foods/drinks that don't require Birkat Hamazon or the Three-faceted Blessing"
        ),
        
        // Natural Phenomena
        Bracha(
            name = "On Thunder",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם שֶׁכֹּחוֹ וּגְבוּרָתוֹ מָלֵא עוֹלָם") },
            english = buildAnnotatedString { append("Blessed are You G-od our G-d, King of the Universe whose power and might fill the world") },
            description = "When hearing thunder"
        ),
        Bracha(
            name = "On Lightning",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם עוֹשֶׂה מַעֲשֵׂה בְרֵאשִׁית") },
            english = buildAnnotatedString { append("Blessed are You G-od our G-d, King of the Universe who does the work of creation") },
            description = "When seeing lightning"
        ),
        Bracha(
            name = "On the Ocean",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹהֵינוּ מֶלֶךְ הָעוֹלָם שֶׁעָשָׂה אֶת הַיָּם הַגָּדוֹל") },
            english = buildAnnotatedString { append("Blessed are You G-od our G-d, King of the Universe who made the great sea") },
            description = "When seeing the ocean (after not having seen it for 30 days)"
        ),
        Bracha(
            name = "Birkat Ha'Ilanot",
            hebrew = buildAnnotatedString { append("בָּרוּךְ אַתָּה ה' אֱלֹקֵינוּ מֶלֶךְ הָעוֹלָם שֶׁלֹּא חִסַּר בְּעוֹלָמוֹ כְּלוּם וּבָרָא בוֹ בְּרִיּוֹת טוֹבוֹת וְאִילָנוֹת טוֹבוֹת לֵהָנוֹת בָּהֶם בְּנֵי אָדָם") },
            english = buildAnnotatedString { append("Blessed are You, Lord our God, King of the Universe, who has made nothing lacking in His universe, and created within it good creatures and good trees to give pleasure to mankind") },
            description = "Blessing on blossoming fruit trees during the month of Nissan"
        ),
        Bracha(
            name = "Three-faceted Blessing",
            hebrew = buildAnnotatedString {
                append("בָּרוּךְ אַתָּה ה' אֱלֹקֵינוּ מֶלֶךְ הָעוֹלָם\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After food prepared from the five grains:\n")
                }
                append("עַל הַמִּחְיָה וְעַל הַכַּלְכָּלָה\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After wine or grape juice:\n")
                }
                append("וְעַל הַגֶּפֶן וְעַל פְּרִי הַגֶּפֶן\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After grapes, figs, pomegranates, olives, or dates:\n")
                }
                append("וְעַל הָעֵץ וְעַל פְּרִי הָעֵץ\n\n")
                
                append("וְעַל תְּנוּבַת הַשָּׂדֶה וְעַל אֶרֶץ חֶמְדָּה טוֹבָה וּרְחָבָה שֶׁרָצִיתָ וְהִנְחַלְתָּ לַאֲבוֹתֵינוּ לֶאֱכוֹל מִפִּרְיָהּ וְלִשְׂבּוֹעַ מִטּוּבָהּ. רַחֵם ה' אֱלֹקֵינוּ עַל יִשְׂרָאֵל עַמֶּךָ וְעַל יְרוּשָׁלַיִם עִירֶךָ וְעַל צִיּוֹן מִשְׁכַּן כְּבוֹדֶךָ וְעַל מִזְבְּחֶךָ וְעַל הֵיכָלֶךָ וּבְנֵה יְרוּשָׁלַיִם עִיר הַקֹּדֶשׁ בִּמְהֵרָה בְיָמֵינוּ וְהַעֲלֵנוּ לְתוֹכָהּ וְשַׂמְּחֵנוּ בְּבִנְיָנָהּ וְנֹאכַל מִפִּרְיָהּ וְנִשְׂבַּע מִטּוּבָהּ וּנְבָרֶכְךָ עָלֶיהָ בִּקְדֻשָּׁה וּבְטָהֳרָה\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("On Rosh Chodesh:\n")
                }
                append("וְזָכְרֵנוּ לְטוֹבָה בְּיוֹם רֹאשׁ הַחֹדֶשׁ הַזֶה\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("On Chol Hamoed Pesach:\n")
                }
                append("וְזָכְרֵנוּ לְטוֹבָה בְּיוֹם חַג הַמַצוֹת הַזֶה\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("On Chol Hamoed Sukkot:\n")
                }
                append("וְזָכְרֵנוּ לְטוֹבָה בְּיוֹם חַג הַסֻכּוֹת הַזֶה\n\n")
                
                append("כִּי אַתָּה ה' טוֹב וּמֵטִיב לַכֹּל וְנוֹדֶה לְךָ עַל הָאָרֶץ וְעַל\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After food prepared from the five grains:\n")
                }
                append("הַמִּחְיָה וְעַל הַכַּלְכָּלָה\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After wine or grape juice:\n")
                }
                append("(וְעַל) פְּרִי הַגֶּפֶן\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After grapes, figs, pomegranates, olives, or dates:\n")
                }
                append("(וְעַל) הַפֵּרוֹת\n\n")
                
                append("בָּרוּךְ אַתָּה ה' עַל הָאָרֶץ וְעַל\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After food prepared from the five grains:\n")
                }
                append("הַמִּחְיָה וְעַל הַכַּלְכָּלָה\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After wine or grape juice:\n")
                }
                append("(וְעַל) פְּרִי הַגֶּפֶן\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After grapes, figs, pomegranates, olives, or dates:\n")
                }
                append("(וְעַל) הַפֵּרוֹת")
            },
            english = buildAnnotatedString {
                append("\n\nEnglish:\n\n")
                append("Blessed are You, Lord our God, King of the universe, for\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After food prepared from the five grains:\n")
                }
                append("the sustenance and the nourishment,\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After wine or grape juice:\n")
                }
                append("(and for) the vine and the fruit of the vine,\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After grapes, figs, pomegranates, olives, or dates:\n")
                }
                append("(and for) the tree and the fruit of the tree,\n\n")
                
                append("for the produce of the field, and for the precious, good, and spacious land which You have graciously given as a heritage to our ancestors, to eat of its fruit and to be satiated with its goodness. Have mercy, Lord our God, on Israel Your people, on Jerusalem Your city, on Zion the abode of Your glory, on Your altar and on Your Temple. Rebuild Jerusalem, the holy city, speedily in our days, and bring us up to it and make us rejoice in it, and we will bless You in holiness and purity.\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("On Rosh Chodesh:\n")
                }
                append("Remember us for good on this day of Rosh Chodesh.\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("On Chol Hamoed Pesach:\n")
                }
                append("Remember us for good on this day of the festival of Matzot.\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("On Chol Hamoed Sukkot:\n")
                }
                append("Remember us for good on this day of festival of Sukkot.\n\n")
                
                append("For You, Lord, are good and do good to all, and we offer thanks to You for the land and for\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After food prepared from the five grains:\n")
                }
                append("the sustenance.\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After wine or grape juice:\n")
                }
                append("(and for) the fruit of the vine.\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After grapes, figs, pomegranates, olives, or dates:\n")
                }
                append("(and for) the fruits.\n\n")
                
                append("Blessed are You, Lord, for the land and for\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After food prepared from the five grains:\n")
                }
                append("the sustenance.\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After wine or grape juice:\n")
                }
                append("(and for) the fruit of the vine.\n\n")
                
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("After grapes, figs, pomegranates, olives, or dates:\n")
                }
                append("(and for) the fruits.")
            },
            description = "After eating grain products (not bread), wine/grape juice, or fruits of the 7 species"
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Brachot") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .transformable(transformableState)
            ) {
                items(brachot) { bracha ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = bracha.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = bracha.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = bracha.hebrew,
                            fontSize = scaledFontSize,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                        Text(
                            text = bracha.english,
                            fontSize = scaledFontSize,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
} 