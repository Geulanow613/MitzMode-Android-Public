package com.beardytop.mitzmode.data

data class Mitzvah(
    val id: String,
    val text: String,
    val links: List<MitzvahLink> = emptyList()
)

data class MitzvahLink(
    val displayText: String,
    val url: String
) 