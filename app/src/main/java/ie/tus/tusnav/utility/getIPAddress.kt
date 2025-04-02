package ie.tus.tusnav.utility

import java.net.NetworkInterface
import java.util.Collections

fun getIPAddress(): String {
    try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in interfaces) {
            val addresses = Collections.list(networkInterface.inetAddresses)
            for (address in addresses) {
                if (!address.isLoopbackAddress) {
                    val hostAddress = address.hostAddress
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (isIPv4) {
                        return hostAddress
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return "Unknown"
}
