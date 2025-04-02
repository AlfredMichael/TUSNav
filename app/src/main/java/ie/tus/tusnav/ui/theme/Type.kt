package ie.tus.tusnav.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ie.tus.tusnav.R

//Custom TUS fonts - Public Sans


val publicSans = FontFamily(
    Font(R.font.publicsans_thin, FontWeight.Thin),
    Font(R.font.publicsans_regular, FontWeight.Normal),
    Font(R.font.publicsans_bold, FontWeight.Bold),
    Font(R.font.publicsans_medium, FontWeight.Medium),
    Font(R.font.publicsans_semibold, FontWeight.SemiBold),
    Font(R.font.publicsans_light, FontWeight.Light),
    Font(R.font.publicsans_extralight, FontWeight.ExtraLight),
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)