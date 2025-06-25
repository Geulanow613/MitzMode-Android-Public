package com.beardytop.mitzmode.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import com.beardytop.mitzmode.viewmodel.DailyMitzvotViewModel
import com.beardytop.mitzmode.util.HebrewDayTimer
import com.beardytop.mitzmode.util.Constants
import com.beardytop.mitzmode.util.Constants.MALE_REQUIRED_ITEMS
import com.beardytop.mitzmode.util.Constants.FEMALE_REQUIRED_ITEMS
import com.beardytop.mitzmode.utils.openUrl
import kotlinx.coroutines.delay
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import javax.inject.Inject
import androidx.compose.ui.geometry.Offset

data class Link(
    val displayText: String,
    val url: String
)

@Composable
fun DailyMitzvotChecklist(
    onDismiss: () -> Unit,
    viewModel: DailyMitzvotViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isMale by remember { mutableStateOf(true) }
    var showHelp by remember { mutableStateOf(false) }
    var textSize by remember { mutableStateOf(14f) }  // Default text size
    val checklistStates by viewModel.checklistStates.collectAsState()
    
    // Check completion based on gender
    LaunchedEffect(checklistStates, isMale) {
        val requiredItems = if (isMale) MALE_REQUIRED_ITEMS else FEMALE_REQUIRED_ITEMS
        val allCompleted = requiredItems.all { checklistStates[it] == true }
        if (allCompleted && !viewModel.hasShownTzaddikToday()) {
            viewModel.showTzaddikAlert()
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(0.95f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header with text size controls, help and close buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Text size controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { textSize = (textSize - 1).coerceAtLeast(12f) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("a", style = MaterialTheme.typography.labelMedium)
                        }
                        IconButton(
                            onClick = { textSize = (textSize + 1).coerceAtMost(20f) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("A", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Text(
                        text = "Daily Mitzvot Guide",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = (textSize + 4).sp  // Title slightly larger than body text
                        ),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row {
                        IconButton(onClick = { showHelp = true }) {
                            Icon(Icons.Default.Help, "Help")
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close")
                        }
                    }
                }

                Text(
                    text = "This guide is for beginners to understand the basic requirements of " +
                          "Torah-observant Jewish life. Take it one step at a time and always " +
                          "consult with a rabbi for proper guidance.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = textSize.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                // Gender selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { isMale = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMale) MaterialTheme.colorScheme.primary 
                                          else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Male")
                    }
                    Button(
                        onClick = { isMale = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isMale) MaterialTheme.colorScheme.primary 
                                          else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Female")
                    }
                }

                // Daily requirements checklist
                if (isMale) {
                    MaleDailyChecklist(viewModel, textSize)
                } else {
                    FemaleDailyChecklist(viewModel, textSize)
                }

                // Hebrew calendar section
                HebrewCalendarSection(textSize)
            }
        }
    }
    
    // Show help dialog when needed
    if (showHelp) {
        BeginnerInfoDialog(onDismiss = { showHelp = false })
    }
    
    // Show Tzaddik Alert if needed
    if (viewModel.showTzaddikAlert.collectAsState().value) {
        TzaddikAlert(onDismiss = viewModel::dismissTzaddikAlert)
    }

    if (viewModel.showBlessedAnimation.collectAsState().value) {
        Dialog(
            onDismissRequest = viewModel::dismissBlessedAnimation,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            BlessedAnimation(
                onFinished = viewModel::dismissBlessedAnimation
            )
        }
    }
}

@Composable
private fun ChecklistSection(
    title: String,
    textSize: Float,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = (textSize + 2).sp
            ),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content()
    }
}

