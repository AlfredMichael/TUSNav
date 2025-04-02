package ie.tus.tusnav.ui.topappbars

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import ie.tus.tusnav.ui.theme.publicSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterTA(titleText: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                titleText,
                maxLines = 1,
                fontFamily = publicSans,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis
            )
        },
    )
}
