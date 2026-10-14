package com.khodehydro.faimess.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.R

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
)

val FrindleTypography = Typography(
    headlineLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 17.sp),
    bodyLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 18.sp),
    bodySmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Medium, fontSize = 10.sp, lineHeight = 15.sp),
    labelLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 13.sp),
    labelMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
    labelSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 9.sp),
)
