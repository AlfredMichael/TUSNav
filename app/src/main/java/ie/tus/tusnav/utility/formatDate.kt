package ie.tus.tusnav.utility

import java.util.Calendar
import java.util.concurrent.TimeUnit

fun formatDate(timestamp: Long): String {
    val now = Calendar.getInstance().timeInMillis
    val diff = now - timestamp
    val daysBetween = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        daysBetween < 1 -> "Today"
        daysBetween == 1L -> "Yesterday"
        daysBetween < 7 -> "$daysBetween days ago"
        daysBetween < 30 -> "${daysBetween / 7} weeks ago"
        daysBetween < 365 -> "${daysBetween / 30} months ago"
        else -> "${daysBetween / 365} years ago"
    }
}

