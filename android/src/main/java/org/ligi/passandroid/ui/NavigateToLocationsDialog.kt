package org.ligi.passandroid.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import org.ligi.passandroid.R
import org.ligi.passandroid.model.pass.Pass
import org.ligi.passandroid.model.pass.PassLocation
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


fun Activity.showNavigateToLocationsDialog(pass: Pass, finishOnDone: Boolean) {
    val locations = pass.locations

    if (locations.isEmpty()) {
        done(this, finishOnDone)
    } else if (locations.size == 1) {
        startIntentForLocation(this, locations.first(), pass)
        done(this, finishOnDone)
    } else if (locations.size > 1) {
        val locationDescriptions = arrayOfNulls<String>(locations.size)

        var i = 0
        for (loc in locations) {
            locationDescriptions[i++] = loc.getNameWithFallback(pass)
        }
        AlertDialog.Builder(this).setTitle(this.getString(R.string.choose_location))
                .setItems(locationDescriptions) { _, which ->
                    startIntentForLocation(this, locations[which], pass)
                    done(this, finishOnDone)
                }
                .show()

    }
}

private fun done(activity: Activity, finishOnDone: Boolean) {
    if (finishOnDone) {
        activity.finish()
    }
}

private fun startIntentForLocation(activity: Activity, location: PassLocation, pass: Pass) {
    val i = Intent(Intent.ACTION_VIEW)

    val description = getEncodedDescription(location, pass)

    val latAndLonStr = location.getCommaSeparated()
    i.data = Uri.parse("geo:$latAndLonStr?q=$latAndLonStr($description)")
    try {
        activity.startActivity(i)
    } catch (e: ActivityNotFoundException) {
        i.data = Uri.parse("http://maps.google.com/?q=$description@$latAndLonStr")
        activity.startActivity(i)
        // TODO also the browser could not be found -> handle
    }

}

private fun getEncodedDescription(location: PassLocation, pass: Pass) = try {
    URLEncoder.encode(location.getNameWithFallback(pass), "UTF-8")
} catch (e1: UnsupportedEncodingException) {
    // OK - no description
    ""
}