@Composable
private fun ChecklistItemWithInfo(
    text: String,
    explanation: String,
    viewModel: DailyMitzvotViewModel,
    textSize: Float,
    link: Link? = null
) {
    var showInfo by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = viewModel.checklistStates.collectAsState().value[text] == true,
            onCheckedChange = { viewModel.updateChecklistItem(text, it) }
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = textSize.sp
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        IconButton(onClick = { showInfo = true }) {
            Icon(Icons.Default.Info, contentDescription = "Info")
        }
    }

    if (showInfo) {
        Dialog(
            onDismissRequest = { showInfo = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = if (link != null) 8.dp else 0.dp)
                    )
                    
                    if (link != null) {
                        TextButton(
                            onClick = { context.openUrl(link.url) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = link.displayText,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    TextButton(
                        onClick = { showInfo = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("CLOSE")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplanationDialog(
    explanation: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Explanation") },
        text = {
            Box(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

@Composable
private fun MaleDailyChecklist(viewModel: DailyMitzvotViewModel, textSize: Float) {
    val context = LocalContext.current
    var showTefillinInfo by remember { mutableStateOf(false) }
    var showTzitzitInfo by remember { mutableStateOf(false) }

    // Add Clear button at the top
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { viewModel.clearChecklist() },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Clear All")
        }
    }

    ChecklistSection("Important Daily Mitzvot", textSize) {
        ChecklistItemWithInfo(
            text = "Ritual hand washing",
            explanation = "Upon waking, we perform ritual hand washing (netilat yadayim) to remove spiritual impurity from sleep:\n\n" +
                         "• Fill a cup with water\n" +
                         "• Pour first on right hand\n" +
                         "• Then on left hand\n" +
                         "• Continue alternating between hands until each hand is washed 3 times\n" +
                         "• Say the blessing 'Al Netilat Yadayim'\n\n" +
                         "Similar hand washing is required before prayer times throughout the day, " +
                         "though without a blessing. This washing helps prepare us to connect with G-d " +
                         "in a state of ritual purity.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Wear a Kippah (head covering)",
            explanation = Constants.HEAD_COVERING_EXPLANATIONS["male"] ?: "",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Put on Tefillin during morning prayers (except Shabbat/Festivals)",
            explanation = "Tefillin are sacred black leather boxes containing Torah verses, worn on the " +
                         "arm and head during morning prayers. They help us to direct our thoughts " +
                         "and actions to G-d's service, and connect us to Him. They can be worn later if not possible during morning prayers (but not at night).\n\n" +
                         "While tefillin are a significant investment, they are one of the most important items " +
                         "a Jewish man can own. It's crucial to purchase only certified kosher tefillin from a " +
                         "reliable source, as there are many non-kosher ones on the market. Consult your local " +
                         "Chabad/Orthodox rabbi for guidance on purchasing the right set and learning how to put them on properly.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Wear Tzitzit (recommended for divine protection)",
            explanation = "Tzitzit are special fringes worn on a four-cornered garment. While technically " +
                         "only required when wearing such a garment, it's highly recommended to wear a " +
                         "tallit katan all day and tallit gadol during morning prayers for spiritual protection.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Keep Kosher",
            explanation = "Keeping kosher involves following Jewish dietary laws:\n\n" +
                         "• Eating only kosher animals (e.g., no pork or shellfish)\n" +
                         "• Using only kosher-certified products\n" +
                         "• Separating meat and dairy completely\n" +
                         "• Using separate dishes, utensils, and appliances for meat and dairy\n" +
                         "• Waiting the required time between eating meat and dairy\n" +
                         "• Using only kosher wine and grape products\n" +
                         "• Special rules for Passover\n\n" +
                         "These laws elevate eating into a spiritual act and help maintain Jewish identity. " +
                         "Please consult with a rabbi for guidance on implementing kosher practices in your home.",
            viewModel = viewModel,
            textSize = textSize
        )

        ChecklistItemWithInfo(
            text = "Have Mezuzot on your doorposts",
            explanation = "A mezuzah is a sacred scroll containing Torah verses that we affix to our doorposts:\n\n" +
                         "• Every Jewish home requires kosher mezuzot on all appropriate doorways\n" +
                         "• The mezuzah provides spiritual protection and blessings for the home and its inhabitants\n" +
                         "• It's crucial to use only certified kosher scrolls - many non-kosher ones exist in the market\n" +
                         "• Consult your local Chabad/Orthodox rabbi about which doorways need mezuzot and where to obtain kosher ones",
            viewModel = viewModel,
            textSize = textSize,
            link = Link(
                displayText = "Learn more about mezuzot",
                url = "https://www.chabad.org/library/article_cdo/aid/278476/jewish/Mezuzah.htm"
            )
        )

        ChecklistItemWithInfo(
            text = "Immerse food vessels in mikveh",
            explanation = "Elevate your kitchen utensils through the beautiful mitzvah of tevilat keilim (vessel immersion):\n\n" +
                         "• New food vessels bought from non-Jewish sources need immersion in a mikveh or suitable body of water\n" +
                         "• This spiritually prepares them for use in kosher food preparation\n" +
                         "• Metal and glass items require immersion\n" +
                         "• Items like plastic, wood, or disposables don't need immersion\n\n" +
                         "This special mitzvah transforms ordinary eating into a holy act. " +
                         "Check with your local rabbi about specific items and the nearest mikveh location.",
            viewModel = viewModel,
            textSize = textSize
        )

        ChecklistItemWithInfo(
            text = "100 Daily Blessings",
            explanation = "The goal is to say 100 blessings each day. On weekdays, this happens naturally through daily prayers and food blessings. On Shabbat and Festivals when prayers are shorter, make up the count with blessings on snacks and fragrances. On Yom Kippur, when we don't eat, there are other ways to make up the count.",
            viewModel = viewModel,
            textSize = textSize,
            link = Link(
                displayText = "Click here for more information",
                url = "https://aish.com/43-100-blessings-each-day/"
            )
        )
    }

    if (showTefillinInfo) {
        TefillinInfoDialog(onDismiss = { showTefillinInfo = false })
    }

    if (showTzitzitInfo) {
        TzitzitInfoDialog(onDismiss = { showTzitzitInfo = false })
    }

    // Add explanation section
    Text(
        text = "Note: Tefillin and Tzitzit are special garments worn by Jewish men. " +
              "A tallit katan is worn under clothes all day, while a tallit gadol is typically worn during morning prayers. " +
              "Consult with a rabbi for proper guidance on how to wear these items.",
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = textSize.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
    )
    
    ChecklistSection("Morning Prayer (Shacharit)", textSize) {
        ChecklistItemWithInfo(
            text = "Morning Blessings (Birchot HaShachar)",
            explanation = "The morning blessings (Birchot HaShachar) are recited at the start of the day. " +
                         "It is recommended to say Korbanot, and at minimum, many rabbis require saying the verses of the Parsha HaTamid (the continual offering sacrified daily in the Temple). " +
                         "The morning blessings express gratitude for basic functions and aspects of life.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Torah Blessings + minimal Torah study",
            explanation = "Before studying Torah, we recite blessings acknowledging G-d as the giver of Torah. " +
                         "We then fulfill the daily obligation to study Torah, by reading the passages that follow in the siddur.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Weekly Parsha reading (twice in Hebrew, once in Targum)",
            explanation = "There is a mitzvah to read the weekly Torah portion (Parsha) twice in Hebrew and once in Targum (Aramaic translation) each week, completing it before Shabbat. This practice is called 'Shnayim Mikra v'Echad Targum.' " +
                         "The goal is to become familiar with the Torah portion that will be read in synagogue on Shabbat.\n\n" +
                         "Some rabbis say that if you have no ability to read Hebrew, you can read the parsha twice and the translation of the targum once in the language you speak (instead of Hebrew/Aramaic - but this is not a universally accepted leniency). " +
                         "Many people spread this out throughout the week, reading a small portion each day.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Minimum Pesukei D'Zimra",
            explanation = "These are verses of praise from Psalms and other sources that prepare us for prayer. " +
                         "The minimum is Baruch She'amar, Ashrei, and Yishtabach.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Morning Shema with its blessings",
            explanation = "The Shema is a central declaration of Jewish faith, recited with blessings before and after. " +
                         "It must be said within the first quarter of the day.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Shemoneh Esrei/Tachanun",
            explanation = "Also called Amidah, this is the central prayer consisting of 19 blessings (18 originally). " +
                         "It's recited standing, facing Jerusalem, three times daily.\n\n" +
                         "On most weekdays, Tachanun (prayers of repentance) is recited immediately after. " +
                         "However, it's omitted on festive days and certain other occasions. " +
                         "Follow your siddur's instructions or the congregation if you're at synagogue.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    ChecklistSection("Additional Prayers", textSize) {
        ChecklistItemWithInfo(
            text = "Musaf (only on Rosh Chodesh, Festivals, and Shabbat)",
            explanation = "The additional prayer service recited after Shacharit on special days, " +
                         "commemorating the additional Temple offerings that were brought on these occasions." + " (Don't use your phone on Shabbat or festivals though!)",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    ChecklistSection("Afternoon Prayer", textSize) {
        ChecklistItemWithInfo(
            text = "Mincha - Shemoneh Esrei/Tachanun",
            explanation = "The afternoon prayer service, centered around the Shemoneh Esrei. " +
                         "It should be said after midday and before sunset. This prayer helps us " +
                         "pause during our busy day to reconnect with G-d.\n\n" +
                         "Like in the morning service, Tachanun is recited after Shemoneh Esrei on most weekdays, " +
                         "but omitted on festive days and certain occasions. " +
                         "Follow your siddur's instructions or the congregation if you're at synagogue.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    ChecklistSection("Evening Requirements", textSize) {
        ChecklistItemWithInfo(
            text = "Evening Shema with its blessings",
            explanation = "The evening Shema must be recited after nightfall. Like the morning Shema, " +
                         "it's surrounded by appropriate blessings. This mitzvah can be fulfilled " +
                         "any time during the night.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Maariv Shemoneh Esrei",
            explanation = "The evening Shemoneh Esrei prayer. While technically optional according to " +
                         "some opinions, it has become obligatory through universal Jewish custom.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Torah Study",
            explanation = "It's a mitzvah to learn at least a little Torah each day and each night. " +
                         "While the more Torah study the better, even learning just a few laws or " +
                         "something inspiring fulfills this obligation. Try to set aside some time " +
                         "before bed to connect with G-d's wisdom through Torah study.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Bedtime Shema (first paragraph - though recommended to say entire Shema for spiritual protection)",
            explanation = "Reciting Shema before sleep provides spiritual protection through the night. " +
                         "While only the first paragraph is strictly required, it's recommended to " +
                         "say the entire Shema and associated prayers for maximum protection.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Hamapil blessing (according to many opinions)",
            explanation = "A beautiful blessing thanking G-d for the gift of sleep and asking for " +
                         "protection through the night. While some consider it optional, many authorities " +
                         "recommend saying it nightly. Check your siddur for the text.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    ChecklistSection("Other Daily Requirements", textSize) {
        ChecklistItemWithInfo(
            text = "Say 100 brachot (blessings) today",
            explanation = "The goal is to say 100 blessings each day. On weekdays, this happens naturally through daily prayers and food blessings. On Shabbat and Festivals when prayers are shorter, or on Yom Kippur when we don't eat, make up the count with blessings on snacks and fragrances.",
            viewModel = viewModel,
            textSize = textSize,
            link = Link(
                displayText = "Click here for more information",
                url = "https://aish.com/43-100-blessings-each-day/"
            )
        )

        ChecklistItemWithInfo(
            text = "Blessings before food",
            explanation = "We acknowledge G-d as the source of our sustenance by reciting specific " +
                         "blessings before eating, with different blessings for different types of food.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Blessings after food",
            explanation = "After eating, we thank G-d for the food. After bread, we recite Birkat " +
                         "Hamazon (Grace After Meals). Other foods require shorter blessings of thanks.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Asher Yatzar after using bathroom",
            explanation = "A blessing thanking G-d for our body's proper functioning. It reminds us " +
                         "that even the most basic bodily functions are miraculous and worthy of gratitude. " + "Check your siddur for the proper text.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    // Add recommended resources section
    ChecklistSection("Recommended Resources", textSize) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Note: To properly fulfill these mitzvot, you'll need a good siddur (prayer book). " +
                      "There are several options available:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = textSize.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "• Many free siddur apps are available on your phone's app store\n" +
                      "• ArtScroll offers nice printed siddurim in various formats",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = textSize.sp
                ),
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            
            TextButton(
                onClick = {
                    context.openUrl("https://www.artscroll.com/Categories/PBK.html")
                }
            ) {
                Text(
                    "Browse printed siddurim options",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = textSize.sp
                    )
                )
            }
        }
    }

    ChecklistSection("Shabbat and Festivals", textSize) {
        ChecklistItemWithInfo(
            text = "Prepare for and observe Shabbat and Festivals",
            explanation = "Shabbat and Jewish Festivals are sacred times that elevate us spiritually:\n\n" +
                         "• Light candles before sunset to welcome these holy days\n" +
                         "• Prepare food and our home in advance\n" +
                         "• Refrain from work activities (melacha) such as using electronics, driving, handling money etc.\n" +
                         "• Enjoy festive meals with family and community\n" +
                         "• Focus on prayer, Torah study, and spiritual growth\n\n" +
                         "Proper observance of these days can bring our souls unique feelings of holiness " +
                         "and closeness with G-d that are unattainable during regular days. These elevated " +
                         "spiritual states can be experienced through proper observance of the laws.",
            viewModel = viewModel,
            textSize = textSize
        )
    }
}

@Composable
private fun FemaleDailyChecklist(viewModel: DailyMitzvotViewModel, textSize: Float) {
    val context = LocalContext.current
    
    // Add Clear button at the top
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { viewModel.clearChecklist() },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Clear All")
        }
    }

    ChecklistSection("Daily Prayer", textSize) {
        ChecklistItemWithInfo(
            text = "Ritual hand washing",
            explanation = "Upon waking, we perform ritual hand washing (netilat yadayim) to remove spiritual impurity from sleep:\n\n" +
                         "• Fill a cup with water\n" +
                         "• Pour first on right hand\n" +
                         "• Then on left hand\n" +
                         "• Continue alternating between hands until each hand is washed 3 times\n" +
                         "• Say the blessing 'Al Netilat Yadayim'\n\n" +
                         "Similar hand washing is required before prayer times throughout the day, " +
                         "though without a blessing. This washing helps prepare us to connect with G-d " +
                         "in a state of ritual purity.",
            viewModel = viewModel,
            textSize = textSize
        )

        ChecklistItemWithInfo(
            text = "At least one prayer daily (typically morning)",
            explanation = "While men are obligated in three daily prayers, women are required to pray " +
                         "at least once per day. Many women choose to pray Shacharit (morning prayers). " +
                         "Some are lenient and offer a simple prayer comprised of praise, thanks, and a request.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    ChecklistSection("Important Lifestyle Mitzvot", textSize) {
        ChecklistItemWithInfo(
            text = "Have Mezuzot on your doorposts",
            explanation = "A mezuzah is a sacred scroll containing Torah verses that we affix to our doorposts:\n\n" +
                         "• Every Jewish home requires kosher mezuzot on all appropriate doorways\n" +
                         "• The mezuzah provides spiritual protection and blessings for the home and its inhabitants\n" +
                         "• It's crucial to use only certified kosher scrolls - many non-kosher ones exist in the market\n" +
                         "• Consult your local Chabad/Orthodox rabbi about which doorways need mezuzot and where to obtain kosher ones",
            viewModel = viewModel,
            textSize = textSize,
            link = Link(
                displayText = "Learn more about mezuzot",
                url = "https://www.chabad.org/library/article_cdo/aid/278476/jewish/Mezuzah.htm"
            )
        )

        ChecklistItemWithInfo(
            text = "Cover hair in public (if married)",
            explanation = Constants.HEAD_COVERING_EXPLANATIONS["female"] ?: "",
            viewModel = viewModel,
            textSize = textSize
        )

        ChecklistItemWithInfo(
            text = "Modesty (Tznius)",
            explanation = "Modesty in Judaism encompasses both dress and behavior:\n\n" +
                         "• Wearing clothing that covers the body appropriately\n" +
                         "• Speaking and acting in a way that doesn't draw unwanted attention\n" +
                         "• Conducting ourselves in a way that reflects Jewish values\n\n" +
                         "The goal is to emphasize our inner spiritual beauty and dignity as Jewish women. " +
                         "Different communities have varying standards - consult with a rabbi for guidance " +
                         "on what's appropriate for you.",
            viewModel = viewModel,
            textSize = textSize
        )

        ChecklistItemWithInfo(
            text = "Keep Kosher",
            explanation = "Keeping kosher involves following Jewish dietary laws:\n\n" +
                         "• Eating only kosher animals (e.g., no pork or shellfish)\n" +
                         "• Using only kosher-certified products\n" +
                         "• Separating meat and dairy completely\n" +
                         "• Using separate dishes, utensils, and appliances for meat and dairy\n" +
                         "• Waiting the required time between eating meat and dairy\n" +
                         "• Using only kosher wine and grape products\n" +
                         "• Special rules for Passover\n\n" +
                         "These laws elevate eating into a spiritual act and help maintain Jewish identity. " +
                         "Please consult with a rabbi for guidance on implementing kosher practices in your home.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Family Purity Laws (if married)",
            explanation = "Family purity laws (Taharat HaMishpacha) are central to Jewish married life:\n\n" +
                         "• Observing the laws of niddah (separation during menstruation)\n" +
                         "• Immersing in a mikvah at the appropriate time\n" +
                         "• Understanding the calendar calculations\n\n" +
                         "These laws are considered one of the three primary mitzvot for Jewish women. " +
                         "They bring holiness and blessing to marriage. Please learn these laws from a " +
                         "qualified instructor before marriage.",
            viewModel = viewModel,
            textSize = textSize
        )

        ChecklistItemWithInfo(
            text = "Immerse food vessels in mikveh",
            explanation = "Elevate your kitchen utensils through the beautiful mitzvah of tevilat keilim (vessel immersion):\n\n" +
                         "• New food vessels bought from non-Jewish sources need immersion in a mikveh or suitable body of water\n" +
                         "• This spiritually prepares them for use in kosher food preparation\n" +
                         "• Metal and glass items require immersion\n" +
                         "• Items like plastic, wood, or disposables don't need immersion\n\n" +
                         "This special mitzvah transforms ordinary eating into a holy act. " +
                         "Check with your local rabbi about specific items and the nearest mikveh location.",
            viewModel = viewModel,
            textSize = textSize
        )

        ChecklistItemWithInfo(
            text = "Torah Study",
            explanation = "While women's Torah study obligations differ from men's, it is important and meritorious for women to learn about the mitzvot in which they are obligated, such as:\n\n" +
                         "• Laws of Shabbat and Festivals\n" +
                         "• Kosher dietary laws\n" +
                         "• Laws of separating challah from dough\n" +
                         "• Family purity laws (for married women)\n" +
                         "• Laws of modest dress and conduct\n" +
                         "• Laws of prayer and blessings\n\n" +
                         "Learning these laws helps women fulfill mitzvot properly. The study should be approached in a pleasant and inspiring manner.\n\n" +
                         "Many wonderful resources are available specifically for women's Torah study, and classes are often offered by local rebbetzins and women teachers.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    ChecklistSection("Daily Blessings", textSize) {
        ChecklistItemWithInfo(
            text = "Blessings before food",
            explanation = "Before eating or drinking, we acknowledge G-d as the source of all sustenance. " +
                         "Different foods have specific blessings, helping us maintain constant awareness " +
                         "of G-d's providence.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Blessings after food",
            explanation = "After eating, we express gratitude to G-d. After bread, we recite Birkat " +
                         "Hamazon (Grace After Meals). Other foods require shorter blessings, each " +
                         "thanking G-d for specific types of nourishment.",
            viewModel = viewModel,
            textSize = textSize
        )
        
        ChecklistItemWithInfo(
            text = "Asher Yatzar after using bathroom",
            explanation = "This blessing acknowledges the wonder of our body's functioning and thanks " +
                         "G-d for maintaining our health. It reminds us that even basic bodily " +
                         "functions are miraculous gifts worthy of gratitude. Check your siddur for the proper text.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    ChecklistSection("Shabbat and Festivals", textSize) {
        ChecklistItemWithInfo(
            text = "Prepare for and observe Shabbat and Festivals",
            explanation = "Shabbat and Jewish Festivals are sacred times that elevate us spiritually:\n\n" +
                         "• Light candles before sunset to welcome these holy days\n" +
                         "• Prepare food and our home in advance\n" +
                         "• Refrain from work activities (melacha) such as using electronics, driving, handling money etc.\n" +
                         "• Enjoy festive meals with family and community\n" +
                         "• Focus on prayer, Torah study, and spiritual growth\n\n" +
                         "Proper observance of these days can bring our souls unique feelings of holiness " +
                         "and closeness with G-d that are unattainable during regular days. These elevated " +
                         "spiritual states can be experienced through proper observance of the laws.",
            viewModel = viewModel,
            textSize = textSize
        )
    }

    // Add recommended resources section
    ChecklistSection("Recommended Resources", textSize) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Note: To properly fulfill these mitzvot, you'll need a good siddur (prayer book). " +
                      "There are several options available:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = textSize.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "• Many free siddur apps are available on your phone's app store\n" +
                      "• ArtScroll offers nice printed siddurim in various formats",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = textSize.sp
                ),
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            
            TextButton(
                onClick = {
                    context.openUrl("https://www.artscroll.com/Categories/PBK.html")
                }
            ) {
                Text(
                    "Browse printed siddurim options",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = textSize.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun HebrewCalendarSection(textSize: Float) {
    Text(
        text = "Special Mitzvot Notes",
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = (textSize + 2).sp
        ),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
    
    Text(
        text = "There are many additional mitzvot that apply at special times. Here are just a few examples:\n" +
              "• Monthly: Kiddush HaLevana, Rosh Chodesh\n" +
              "• Seasonal: Counting the Omer (between Pesach and Shavuot)\n" +
              "• Festivals: Eating Matzah, Hearing Shofar, Lulav/Etrog, Hannukah, Purim\n\n" +
              "To check which major upcoming Jewish events/holidays/mitzvot are happening currently, check out the \n",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = textSize.sp
        ),
        modifier = Modifier.padding(bottom = 0.dp)
    )

    val uriHandler = LocalUriHandler.current
    Text(
        text = "Chabad.org calendar",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = textSize.sp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .clickable { uriHandler.openUri("https://www.chabad.org/calendar/view/month.htm") }
            .padding(bottom = 0.dp)
    )

    Text(
        text = "\nThis list covers basic requirements in terms of prayer, blessings, and lifestyle " +
              "needed to fulfill daily Torah and rabbinic mitzvot. However, there are many other mitzvot in areas such as " +
              "business, proper speech, relationships, and more that are beyond the scope of this list. " +
              "Please consult a rabbi and reliable Jewish sources to learn about all the mitzvot relevant to your situation.",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = textSize.sp
        ),
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Add divider and electronics warning at the bottom
    Divider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    )
    
    Text(
        text = "Important Note About Electronics",
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = (textSize + 2).sp
        ),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
    
    Text(
        text = "Please refrain from using phones, computers, or any electronics during Shabbat " +
              "and Festivals, as these are holy days of complete rest.",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = textSize.sp
        ),
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun BeginnerInfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Understanding Daily Jewish Observance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "This checklist shows most of the basic daily requirements for Torah-observant Jews. " +
                          "If you're just starting your journey in Jewish observance:\n\n" +
                          "• Take it one mitzvah at a time\n" +
                          "• Don't feel overwhelmed - growth is gradual\n" +
                          "• Consult with a rabbi for proper guidance\n\n" +       
                          "This list is meant as an educational tool to understand what daily Jewish life entails, " +
                          "not as a source of pressure.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("CLOSE")
                }
            }
        }
    }
}

@Composable
private fun KosherInfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Understanding Kosher",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Keeping kosher involves following Jewish dietary laws:\n\n" +
                          "• Eating only kosher animals (e.g., no pork or shellfish)\n" +
                          "• Using only kosher-certified products\n" +
                          "• Separating meat and dairy\n" +
                          "• Using separate dishes for meat and dairy\n" +
                          "• Waiting required time between meat and dairy\n\n" +
                          "Please consult with a rabbi for proper guidance on implementing " +
                          "kosher practices in your home and life.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("CLOSE")
                }
            }
        }
    }
}

@Composable
private fun FamilyPurityInfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Family Purity Laws",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "The laws of family purity (Taharat HaMishpacha) are a fundamental " +
                          "part of Jewish married life. These laws involve:\n\n" +
                          "• Periods of separation between husband and wife\n" +
                          "• The process of counting clean days\n" +
                          "• Immersion in a mikvah (ritual bath)\n\n" +
                          "Due to the complexity and personal nature of these laws, " +
                          "it is essential to learn them properly from a qualified rabbi " +
                          "or rebbetzin before marriage.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("CLOSE")
                }
            }
        }
    }
}

