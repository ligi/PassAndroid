package org.ligi.passandroid.ui

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.app.NotificationCompat
import org.ligi.passandroid.R
import org.ligi.passandroid.model.PassBitmapDefinitions
import org.ligi.passandroid.model.PassStore
import org.ligi.passandroid.model.pass.Pass
import org.threeten.bp.ZonedDateTime
import java.io.File

internal class SearchSuccessCallback(private val context: Context, private val passStore: PassStore, private val foundList: MutableList<Pass>, private val findNotificationBuilder: NotificationCompat.Builder, private val file: File, private val notifyManager: NotificationManager) : UnzipPassController.SuccessCallback {

    override fun call(uuid: String) {

        val pass = passStore.getPassbookForId(uuid)

        val isDuplicate = foundList.any { it.id == uuid }
        if (pass != null && !isDuplicate) {
            foundList.add(pass)
            val iconBitmap = pass.getBitmap(passStore, PassBitmapDefinitions.BITMAP_ICON)
            passStore.classifier.moveToTopic(pass, getInitialTopic(pass))
            if (iconBitmap != null) {
                val bitmap = scale2maxSize(iconBitmap, context.resources.getDimensionPixelSize(R.dimen.finger))
                findNotificationBuilder.setLargeIcon(bitmap)
            }

            val foundString = context.getString(R.string.found_pass, pass.description)
            findNotificationBuilder.setContentTitle(foundString)

            if (foundList.size > 1) {
                val foundMoreString = context.getString(R.string.found__pass, foundList.size - 1)
                findNotificationBuilder.setContentText(foundMoreString)
            } else {
                findNotificationBuilder.setContentText(file.absolutePath)
            }

            val intent = Intent(context, PassViewActivity::class.java)
            intent.putExtra(PassViewActivityBase.EXTRA_KEY_UUID, uuid)
            findNotificationBuilder.setContentIntent(PendingIntent.getActivity(context, SearchPassesIntentService.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT))
            notifyManager.notify(SearchPassesIntentService.FOUND_NOTIFICATION_ID, findNotificationBuilder.build())
        }
    }

    private fun scale2maxSize(bitmap: Bitmap, dimensionPixelSize: Int): Bitmap {
        val scale = dimensionPixelSize.toFloat() / if (bitmap.width > bitmap.height) bitmap.width else bitmap.height
        return Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), false)
    }

    private fun getInitialTopic(pass: Pass): String {
        val passDate = getDateOfPassForComparison(pass)
        if (passDate != null && passDate.isBefore(ZonedDateTime.now())) {
            return context.getString(R.string.topic_archive)
        }
        return context.getString(R.string.topic_new)
    }

    private fun getDateOfPassForComparison(pass: Pass): ZonedDateTime? {
        if (pass.calendarTimespan != null && pass.calendarTimespan!!.from != null) {
            return pass.calendarTimespan!!.from
        } else if (pass.validTimespans != null && pass.validTimespans!!.isNotEmpty() && pass.validTimespans!![0].to != null) {
            return pass.validTimespans!![0].to
        }
        return null
    }
}
