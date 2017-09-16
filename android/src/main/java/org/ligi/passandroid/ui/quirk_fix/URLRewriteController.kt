package org.ligi.passandroid.ui.quirk_fix

import android.net.Uri
import org.ligi.passandroid.Tracker
import java.net.URLEncoder

class URLRewriteController(private val tracker: Tracker) {

    fun getUrlByUri(uri: Uri): String? {

        if (uri.scheme != null && uri.authority != null && uri.authority == "import") {
            when (uri.scheme) {
                "pass2u" -> return uri.toString().substring("pass2u://import/".length)
                "passandroid" -> return uri.toString().substring("passandroid://import/".length)
            }
        }

        if (uri.host != null) {
            if (uri.host == "pass-cloud.appspot.com") {
                return uri.getQueryParameter("url")
            }

            if (uri.host.endsWith(".virginaustralia.com")) { // mobile. or checkin.
                return getVirginAustraliaURL(uri)
            }

            when (uri.host) {
                "m.aircanada.ca", "services.aircanada.com" -> return getAirCanada(uri)
                "mci.aircanada.com" -> return getAirCanada2(uri)
                "www.cathaypacific.com" -> return getCathay(uri)
                "mbp.swiss.com", "prod.wap.ncrwebhost.mobi" -> return getNrcWebHost(uri)
            }
        }

        return null
    }

    private fun getAirCanada(uri: Uri) = uri.toString() + "?appDetection=false"
    private fun getAirCanada2(uri: Uri) = uri.toString() + ".pkpass"

    private fun getVirginAustraliaURL(uri: Uri): String? {

        val passId: String?
        if (uri.toString().contains("CheckInApiIntegration")) {
            passId = uri.getQueryParameter("key")
            tracker.trackEvent("quirk_fix", "redirect_attempt", "virgin_australia2", null)
        } else {
            tracker.trackEvent("quirk_fix", "redirect_attempt", "virgin_australia1", null)
            passId = uri.getQueryParameter("c")
        }

        if (passId == null) {
            return null
        }

        tracker.trackEvent("quirk_fix", "redirect", "virgin_australia", null)

        return "https://mobile.virginaustralia.com/boarding/pass.pkpass?key=" + URLEncoder.encode(passId)
    }

    private fun getCathay(uri: Uri): String? {
        val passId = uri.getQueryParameter("v")

        tracker.trackEvent("quirk_fix", "redirect_attempt", "cathay", null)

        if (passId == null) {
            return null
        }

        tracker.trackEvent("quirk_fix", "redirect", "cathay", null)

        return "https://www.cathaypacific.com/icheckin2/PassbookServlet?v=" + URLEncoder.encode(passId)
    }

    private fun getNrcWebHost(uri: Uri): String? {
        var url = uri.toString()
        if (url.endsWith("/")) {
            url = url.substring(0, url.length - 1)
        }

        val split = url.split("/".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

        if (split.size < 6) {
            return null
        }

        return "http://prod.wap.ncrwebhost.mobi/mobiqa/wap/" + split[split.size - 2] + "/" + split[split.size - 1] + "/passbook"
    }
}
