package org.ligi.passandroid.functions

import android.content.Context
import android.net.Uri
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ligi.passandroid.Tracker
import org.ligi.passandroid.model.InputStreamWithSource
import java.io.BufferedInputStream
import java.net.URL

const val IPHONE_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X; en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53"

fun fromURI(context: Context, uri: Uri, tracker: Tracker): InputStreamWithSource? {
    tracker.trackEvent("protocol", "to_inputstream", uri.scheme, null)
    return when (uri.scheme) {
        "content" -> fromContent(context, uri)

        "http", "https" ->
            // TODO check if SPDY should be here
            return fromOKHttp(uri, tracker)

        "file" -> getDefaultInputStreamForUri(uri)
        else -> {
            tracker.trackException("unknown scheme in ImportAsyncTask" + uri.scheme, false)
            getDefaultInputStreamForUri(uri)
        }
    }
}

private fun fromOKHttp(uri: Uri, tracker: Tracker): InputStreamWithSource? {
    val client = OkHttpClient()
    val url = URL("$uri")
    val requestBuilder = Request.Builder().url(url)

    // fake to be an iPhone in some cases when the server decides to send no passbook
    // to android phones - but only do it then - we are proud to be Android ;-)
    val iPhoneFakeMap = mapOf(
            "air_canada" to "//m.aircanada.ca/ebp/",
            "air_canada2" to "//services.aircanada.com/ebp/",
            "air_canada3" to "//mci.aircanada.com/mci/bp/",
            "icelandair" to "//checkin.si.amadeus.net",
            "mbk" to "//mbk.thy.com/",
            "heathrow" to "//passbook.heathrow.com/",
            "eventbrite" to "//www.eventbrite.com/passes/order"
    )

    for ((key, value) in iPhoneFakeMap) {
        if ("$uri".contains(value)) {
            tracker.trackEvent("quirk_fix", "ua_fake", key, null)
            requestBuilder.header("User-Agent", IPHONE_USER_AGENT)
        }
    }

    val request = requestBuilder.build()

    val response = client.newCall(request).execute()

    val body = response.body()

    if (body != null) {
        return InputStreamWithSource("$uri", body.byteStream())
    }

    return null
}

private fun fromContent(ctx: Context, uri: Uri) = ctx.contentResolver.openInputStream(uri)?.let {
    InputStreamWithSource("$uri", it)
}

private fun getDefaultInputStreamForUri(uri: Uri) = InputStreamWithSource("$uri", BufferedInputStream(URL("$uri").openStream(), 4096))
