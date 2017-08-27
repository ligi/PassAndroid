package org.ligi.passandroid.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import okhttp3.OkHttpClient
import okhttp3.Request
import org.ligi.kaxt.dismissIfShowing
import org.ligi.passandroid.functions.IPHONE_USER_AGENT
import org.ligi.passandroid.ui.quirk_fix.OpenIphoneWebView
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.regex.Pattern

class ExtractURLAsIphoneActivity : PassAndroidActivity() {

    private val progressDialog by lazy { ProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog.show()
        tracker.trackEvent("quirk_fix", "unpack_attempt", intent.data.host, null)

        DownloadExtractAndStartImportTask().execute()
    }

    private inner class DownloadExtractAndStartImportTask : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void): String? {

            val client = OkHttpClient()
            try {
                val requestBuilder = Request.Builder().url(URI(intent.data.toString()).toURL())
                requestBuilder.header("User-Agent", IPHONE_USER_AGENT)

                val body = client.newCall(requestBuilder.build()).execute().body()

                if (body != null) {
                    val bodyString = body.string()
                    body.close()

                    val url = extractURL(bodyString) ?: return null

                    if (!url.startsWith("http")) {
                        return intent.data.scheme + "://" + intent.data.host + "/" + url
                    }

                    return url
                }
            } catch (e: IOException) {
                tracker.trackException("ExtractURLAsIphoneActivity", e, false)
            } catch (e: URISyntaxException) {
                tracker.trackException("ExtractURLAsIphoneActivity", e, false)
            }

            return null
        }

        private fun extractURL(body: String): String? {
            val patterns = arrayOf("href=\"(.*\\.pkpass.*?)\"", "window.location = \'(.*\\.pkpass.*?)\'")

            return patterns
                    .map { Pattern.compile(it).matcher(body) }
                    .firstOrNull { it.find() }
                    ?.group(1)
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            if (s == null) {
                val intent = Intent(this@ExtractURLAsIphoneActivity, OpenIphoneWebView::class.java)
                intent.data = getIntent().data
                startActivity(intent)
                tearDown()
                return
            }

            tracker.trackEvent("quirk_fix", "unpack_success", intent.data.host, null)

            val intent = Intent(this@ExtractURLAsIphoneActivity, PassImportActivity::class.java)
            intent.data = Uri.parse(s)

            startActivity(intent)
            tearDown()
        }
    }

    fun tearDown() {
        progressDialog.dismissIfShowing()
        finish()
    }
}