@Composable
private fun TefillinInfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "About Tefillin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Important points about Tefillin:\n\n" +
                          "• Typically worn during morning prayers\n" +
                          "• Can be worn later in the day if unable to in morning\n" +
                          "• Not worn at night\n" +
                          "• Not worn on Shabbat or Festivals\n" +
                          "• Boys begin wearing at age 13 (Bar Mitzvah)\n\n" +
                          "Please consult with a rabbi to learn the proper way to put on and care for Tefillin.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("CLOSE")
                }
            }
        }
    }
}

@Composable
private fun TzitzitInfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "About Tzitzit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "About wearing Tzitzit:\n\n" +
                          "• A tallit katan can be worn under clothes all day\n" +
                          "• A tallit gadol is typically worn during morning prayers\n" +
                          "• While technically only required on four-cornered garments, " +
                          "it's recommended to wear a tallit to fulfill this important mitzvah\n" +
                          "• Wearing tzitzit provides spiritual protection\n\n" +
                          "Consult with a rabbi about proper wearing and checking of tzitzit.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("CLOSE")
                }
            }
        }
    }
}

@Composable
private fun TorahStudyItem(viewModel: DailyMitzvotViewModel, textSize: Float) {
    ChecklistItemWithInfo(
        text = "Torah Study",
        explanation = "While women's Torah study obligations differ from men's, it is important and meritorious for women to learn about the mitzvot in which they are obligated, such as:\n\n" +
                     "• Laws of Shabbat and Festivals\n" +
                     "• Kosher dietary laws\n" +
                     "• Laws of separating challah from dough\n" +
                     "• Family purity laws (for married women)\n" +
                     "• Laws of modest dress and conduct\n" +
                     "• Laws of prayer and blessings\n\n" +
                     "Learning these laws helps women fulfill mitzvot properly. The study should be approached in a pleasant and inspiring manner.\n\n" +
                     "Many wonderful resources are available specifically for women's Torah study, and classes are often offered by local rebbetzins and women teachers.",
        viewModel = viewModel,
        textSize = textSize
    )
}

// ... Additional composables for female checklist, Hebrew calendar, etc. 