package com.beardytop.mitzmode.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.font.FontWeight
import com.beardytop.mitzmode.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val MontserratFont = GoogleFont("Montserrat")
val LoraFont = GoogleFont("Lora")

val Montserrat = FontFamily(
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.Bold)
)

val Lora = FontFamily(
    Font(googleFont = LoraFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = LoraFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = LoraFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = LoraFont, fontProvider = provider, weight = FontWeight.Bold)
) 