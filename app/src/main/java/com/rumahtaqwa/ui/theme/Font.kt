package com.rumahtaqwa.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.rumahtaqwa.R

val PlusJakartaSansFont = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_italic, FontWeight.Normal, FontStyle.Italic),

    Font(R.font.plus_jakarta_sans_semi_bold, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semi_bold_italic, FontWeight.Medium, FontStyle.Italic),

    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_bold_italic, FontWeight.Bold, FontStyle.Italic)
)